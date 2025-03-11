package com.inghubs.brokerage.service;

import com.inghubs.brokerage.dto.OrderDto;
import com.inghubs.brokerage.dto.request.CreateOrderRequest;
import com.inghubs.brokerage.dto.request.MatchOrdersRequest;
import com.inghubs.brokerage.entity.Order;
import com.inghubs.brokerage.enums.OrderStatus;
import com.inghubs.brokerage.repository.OrderRepository;
import com.inghubs.brokerage.service.strategy.OrderStrategy;
import com.inghubs.brokerage.service.strategy.OrderStrategyFactory;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

  public static final String TRY = "TRY";

  private final OrderRepository orderRepository;
  private final OrderStrategyFactory orderStrategyFactory;
  private final CustomerService customerService;

  public OrderDto createOrder(CreateOrderRequest requestDto) {
    customerService.checkCustomerAndPermission(requestDto.customerId());
    log.info("Creating order for customer {}", requestDto.customerId());
    if (TRY.equalsIgnoreCase(requestDto.assetName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot directly buy/sell TRY");
    }
    OrderStrategy strategy = orderStrategyFactory.getStrategy(requestDto.orderSide());
    Order createdOrder = strategy.createOrder(requestDto);

    log.info("Order created successfully with id {}", createdOrder.getId());
    return orderToOrderDto(createdOrder);
  }

  public List<OrderDto> getAllOrders(Long customerId, LocalDate startDate, LocalDate endDate) {
    customerService.checkCustomerAndPermission(customerId);
    log.info("Retrieving orders for customer {}", customerId);
    List<Order> orders;
    if (startDate != null && endDate != null) {
      LocalDateTime startDateTime = startDate.atStartOfDay();
      LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
      orders =
          orderRepository.findByCustomerIdAndCreateDateBetween(
              customerId, startDateTime, endDateTime);
    } else {
      orders = orderRepository.findByCustomerId(customerId);
    }

    return orders.stream().map(this::orderToOrderDto).toList();
  }

  @Transactional
  public OrderDto cancelOrder(Long orderId) {
    log.info("Cancelling order with id {}", orderId);
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    customerService.checkCustomerAndPermission(order.getCustomerId());
    if (!OrderStatus.PENDING.equals(order.getStatus())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status is not PENDING");
    }

    OrderStrategy strategy = orderStrategyFactory.getStrategy(order.getOrderSide());
    Order cancelledOrder = strategy.cancelOrder(order);

    log.info("Order with id {} cancelled successfully", orderId);
    return orderToOrderDto(cancelledOrder);
  }

  @Transactional
  public List<OrderDto> matchOrders(MatchOrdersRequest ordersRequestDto) {
    log.info(
        "Matching orders: {}", ordersRequestDto.matchAll() ? "all" : ordersRequestDto.orderIds());
    List<Order> pendingOrders =
        ordersRequestDto.matchAll()
            ? orderRepository.findAllByStatus(OrderStatus.PENDING)
            : orderRepository.findByIdInAndStatus(ordersRequestDto.orderIds(), OrderStatus.PENDING);

    for (Order order : pendingOrders) {
      OrderStrategy strategy = orderStrategyFactory.getStrategy(order.getOrderSide());
      strategy.matchOrder(order);
      order.setStatus(OrderStatus.MATCHED);
      log.info("Order {} matched successfully", order.getId());
    }

    return pendingOrders.stream().map(this::orderToOrderDto).toList();
  }

  private OrderDto orderToOrderDto(Order order) {
    return OrderDto.builder()
        .id(order.getId())
        .customerId(order.getCustomerId())
        .assetName(order.getAssetName())
        .orderSide(order.getOrderSide())
        .size(order.getSize())
        .price(order.getPrice())
        .status(order.getStatus())
        .creatDate(order.getCreateDate())
        .build();
  }
}
