package com.inghubs.brokerage.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.inghubs.brokerage.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record RegisterRequest(
    @NotNull @NotBlank String username, @NotNull @NotBlank String password, @NotNull Role role) {}
