package com.inghubs.brokerage.auth;

import com.inghubs.brokerage.dto.AuthenticatedCustomer;
import com.inghubs.brokerage.entity.Customer;
import com.inghubs.brokerage.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerDetailsService implements UserDetailsService {

  private final CustomerRepository customerRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Customer customer =
        customerRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
    return AuthenticatedCustomer.builder()
        .id(customer.getId())
        .username(customer.getUsername())
        .role(customer.getRole())
        .build();
  }
}
