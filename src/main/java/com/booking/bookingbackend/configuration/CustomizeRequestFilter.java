package com.booking.bookingbackend.configuration;

import com.booking.bookingbackend.constant.TokenType;
import com.booking.bookingbackend.service.jwt.JwtService;
import com.booking.bookingbackend.service.user.UserInfoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "CUSTOMIZE-REQUEST-FILTER")
public class CustomizeRequestFilter extends OncePerRequestFilter {

  private final UserInfoService userInfoService;
  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    String token = null;
    String username = null;

    // Check if the header starts with "Bearer "
    if (authHeader != null && authHeader.startsWith("Bearer")) {
      token = authHeader.substring(7); // extract token
      username = jwtService.extractUsername(TokenType.ACCESS_TOKEN, token); // extract username
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      log.debug("Load user by username");
      UserDetails userDetails = userInfoService.loadUserByUsername(username);
      if (jwtService.validateToken(TokenType.ACCESS_TOKEN, token, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}
