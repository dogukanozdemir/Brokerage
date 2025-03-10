package com.inghubs.brokerage.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.inghubs.brokerage.auth.jwt.JwtService;
import com.inghubs.brokerage.dto.request.LoginRequest;
import com.inghubs.brokerage.dto.request.RegisterRequest;
import com.inghubs.brokerage.dto.response.AuthenticationResponse;
import com.inghubs.brokerage.entity.Customer;
import com.inghubs.brokerage.enums.Role;
import com.inghubs.brokerage.repository.CustomerRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @InjectMocks private AuthenticationService authenticationService;

  @Mock private CustomerRepository customerRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtService jwtService;

  @Mock private AuthenticationManager authenticationManager;

  @Test
  void testLogin_Success() {
    String username = "user1";
    String password = "password";
    String token = "dummy-token";

    LoginRequest loginRequest = new LoginRequest(username, password);
    Customer customer = Customer.builder().username(username).build();

    when(customerRepository.findByUsername(username)).thenReturn(Optional.of(customer));
    when(jwtService.generateToken(customer)).thenReturn(token);

    AuthenticationResponse response = authenticationService.login(loginRequest);

    assertNotNull(response);
    assertEquals(username, response.username());
    assertEquals(token, response.token());
  }

  @Test
  void testLogin_CustomerNotFound() {

    String username = "userNotFound";
    String password = "password";
    LoginRequest loginRequest = new LoginRequest(username, password);

    when(customerRepository.findByUsername(username)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> authenticationService.login(loginRequest));
    assertEquals(String.format("Customer with name %s not found", username), exception.getReason());
  }

  @Test
  void testRegister_Success() {

    String username = "newUser";
    String rawPassword = "rawPassword";
    String encodedPassword = "encodedPassword";
    String token = "dummy-token";

    Role role = Role.USER;

    RegisterRequest registerRequest = new RegisterRequest(username, rawPassword, role);

    when(customerRepository.findByUsername(username)).thenReturn(Optional.empty());
    when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

    when(jwtService.generateToken(any(Customer.class))).thenReturn(token);

    AuthenticationResponse response = authenticationService.register(registerRequest);

    assertNotNull(response);
    assertEquals(username, response.username());
    assertEquals(token, response.token());
    verify(customerRepository).save(any(Customer.class));
  }

  @Test
  void testRegister_CustomerAlreadyExists() {

    String username = "existingUser";
    String rawPassword = "rawPassword";
    Role role = Role.USER;

    RegisterRequest registerRequest = new RegisterRequest(username, rawPassword, role);

    Customer existingCustomer = Customer.builder().username(username).build();
    when(customerRepository.findByUsername(username)).thenReturn(Optional.of(existingCustomer));

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> authenticationService.register(registerRequest));
    assertEquals("Customer with this username already exists", exception.getReason());
  }
}
