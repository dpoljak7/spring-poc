package com.example.demo.filters;

import com.example.demo.oauth2.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    log.info("OAuth2 authentication started");
    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      log.error("OAuth2: missing or invalid Authorization header. Authorization={}", authHeader);
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(7);
    Claims claims = jwtUtil.extractAllClaims(token);
    String username = claims.getSubject();

    if (username != null
        && SecurityContextHolder.getContext().getAuthentication() == null
        && jwtUtil.isTokenValid(token, username)) {
      List<String> roles = claims.get("roles", List.class);
      List<GrantedAuthority> authorities =
          roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

      UserDetails userDetails = new User(username, "", authorities);
      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    log.info("OAuth2 authentication successful");
    filterChain.doFilter(request, response);
  }
}
