package com.inghubs.brokerage.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.inghubs.brokerage.dto.request.CreateOrderRequest;
import com.inghubs.brokerage.entity.Asset;
import com.inghubs.brokerage.entity.AssetId;
import com.inghubs.brokerage.entity.Order;
import com.inghubs.brokerage.enums.OrderSide;
import com.inghubs.brokerage.enums.OrderStatus;
import com.inghubs.brokerage.repository.AssetRepository;
import com.inghubs.brokerage.repository.OrderRepository;
import com.inghubs.brokerage.service.strategy.OrderSellStrategyService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class OrderSellStrategyServiceTest {

  @Mock private OrderRepository orderRepository;

  @Mock private AssetRepository assetRepository;

  @InjectMocks private OrderSellStrategyService orderSellStrategyService;

  private final long customerId = 1L;
  private final String assetName = "BTC";
  private final double orderSize = 10.0;
  private final double orderPrice = 5000.0;

  @Test
  void createOrder_Success() {
    CreateOrderRequest request =
        new CreateOrderRequest(customerId, assetName, OrderSide.SELL, orderSize, orderPrice);
    Asset asset =
        Asset.builder()
            .id(new AssetId(customerId, assetName))
            .size(100.0)
            .usableSize(100.0)
            .build();

    when(assetRepository.findById(new AssetId(customerId, assetName)))
        .thenReturn(Optional.of(asset));
    when(assetRepository.save(asset)).thenReturn(asset);

    Order savedOrder =
        Order.builder()
            .id(1L)
            .customerId(customerId)
            .assetName(assetName)
            .orderSide(OrderSide.SELL)
            .status(OrderStatus.PENDING)
            .size(orderSize)
            .price(orderPrice)
            .createDate(LocalDateTime.now())
            .build();
    when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

    Order result = orderSellStrategyService.createOrder(request);

    assertEquals(100.0 - orderSize, asset.getUsableSize(), 0.001);
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(OrderStatus.PENDING, result.getStatus());

    verify(assetRepository).save(asset);
    verify(orderRepository).save(any(Order.class));
  }

  @Test
  void createOrder_InsufficientUsableSize_ThrowsException() {
    CreateOrderRequest request =
        new CreateOrderRequest(customerId, assetName, OrderSide.SELL, orderSize, orderPrice);
    // Asset with insufficient usableSize (e.g. only 5.0 available)
    Asset asset =
        Asset.builder().id(new AssetId(customerId, assetName)).size(100.0).usableSize(5.0).build();

    when(assetRepository.findById(new AssetId(customerId, assetName)))
        .thenReturn(Optional.of(asset));

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> orderSellStrategyService.createOrder(request));
    assertEquals("400 BAD_REQUEST \"Not enough usable size\"", exception.getMessage());

    verify(assetRepository, never()).save(any());
    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  void createOrder_AssetNotFound_ThrowsException() {
    CreateOrderRequest request =
        new CreateOrderRequest(customerId, assetName, OrderSide.SELL, orderSize, orderPrice);
    when(assetRepository.findById(new AssetId(customerId, assetName))).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> orderSellStrategyService.createOrder(request));
    assertEquals("404 NOT_FOUND \"No such asset\"", exception.getMessage());
  }

  @Test
  void cancelOrder_Success() {
    Order order =
        Order.builder()
            .id(1L)
            .customerId(customerId)
            .assetName(assetName)
            .size(orderSize)
            .price(orderPrice)
            .orderSide(OrderSide.SELL)
            .status(OrderStatus.PENDING)
            .createDate(LocalDateTime.now())
            .build();
    Asset asset =
        Asset.builder().id(new AssetId(customerId, assetName)).size(100.0).usableSize(50.0).build();

    when(assetRepository.findById(new AssetId(customerId, assetName)))
        .thenReturn(Optional.of(asset));
    when(assetRepository.save(asset)).thenReturn(asset);

    Order canceledOrder =
        Order.builder()
            .id(1L)
            .customerId(customerId)
            .assetName(assetName)
            .orderSide(OrderSide.SELL)
            .status(OrderStatus.CANCELED)
            .size(orderSize)
            .price(orderPrice)
            .createDate(LocalDateTime.now())
            .build();
    when(orderRepository.save(order)).thenReturn(canceledOrder);

    Order result = orderSellStrategyService.cancelOrder(order);

    // In cancelOrder, asset's usableSize increases by order.size.
    assertEquals(50.0 + orderSize, asset.getUsableSize(), 0.001);
    assertEquals(OrderStatus.CANCELED, result.getStatus());
    verify(assetRepository).save(asset);
    verify(orderRepository).save(order);
  }

  @Test
  void cancelOrder_AssetNotFound_ThrowsException() {
    Order order =
        Order.builder()
            .id(1L)
            .customerId(customerId)
            .assetName(assetName)
            .size(orderSize)
            .price(orderPrice)
            .orderSide(OrderSide.SELL)
            .status(OrderStatus.PENDING)
            .createDate(LocalDateTime.now())
            .build();

    when(assetRepository.findById(new AssetId(customerId, assetName))).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> orderSellStrategyService.cancelOrder(order));
    assertEquals("404 NOT_FOUND \"No such asset found\"", exception.getMessage());
  }

  @Test
  void matchOrder_SoldAssetFound_TryBalanceExists() {

    Order order =
        Order.builder()
            .customerId(customerId)
            .assetName(assetName)
            .size(2.0)
            .price(5000.0) // total = 2 * 5000 = 10000
            .build();

    Asset soldAsset =
        Asset.builder().id(new AssetId(customerId, assetName)).size(20.0).usableSize(20.0).build();

    // Try balance asset is identified by TRY
    Asset tryBalance =
        Asset.builder()
            .id(new AssetId(customerId, OrderSellStrategyService.TRY))
            .size(10000.0)
            .usableSize(10000.0)
            .build();

    when(assetRepository.findById(new AssetId(customerId, assetName)))
        .thenReturn(Optional.of(soldAsset));
    when(assetRepository.findById(new AssetId(customerId, OrderSellStrategyService.TRY)))
        .thenReturn(Optional.of(tryBalance));

    orderSellStrategyService.matchOrder(order);

    // soldAsset size should decrease by order.size
    assertEquals(20.0 - 2.0, soldAsset.getSize(), 0.001);
    // tryBalance size and usableSize should increase by total order value (10000)
    assertEquals(10000.0 + 10000.0, tryBalance.getSize(), 0.001);
    assertEquals(10000.0 + 10000.0, tryBalance.getUsableSize(), 0.001);

    // Verify that saveAll was called with an Iterable containing both assets.
    ArgumentCaptor<Iterable<Asset>> captor = ArgumentCaptor.forClass(Iterable.class);
    verify(assetRepository).saveAll(captor.capture());
    Iterable<Asset> savedAssets = captor.getValue();
    boolean containsTryBalance =
        StreamSupport.stream(savedAssets.spliterator(), false)
            .anyMatch(a -> a.getId().getAssetName().equals(OrderSellStrategyService.TRY));
    boolean containsSoldAsset =
        StreamSupport.stream(savedAssets.spliterator(), false)
            .anyMatch(a -> a.getId().getAssetName().equals(assetName));
    assertTrue(containsTryBalance);
    assertTrue(containsSoldAsset);
  }

  @Test
  void matchOrder_SoldAssetFound_TryBalanceNotFound() {
    // When try balance is not found, orElseGet creates a new asset.
    Order order =
        Order.builder().customerId(customerId).assetName(assetName).size(2.0).price(5000.0).build();

    Asset soldAsset =
        Asset.builder().id(new AssetId(customerId, assetName)).size(20.0).usableSize(20.0).build();

    when(assetRepository.findById(new AssetId(customerId, assetName)))
        .thenReturn(Optional.of(soldAsset));
    when(assetRepository.findById(new AssetId(customerId, OrderSellStrategyService.TRY)))
        .thenReturn(Optional.empty());

    orderSellStrategyService.matchOrder(order);

    ArgumentCaptor<Iterable<Asset>> captor = ArgumentCaptor.forClass(Iterable.class);
    verify(assetRepository).saveAll(captor.capture());
    Iterable<Asset> savedAssets = captor.getValue();

    // Due to the orElseGet, a new try balance is created. (Note: the id is built with
    // order.getAssetName() due to the code.)
    boolean foundNewTryBalance =
        StreamSupport.stream(savedAssets.spliterator(), false)
            .anyMatch(
                a ->
                    !a.getId().getAssetName().equals(OrderSellStrategyService.TRY)
                        && a.getId().getAssetName().equals(assetName)
                        && a.getSize() == 10000.0
                        && a.getUsableSize() == 10000.0);
    boolean containsSoldAsset =
        StreamSupport.stream(savedAssets.spliterator(), false)
            .anyMatch(a -> a.getId().getAssetName().equals(assetName));
    assertTrue(foundNewTryBalance);
    assertTrue(containsSoldAsset);
  }

  @Test
  void matchOrder_SoldAssetNotFound_ThrowsException() {
    Order order =
        Order.builder().customerId(customerId).assetName(assetName).size(2.0).price(5000.0).build();

    when(assetRepository.findById(new AssetId(customerId, assetName))).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> orderSellStrategyService.matchOrder(order));
    assertEquals(
        "404 NOT_FOUND \"Ordered asset was not found to complete the transaction\"",
        exception.getMessage());
  }
}
