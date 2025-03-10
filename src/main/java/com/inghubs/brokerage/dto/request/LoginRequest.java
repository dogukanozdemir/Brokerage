package com.inghubs.brokerage.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record LoginRequest(
    @NotNull @NotBlank String username, @NotNull @NotBlank String password) {}
