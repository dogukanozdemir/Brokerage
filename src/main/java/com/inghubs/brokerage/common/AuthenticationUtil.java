package com.inghubs.brokerage.common;

import com.inghubs.brokerage.dto.AuthenticatedCustomer;
import com.inghubs.brokerage.enums.Role;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthenticationUtil {

  public AuthenticatedCustomer getAuthenticatedCustomer() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Object principal = auth.getPrincipal();
    assert principal instanceof AuthenticatedCustomer;
    return ((AuthenticatedCustomer) principal);
  }

  public void checkPermission(Long customerId) {
    AuthenticatedCustomer authenticatedCustomer = getAuthenticatedCustomer();
    if (!Role.ADMIN.equals(authenticatedCustomer.getRole())
        && !Objects.equals(customerId, authenticatedCustomer.getId())) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          "You don't have permission to access other customers' resources");
    }
  }
}
