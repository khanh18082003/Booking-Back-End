package com.booking.bookingbackend.service.authentication;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.constant.TokenType;
import com.booking.bookingbackend.data.dto.request.AuthenticationRequest;
import com.booking.bookingbackend.data.dto.request.RefreshTokenRequest;
import com.booking.bookingbackend.data.dto.response.AuthenticationResponse;
import com.booking.bookingbackend.data.entity.RedisInvalidToken;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.repository.InvalidTokenRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.service.jwt.JwtService;
import java.util.Date;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

  UserRepository userRepository;
  AuthenticationManager authenticationManager;
  JwtService jwtService;
  InvalidTokenRepository invalidTokenRepository;

  /**
   * Authenticates a user based on the provided authentication request.
   *
   * @param request the authentication request containing user credentials (email and password)
   * @return an authentication response containing the generated access and refresh tokens
   * @throws AppException if the authentication process fails due to invalid credentials
   */
  @Override
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.email(),
            request.password()
        )
    );

    String accessToken = "";
    String refreshToken = "";

    if (authentication.isAuthenticated()) {
      accessToken = jwtService.generateAccessToken(
          request.email(),
          authentication.getAuthorities()
      );
      refreshToken = jwtService.generateRefreshToken(
          request.email(),
          authentication.getAuthorities()
      );
    } else {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }
    return AuthenticationResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Override
  public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
    if (request.refreshToken() == null || request.refreshToken().isEmpty()) {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }

    if (!jwtService.validateToken(TokenType.REFRESH_TOKEN, request.refreshToken(), user)
        && !jwtService.validateToken(TokenType.ACCESS_TOKEN, request.accessToken(), user)
    ) {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }

    String newAccessToken = jwtService.generateAccessToken(
        user.getUsername(),
        user.getAuthorities()
    );

    invalidateToken(request.accessToken(), TokenType.ACCESS_TOKEN);

    return AuthenticationResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(request.refreshToken()) // Reuse the same refresh token
        .build();
  }

  @Override
  public void logout(String accessToken, String refreshToken) {
    // Invalidate access token
    invalidateToken(accessToken, TokenType.ACCESS_TOKEN);

    // Invalidate refresh token
    if (refreshToken != null && !refreshToken.isEmpty()) {
      invalidateToken(refreshToken, TokenType.REFRESH_TOKEN);
    }

  }

  private void invalidateToken(String token, TokenType tokenType) {
    String jit = jwtService.extractId(tokenType, token);
    Date expireTime = jwtService.extractExpiration(tokenType, token);
    if (jit == null || expireTime == null) {
      log.error("Invalid token");
      return;
    }

    RedisInvalidToken redisInvalidToken = RedisInvalidToken.builder()
        .id(jit)
        .token(token)
        .expireTime(expireTime)
        .build();

    invalidTokenRepository.save(redisInvalidToken);
  }
}
