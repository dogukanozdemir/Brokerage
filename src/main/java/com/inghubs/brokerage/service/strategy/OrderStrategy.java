package com.inghubs.brokerage.service.strategy;

import com.inghubs.brokerage.dto.request.CreateOrderRequest;
import com.inghubs.brokerage.entity.Order;

public interface OrderStrategy {

  Order createOrder(CreateOrderRequest requestDto);

  Order cancelOrder(Order order);

  void matchOrder(Order order);
}
