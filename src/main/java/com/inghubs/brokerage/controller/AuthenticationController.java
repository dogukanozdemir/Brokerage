package com.inghubs.brokerage.controller;

import com.inghubs.brokerage.dto.request.LoginRequest;
import com.inghubs.brokerage.dto.request.RegisterRequest;
import com.inghubs.brokerage.dto.response.AuthenticationResponse;
import com.inghubs.brokerage.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(
      @RequestBody @Valid LoginRequest loginRequest) {
    return ResponseEntity.ok(authenticationService.login(loginRequest));
  }

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody @Valid RegisterRequest registerRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(authenticationService.register(registerRequest));
  }
}
