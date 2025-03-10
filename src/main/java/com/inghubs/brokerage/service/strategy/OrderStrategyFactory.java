package com.inghubs.brokerage.service.strategy;

import com.inghubs.brokerage.enums.OrderSide;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStrategyFactory {
  private final OrderBuyStrategyService buyStrategy;
  private final OrderSellStrategyService sellStrategy;

  public OrderStrategy getStrategy(OrderSide orderSide) {
    if (OrderSide.BUY.equals(orderSide)) {
      return buyStrategy;
    }
    return sellStrategy;
  }
}
