package com.inghubs.brokerage.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.inghubs.brokerage.enums.OrderSide;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record CreateOrderRequest(
    @NotNull Long customerId,
    @NotBlank @NotNull String assetName,
    @NotNull OrderSide orderSide,
    @NotNull @Positive Double size,
    @NotNull @Positive Double price) {}
