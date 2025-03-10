package com.inghubs.brokerage.common;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inghubs.brokerage.dto.AuthenticatedCustomer;
import com.inghubs.brokerage.enums.Role;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthenticationUtilTest {

  @InjectMocks private AuthenticationUtil authenticationUtil;

  @Mock private SecurityContext securityContext;

  @Mock private Authentication authentication;

  @Mock private AuthenticatedCustomer customer;

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
  void testCheckPermission_AsAdmin_ShouldNotThrow() {
    when(customer.getRole()).thenReturn(Role.ADMIN);
    assertDoesNotThrow(() -> authenticationUtil.checkPermission(999L));
  }

  @Test
  void testCheckPermission_WithMatchingCustomerId_ShouldNotThrow() {

    when(customer.getRole()).thenReturn(Role.USER);
    when(customer.getId()).thenReturn(1L);
    assertDoesNotThrow(() -> authenticationUtil.checkPermission(1L));
  }

  @Test
  void testCheckPermission_WithNonMatchingCustomerId_ShouldThrowUnauthorized() {
    when(customer.getRole()).thenReturn(Role.USER);
    when(customer.getId()).thenReturn(1L);

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> authenticationUtil.checkPermission(2L));
    assertEquals(
        "You don't have permission to access other customers' resources", exception.getReason());
  }
}
