package com.inghubs.brokerage.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inghubs.brokerage.dto.request.CreateOrderRequest;
import com.inghubs.brokerage.entity.Asset;
import com.inghubs.brokerage.entity.AssetId;
import com.inghubs.brokerage.entity.Order;
import com.inghubs.brokerage.enums.OrderSide;
import com.inghubs.brokerage.enums.OrderStatus;
import com.inghubs.brokerage.repository.AssetRepository;
import com.inghubs.brokerage.repository.OrderRepository;
import com.inghubs.brokerage.service.strategy.OrderBuyStrategyService;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class OrderBuyStrategyServiceTest {

  @Mock private AssetRepository assetRepository;

  @Mock private OrderRepository orderRepository;

  @InjectMocks private OrderBuyStrategyService orderBuyStrategyService;

  private final AssetId tryAssetId = new AssetId(1L, "TRY");
  private final Asset tryAsset =
      Asset.builder().id(tryAssetId).size(10000.0).usableSize(10000.0).build();

  @Test
  void createOrder_ShouldCreateOrderAndDeductTRYBalance() {
    CreateOrderRequest request = new CreateOrderRequest(1L, "BTC", OrderSide.BUY, 1.0, 1000.0);
    when(assetRepository.findById(tryAssetId)).thenReturn(Optional.of(tryAsset));
    Order savedOrder = Order.builder().id(1L).build();
    when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

    Order result = orderBuyStrategyService.createOrder(request);

    assertEquals(1L, result.getId());
    assertEquals(9000.0, tryAsset.getUsableSize());
    verify(assetRepository).save(tryAsset);
    verify(orderRepository).save(any(Order.class));
  }

  @Test
  void createOrder_ShouldThrowIfNotEnoughTRYBalance() {
    CreateOrderRequest request = new CreateOrderRequest(1L, "BTC", OrderSide.BUY, 1.0, 11000.0);
    when(assetRepository.findById(tryAssetId)).thenReturn(Optional.of(tryAsset));

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> orderBuyStrategyService.createOrder(request));
    assertEquals("400 BAD_REQUEST \"Not enough usable size\"", exception.getMessage());
    verify(orderRepository, never()).save(any());
  }

  @Test
  void createOrder_ShouldThrowIfNoTRYAsset() {
    CreateOrderRequest request = new CreateOrderRequest(1L, "BTC", OrderSide.BUY, 1.0, 1000.0);
    when(assetRepository.findById(tryAssetId)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> orderBuyStrategyService.createOrder(request));
    assertEquals(
        "404 NOT_FOUND \"No TRY asset not found, please deposit TRY\"", exception.getMessage());
  }

  @Test
  void cancelOrder_ShouldRestoreTRYBalanceAndCancelOrder() {
    Order order =
        Order.builder()
            .customerId(1L)
            .orderSide(OrderSide.BUY)
            .size(2.0)
            .price(500.0)
            .status(OrderStatus.PENDING)
            .build(); // total 1000 TRY
    when(assetRepository.findById(tryAssetId)).thenReturn(Optional.of(tryAsset));
    Order canceledOrder = Order.builder().id(1L).status(OrderStatus.CANCELED).build();
    when(orderRepository.save(any(Order.class))).thenReturn(canceledOrder);

    Order result = orderBuyStrategyService.cancelOrder(order);

    assertEquals(OrderStatus.CANCELED, result.getStatus());
    assertEquals(11000.0, tryAsset.getUsableSize());
    verify(assetRepository).save(tryAsset);
    verify(orderRepository).save(order);
  }

  @Test
  void cancelOrder_ShouldThrowIfNoTRYAsset() {
    Order order =
        Order.builder()
            .customerId(1L)
            .orderSide(OrderSide.BUY)
            .size(2.0)
            .price(500.0)
            .build(); // total 1000 TRY
    when(assetRepository.findById(tryAssetId)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> orderBuyStrategyService.cancelOrder(order));
    assertEquals("404 NOT_FOUND \"No TRY assets found\"", exception.getMessage());
  }

  @Test
  void matchOrder_ShouldAdjustBalancesCorrectly() {
    Order order = Order.builder().customerId(1L).assetName("BTC").size(1.0).price(1000.0).build();
    Asset btcAsset = Asset.builder().id(new AssetId(1L, "BTC")).size(0.0).usableSize(0.0).build();
    when(assetRepository.findById(tryAssetId)).thenReturn(Optional.of(tryAsset));
    when(assetRepository.findById(new AssetId(1L, "BTC"))).thenReturn(Optional.of(btcAsset));

    orderBuyStrategyService.matchOrder(order);

    assertEquals(1.0, btcAsset.getSize());
    assertEquals(1.0, btcAsset.getUsableSize());
    assertEquals(9000.0, tryAsset.getSize());
    verify(assetRepository).saveAll(List.of(tryAsset, btcAsset));
  }

  @Test
  void matchOrder_ShouldCreateNewAssetIfNotExists() {
    Order order = Order.builder().customerId(1L).assetName("ETH").size(2.0).price(500.0).build();
    when(assetRepository.findById(tryAssetId)).thenReturn(Optional.of(tryAsset));
    when(assetRepository.findById(new AssetId(1L, "ETH"))).thenReturn(Optional.empty());

    orderBuyStrategyService.matchOrder(order);

    verify(assetRepository)
        .saveAll(
            argThat(
                assets ->
                    StreamSupport.stream(assets.spliterator(), false)
                        .anyMatch(
                            a -> "ETH".equals(a.getId().getAssetName()) && a.getSize() == 2.0)));
  }

  @Test
  void matchOrder_ShouldThrowIfNoTRYAssetFound() {
    Order order = Order.builder().customerId(1L).assetName("BTC").size(1.0).price(1000.0).build();
    when(assetRepository.findById(tryAssetId)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> orderBuyStrategyService.matchOrder(order));
    assertEquals(
        "404 NOT_FOUND \"No TRY assets found to complete the transaction\"",
        exception.getMessage());
  }
}
