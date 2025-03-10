package com.inghubs.brokerage.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse {

  private LocalDateTime time;
  private String error;
  private Map<String, String> errors;
}
