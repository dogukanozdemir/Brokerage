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
public class OrderSellStrategyService implements OrderStrategy {

  public static final String TRY = "TRY";

  private final OrderRepository orderRepository;
  private final AssetRepository assetRepository;

  @Override
  public Order createOrder(CreateOrderRequest requestDto) {
    Asset tryAsset =
        assetRepository
            .findById(new AssetId(requestDto.customerId(), requestDto.assetName()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such asset"));

    if (tryAsset.getUsableSize() < requestDto.size()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough usable size");
    }

    tryAsset.setUsableSize(tryAsset.getUsableSize() - requestDto.size());
    assetRepository.save(tryAsset);

    Order order =
        Order.builder()
            .customerId(requestDto.customerId())
            .orderSide(OrderSide.SELL)
            .status(OrderStatus.PENDING)
            .assetName(requestDto.assetName())
            .size(requestDto.size())
            .price(requestDto.price())
            .createDate(LocalDateTime.now())
            .build();

    return orderRepository.save(order);
  }

  @Override
  public Order cancelOrder(Order order) {
    Asset asset =
        assetRepository
            .findById(new AssetId(order.getCustomerId(), order.getAssetName()))
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such asset found"));
    asset.setUsableSize(asset.getUsableSize() + order.getSize());
    assetRepository.save(asset);
    order.setStatus(OrderStatus.CANCELED);
    return orderRepository.save(order);
  }

  @Override
  public void matchOrder(Order order) {
    Asset soldAsset =
        assetRepository
            .findById(new AssetId(order.getCustomerId(), order.getAssetName()))
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ordered asset was not found to complete the transaction"));
    Asset tryBalance =
        assetRepository
            .findById(new AssetId(order.getCustomerId(), TRY))
            .orElseGet(
                () ->
                    Asset.builder()
                        .id(new AssetId(order.getCustomerId(), order.getAssetName()))
                        .size(0.0)
                        .usableSize(0.0)
                        .build());

    double totalOrderSize = order.getSize() * order.getPrice();
    soldAsset.setSize(soldAsset.getSize() - order.getSize());
    tryBalance.setSize(tryBalance.getSize() + totalOrderSize);
    tryBalance.setUsableSize(tryBalance.getUsableSize() + totalOrderSize);
    assetRepository.saveAll(List.of(tryBalance, soldAsset));
  }
}
