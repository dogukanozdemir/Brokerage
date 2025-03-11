package com.inghubs.brokerage.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.inghubs.brokerage.dto.OrderDto;
import com.inghubs.brokerage.dto.request.CreateOrderRequest;
import com.inghubs.brokerage.dto.request.MatchOrdersRequest;
import com.inghubs.brokerage.entity.Order;
import com.inghubs.brokerage.enums.OrderSide;
import com.inghubs.brokerage.enums.OrderStatus;
import com.inghubs.brokerage.repository.OrderRepository;
import com.inghubs.brokerage.service.strategy.OrderStrategy;
import com.inghubs.brokerage.service.strategy.OrderStrategyFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock private OrderRepository orderRepository;

  @Mock private OrderStrategyFactory orderStrategyFactory;

  @Mock private CustomerService customerService;

  @InjectMocks private OrderService orderService;

  @Mock private OrderStrategy orderStrategy;

  private final Order order =
      Order.builder()
          .id(1L)
          .customerId(1L)
          .assetName("USD")
          .orderSide(OrderSide.BUY)
          .size(10.0)
          .price(100.0)
          .status(OrderStatus.PENDING)
          .createDate(LocalDateTime.now())
          .build();

  @Test
  void createOrder_ShouldReturnOrderDto() {
    CreateOrderRequest request = new CreateOrderRequest(1L, "USD", OrderSide.BUY, 10.0, 100.0);
    when(orderStrategyFactory.getStrategy(OrderSide.BUY)).thenReturn(orderStrategy);
    when(orderStrategy.createOrder(request)).thenReturn(order);
    doNothing().when(customerService).checkCustomerAndPermission(1L);

    OrderDto result = orderService.createOrder(request);

    assertEquals(order.getId(), result.id());
    assertEquals(order.getAssetName(), result.assetName());
    verify(customerService).checkCustomerAndPermission(1L);
  }

  @Test
  void createOrder_ShouldThrowExceptionWhenTRYAsset() {
    CreateOrderRequest request = new CreateOrderRequest(1L, "TRY", OrderSide.BUY, 10.0, 100.0);
    doNothing().when(customerService).checkCustomerAndPermission(1L);

    assertThrows(ResponseStatusException.class, () -> orderService.createOrder(request));
  }

  @Test
  void getAllOrdersWithoutDates_ShouldReturnOrders() {
    when(orderRepository.findByCustomerId(1L)).thenReturn(List.of(order));
    doNothing().when(customerService).checkCustomerAndPermission(1L);

    List<OrderDto> result = orderService.getAllOrders(1L, null, null);

    assertEquals(1, result.size());
    assertEquals(order.getId(), result.getFirst().id());
  }

  @Test
  void getAllOrdersWithDates_ShouldReturnOrders() {
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 12, 31);
    when(orderRepository.findByCustomerIdAndCreateDateBetween(any(), any(), any()))
        .thenReturn(List.of(order));
    doNothing().when(customerService).checkCustomerAndPermission(1L);

    List<OrderDto> result = orderService.getAllOrders(1L, startDate, endDate);

    assertEquals(1, result.size());
    assertEquals(order.getId(), result.getFirst().id());
  }

  @Test
  void cancelOrder_ShouldReturnCancelledOrderDto() {
    Order cancelledOrder = Order.builder().id(1L).status(OrderStatus.CANCELED).build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderStrategyFactory.getStrategy(OrderSide.BUY)).thenReturn(orderStrategy);
    when(orderStrategy.cancelOrder(order)).thenReturn(cancelledOrder);
    doNothing().when(customerService).checkCustomerAndPermission(1L);

    OrderDto result = orderService.cancelOrder(1L);

    assertEquals(OrderStatus.CANCELED, result.status());
    verify(customerService).checkCustomerAndPermission(1L);
  }

  @Test
  void cancelOrder_ShouldThrowNotFoundException() {
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResponseStatusException.class, () -> orderService.cancelOrder(1L));
  }

  @Test
  void cancelOrder_ShouldThrowBadRequestWhenStatusNotPending() {
    Order nonPendingOrder =
        Order.builder().id(1L).customerId(1L).status(OrderStatus.MATCHED).build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(nonPendingOrder));
    doNothing().when(customerService).checkCustomerAndPermission(1L);

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> orderService.cancelOrder(1L));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertTrue(
        Objects.requireNonNull(exception.getReason()).contains("Order status is not PENDING"));
  }

  @Test
  void matchOrdersWithAll_ShouldReturnMatchedOrders() {
    MatchOrdersRequest request = new MatchOrdersRequest(null, true);
    when(orderRepository.findAllByStatus(OrderStatus.PENDING)).thenReturn(List.of(order));
    when(orderStrategyFactory.getStrategy(OrderSide.BUY)).thenReturn(orderStrategy);
    doNothing().when(orderStrategy).matchOrder(order);

    List<OrderDto> result = orderService.matchOrders(request);

    assertEquals(1, result.size());
    assertEquals(order.getId(), result.getFirst().id());
  }

  @Test
  void matchOrdersWithSpecificIds_ShouldReturnMatchedOrders() {
    MatchOrdersRequest request = new MatchOrdersRequest(List.of(1L), false);
    when(orderRepository.findByIdInAndStatus(List.of(1L), OrderStatus.PENDING))
        .thenReturn(List.of(order));
    when(orderStrategyFactory.getStrategy(OrderSide.BUY)).thenReturn(orderStrategy);
    doNothing().when(orderStrategy).matchOrder(order);

    List<OrderDto> result = orderService.matchOrders(request);

    assertEquals(1, result.size());
    assertEquals(order.getId(), result.getFirst().id());
  }
}
