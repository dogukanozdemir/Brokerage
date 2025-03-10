package com.inghubs.brokerage.service.strategy;

import com.inghubs.brokerage.dto.request.CreateOrderRequest;
import com.inghubs.brokerage.entity.Asset;
import com.inghubs.brokerage.entity.AssetId;
import com.inghubs.brokerage.entity.Order;
import com.inghubs.brokerage.enums.OrderSide;
import com.inghubs.brokerage.enums.OrderStatus;
import com.inghubs.brokerage.repository.AssetRepository;
import com.inghubs.brokerage.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrderBuyStrategyService implements OrderStrategy {

  public static final String TRY = "TRY";

  private final AssetRepository assetRepository;
  private final OrderRepository orderRepository;

  @Override
  public Order createOrder(CreateOrderRequest requestDto) {
    double totalOrderSize = requestDto.size() * requestDto.price();
    Asset tryBalance =
        assetRepository
            .findById(new AssetId(requestDto.customerId(), TRY))
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No TRY asset not found, please deposit TRY"));

    if (tryBalance.getUsableSize() < totalOrderSize) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough usable size");
    }

    tryBalance.setUsableSize(tryBalance.getUsableSize() - totalOrderSize);
    assetRepository.save(tryBalance);

    Order order =
        Order.builder()
            .customerId(requestDto.customerId())
            .orderSide(OrderSide.BUY)
            .assetName(requestDto.assetName())
            .status(OrderStatus.PENDING)
            .size(requestDto.size())
            .price(requestDto.price())
            .createDate(LocalDateTime.now())
            .build();

    return orderRepository.save(order);
  }

  @Override
  public Order cancelOrder(Order order) {
    double totalOrderSize = order.getSize() * order.getPrice();
    Asset tryAsset =
        assetRepository
            .findById(new AssetId(order.getCustomerId(), TRY))
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No TRY assets found"));

    tryAsset.setUsableSize(tryAsset.getUsableSize() + totalOrderSize);
    assetRepository.save(tryAsset);
    order.setStatus(OrderStatus.CANCELED);
    return orderRepository.save(order);
  }

  @Override
  public void matchOrder(Order order) {
    Asset tryBalance =
        assetRepository
            .findById(new AssetId(order.getCustomerId(), TRY))
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No TRY assets found to complete the transaction"));

    Asset boughtAsset =
        assetRepository
            .findById(new AssetId(order.getCustomerId(), order.getAssetName()))
            .orElseGet(
                () ->
                    Asset.builder()
                        .id(new AssetId(order.getCustomerId(), order.getAssetName()))
                        .size(0.0)
                        .usableSize(0.0)
                        .build());

    double totalOrderSize = order.getSize() * order.getPrice();
    boughtAsset.setSize(boughtAsset.getSize() + order.getSize());
    boughtAsset.setUsableSize(boughtAsset.getUsableSize() + order.getSize());
    tryBalance.setSize(tryBalance.getSize() - totalOrderSize);
    assetRepository.saveAll(List.of(tryBalance, boughtAsset));
  }
}
