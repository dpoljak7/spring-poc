package com.example.demo.config;

import com.example.demo.filters.JwtAuthenticationFilter;
import com.example.demo.oauth2.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  @Value("${security.oauth2.unprotected-endpoints}")
  private String[] unprotectedEndpoints;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtUtil jwtUtil, AuthenticationManager authenticationManager) throws Exception {
    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);

    http.csrf(csrf -> csrf.disable()) // used for authentication which relies on cookies (session-based)
    .authorizeHttpRequests(auth -> auth
          .requestMatchers(unprotectedEndpoints).permitAll() // Allow without authentication
          .requestMatchers("/v1/probe/**").authenticated()
          .anyRequest().authenticated()) // Require authentication for other endpoints
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    // Provide the AuthenticationManager to Spring Security
    return authenticationConfiguration.getAuthenticationManager();
  }

}
