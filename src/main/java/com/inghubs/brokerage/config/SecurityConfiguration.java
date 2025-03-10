package com.inghubs.brokerage.config;

import com.inghubs.brokerage.auth.AuthenticationEntry;
import com.inghubs.brokerage.auth.CustomerDetailsService;
import com.inghubs.brokerage.auth.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private final CustomerDetailsService customerDetailsService;

  private final JwtAuthenticationFilter authenticationFilter;

  private final AuthenticationEntry authEntryPoint;

  public SecurityConfiguration(
      CustomerDetailsService customerDetailsService,
      JwtAuthenticationFilter authenticationFilter,
      AuthenticationEntry authEntryPoint) {
    this.customerDetailsService = customerDetailsService;
    this.authenticationFilter = authenticationFilter;
    this.authEntryPoint = authEntryPoint;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/v1/login", "/v1/register")
                    .permitAll()
                    .requestMatchers("/v1/admin/**")
                    .hasAnyAuthority("ADMIN")
                    .anyRequest()
                    .authenticated())
        .userDetailsService(customerDetailsService)
        .exceptionHandling(e -> e.authenticationEntryPoint(authEntryPoint))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
