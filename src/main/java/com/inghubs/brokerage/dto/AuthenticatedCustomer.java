package com.inghubs.brokerage.dto;

import com.inghubs.brokerage.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticatedCustomer {

  private Long id;
  private String username;
  private String password;
  private Role role;
}
