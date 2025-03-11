package com.inghubs.brokerage.service;

import com.inghubs.brokerage.common.AuthenticationUtil;
import com.inghubs.brokerage.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final AuthenticationUtil authenticationUtil;

  public void checkCustomerAndPermission(Long id) {
    if (!authenticationUtil.doesCustomerHavePermission(id)) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          "You don't have permission to access other customers' resources");
    }
    if (!customerRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer does not exist");
    }
  }
}
