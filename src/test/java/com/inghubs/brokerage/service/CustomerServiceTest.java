package com.inghubs.brokerage.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inghubs.brokerage.common.AuthenticationUtil;
import com.inghubs.brokerage.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock private CustomerRepository customerRepository;

  @Mock private AuthenticationUtil authenticationUtil;

  @InjectMocks private CustomerService customerService;

  private Long customerId;

  @BeforeEach
  void setUp() {
    customerId = 1L;
  }

  @Test
  void testCheckCustomerAndPermission_NoPermission_ShouldThrowUnauthorized() {

    when(authenticationUtil.doesCustomerHavePermission(customerId)).thenReturn(false);

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> customerService.checkCustomerAndPermission(customerId));

    assertEquals(
        "You don't have permission to access other customers' resources", exception.getReason());

    verify(authenticationUtil, times(1)).doesCustomerHavePermission(customerId);
    verify(customerRepository, never()).existsById(anyLong());
  }

  @Test
  void testCheckCustomerAndPermission_HasPermissionButCustomerDoesNotExist_ShouldThrowBadRequest() {

    when(authenticationUtil.doesCustomerHavePermission(customerId)).thenReturn(true);
    when(customerRepository.existsById(customerId)).thenReturn(false);

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> customerService.checkCustomerAndPermission(customerId));

    assertEquals("Customer does not exist", exception.getReason());

    verify(authenticationUtil, times(1)).doesCustomerHavePermission(customerId);
    verify(customerRepository, times(1)).existsById(customerId);
  }

  @Test
  void testCheckCustomerAndPermission_HasPermissionAndCustomerExists_ShouldNotThrow() {

    when(authenticationUtil.doesCustomerHavePermission(customerId)).thenReturn(true);
    when(customerRepository.existsById(customerId)).thenReturn(true);

    assertDoesNotThrow(() -> customerService.checkCustomerAndPermission(customerId));

    verify(authenticationUtil, times(1)).doesCustomerHavePermission(customerId);
    verify(customerRepository, times(1)).existsById(customerId);
  }
}
