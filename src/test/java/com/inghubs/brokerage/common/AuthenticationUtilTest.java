package com.inghubs.brokerage.common;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inghubs.brokerage.dto.AuthenticatedCustomer;
import com.inghubs.brokerage.entity.Customer;
import com.inghubs.brokerage.enums.Role;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuthenticationUtilTest {

  @InjectMocks private AuthenticationUtil authenticationUtil;

  @Mock private SecurityContext securityContext;

  @Mock private Authentication authentication;

  @Mock private Customer customer;

  @BeforeEach
  public void setUp() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(customer);
  }

  @AfterEach
  public void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void testGetAuthenticatedCustomer_ReturnsExpectedAuthenticatedCustomer() {
    Long id = 1L;
    String username = "testUser";
    String password = "secret";
    Role role = Role.USER;
    when(customer.getId()).thenReturn(id);
    when(customer.getUsername()).thenReturn(username);
    when(customer.getPassword()).thenReturn(password);
    when(customer.getRole()).thenReturn(role);

    AuthenticatedCustomer result = authenticationUtil.getAuthenticatedCustomer();

    assertNotNull(result);
    assertEquals(id, result.getId());
    assertEquals(username, result.getUsername());
    assertEquals(password, result.getPassword());
    assertEquals(role, result.getRole());
  }

  @Test
  void testDoesCustomerHavePermission_AsAdmin_ShouldReturnTrue() {
    when(customer.getRole()).thenReturn(Role.ADMIN);
    when(customer.getId()).thenReturn(1L); // Irrelevant for admin but safe to mock

    boolean hasPermission = authenticationUtil.doesCustomerHavePermission(999L);

    assertTrue(hasPermission);
  }

  @Test
  void testDoesCustomerHavePermission_WithMatchingCustomerId_ShouldReturnTrue() {
    when(customer.getRole()).thenReturn(Role.USER);
    when(customer.getId()).thenReturn(1L);

    boolean hasPermission = authenticationUtil.doesCustomerHavePermission(1L);

    assertTrue(hasPermission);
  }

  @Test
  void testDoesCustomerHavePermission_WithNonMatchingCustomerId_ShouldReturnFalse() {
    when(customer.getRole()).thenReturn(Role.USER);
    when(customer.getId()).thenReturn(1L);

    boolean hasPermission = authenticationUtil.doesCustomerHavePermission(2L);

    assertFalse(hasPermission);
  }
}
