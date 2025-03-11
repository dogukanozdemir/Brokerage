package com.inghubs.brokerage.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.brokerage.exception.dto.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationEntry implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

    ExceptionResponse exceptionResponse =
        ExceptionResponse.builder()
            .time(LocalDateTime.now())
            .error("Invalid token or credentials")
            .build();

    response.getOutputStream().println(objectMapper.writeValueAsString(exceptionResponse));
  }
}
