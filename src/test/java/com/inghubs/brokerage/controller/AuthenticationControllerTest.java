package com.inghubs.brokerage.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.brokerage.dto.request.LoginRequest;
import com.inghubs.brokerage.dto.request.RegisterRequest;
import com.inghubs.brokerage.dto.response.AuthenticationResponse;
import com.inghubs.brokerage.enums.Role;
import com.inghubs.brokerage.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AuthenticationService authenticationService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void testLogin() throws Exception {
    LoginRequest loginRequest = new LoginRequest("dogukan", "password");
    AuthenticationResponse authResponse =
        AuthenticationResponse.builder().username("dogukan").token("dummyToken").build();

    when(authenticationService.login(loginRequest)).thenReturn(authResponse);

    mockMvc
        .perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("dogukan"))
        .andExpect(jsonPath("$.token").value("dummyToken"));
  }

  @Test
  void testLogin_BadCredentials() throws Exception {
    LoginRequest loginRequest = new LoginRequest("dogukan", "wrongPassword");

    when(authenticationService.login(loginRequest))
        .thenThrow(new BadCredentialsException("Bad credentials"));

    mockMvc
        .perform(
            post("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").value("Invalid username or password"));
  }

  @Test
  void testRegister() throws Exception {
    RegisterRequest registerRequest = new RegisterRequest("newUser", "password", Role.USER);
    AuthenticationResponse authResponse =
        AuthenticationResponse.builder().username("newUser").token("newDummyToken").build();

    when(authenticationService.register(any(RegisterRequest.class))).thenReturn(authResponse);

    mockMvc
        .perform(
            post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("newUser"))
        .andExpect(jsonPath("$.token").value("newDummyToken"));
  }
}
