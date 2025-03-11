package com.inghubs.brokerage.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.brokerage.exception.dto.ExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {

    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

    ExceptionResponse exceptionResponse =
        ExceptionResponse.builder()
            .time(LocalDateTime.now())
            .error("You don't have permission to access this resource")
            .build();

    response.getOutputStream().println(objectMapper.writeValueAsString(exceptionResponse));
  }
}
