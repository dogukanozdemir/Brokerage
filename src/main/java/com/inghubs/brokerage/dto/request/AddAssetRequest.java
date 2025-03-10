package com.inghubs.brokerage.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record AddAssetRequest(
    @NotNull Long customerId,
    @NotNull @NotBlank String assetName,
    @NotNull @Positive Double size) {}
