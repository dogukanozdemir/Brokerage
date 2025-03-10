package com.inghubs.brokerage.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.inghubs.brokerage.enums.OrderSide;
import com.inghubs.brokerage.enums.OrderStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record OrderDto(
    Long id,
    Long customerId,
    String assetName,
    OrderSide orderSide,
    Double size,
    Double price,
    OrderStatus status,
    LocalDateTime creatDate) {}
