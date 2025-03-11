package com.inghubs.brokerage.common;

import com.inghubs.brokerage.dto.AuthenticatedCustomer;
import com.inghubs.brokerage.entity.Customer;
import com.inghubs.brokerage.enums.Role;
import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtil {

  public AuthenticatedCustomer getAuthenticatedCustomer() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Object principal = auth.getPrincipal();
    assert principal instanceof Customer;
    Customer customer = (Customer) principal;
    return AuthenticatedCustomer.builder()
        .id(customer.getId())
        .username(customer.getUsername())
        .password(customer.getPassword())
        .role(customer.getRole())
        .build();
  }

  public boolean doesCustomerHavePermission(Long customerId) {
    AuthenticatedCustomer authenticatedCustomer = getAuthenticatedCustomer();
    return Role.ADMIN.equals(authenticatedCustomer.getRole())
        || Objects.equals(customerId, authenticatedCustomer.getId());
  }
}
