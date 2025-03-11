package com.inghubs.brokerage.service;

import com.inghubs.brokerage.auth.jwt.JwtService;
import com.inghubs.brokerage.dto.request.LoginRequest;
import com.inghubs.brokerage.dto.request.RegisterRequest;
import com.inghubs.brokerage.dto.response.AuthenticationResponse;
import com.inghubs.brokerage.entity.Customer;
import com.inghubs.brokerage.repository.CustomerRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse login(LoginRequest loginRequest) {
    String username = loginRequest.username();
    Customer customer =
        customerRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Customer with name %s not found", username)));

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, loginRequest.password()));

    String token = jwtService.generateToken(customer);
    return AuthenticationResponse.builder().username(username).token(token).build();
  }

  public AuthenticationResponse register(RegisterRequest registerRequest) {
    String username = registerRequest.username();
    Optional<Customer> existingCustomer = customerRepository.findByUsername(username);
    if (existingCustomer.isPresent()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Customer with this username already exists");
    }
    Customer customer =
        Customer.builder()
            .username(username)
            .password(passwordEncoder.encode(registerRequest.password()))
            .role(registerRequest.role())
            .build();
    customerRepository.save(customer);

    String token = jwtService.generateToken(customer);
    return AuthenticationResponse.builder().username(username).token(token).build();
  }
}
