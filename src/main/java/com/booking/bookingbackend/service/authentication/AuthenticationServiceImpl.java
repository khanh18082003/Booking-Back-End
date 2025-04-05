package com.booking.bookingbackend.service.authentication;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.constant.TokenType;
import com.booking.bookingbackend.data.dto.request.AuthenticationRequest;
import com.booking.bookingbackend.data.dto.request.RefreshTokenRequest;
import com.booking.bookingbackend.data.dto.request.VerificationEmailRequest;
import com.booking.bookingbackend.data.dto.response.AuthenticationResponse;
import com.booking.bookingbackend.data.entity.RedisVerificationCode;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.repository.VerificationCodeRepository;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.service.jwt.JwtService;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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

  AuthenticationManager authenticationManager;
  JwtService jwtService;
  RedisTemplate<String, Object> redisTemplate;
  VerificationCodeRepository verificationCodeRepository;

  /**
   * Authenticates a user based on the provided authentication request.
   *
   * @param request the authentication request containing user credentials (email and password)
   * @return an authentication response containing the generated access and refresh tokens
   * @throws AppException if the authentication process fails due to invalid credentials
   */
  @Override
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    log.info("Authentication request received for email: {}", request.email());
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.email(),
            request.password()
        )
    );

    String accessToken;
    String refreshToken;

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

  /**
   * Refreshes the access token using the provided refresh token and access token.
   *
   * @param request the refresh token request containing the refresh token and access token
   * @return an authentication response containing the new access token and the reused refresh token
   * @throws AppException if the refresh token is invalid, expired, or authentication fails
   */
  @Override
  public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
    log.info("Refresh token request received for refresh token: {}", request.refreshToken());

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

  /**
   * Logs out a user by invalidating their access and refresh tokens.
   *
   * @param accessToken  the access token to be invalidated
   * @param refreshToken the refresh token to be invalidated; can be null or empty
   */
  @Override
  public void logout(String accessToken, String refreshToken) {
    // Invalidate access token
    invalidateToken(accessToken, TokenType.ACCESS_TOKEN);

    // Invalidate refresh token
    if (refreshToken != null && !refreshToken.isEmpty()) {
      invalidateToken(refreshToken, TokenType.REFRESH_TOKEN);
    }

  }

  /**
   * Verifies the validity of the provided token. Checks if the token is non-null, non-empty, and
   * exists in the verificationCodeRepository with a valid expiration time.
   *
   * @param request the token to be verified. Must be a non-null and non-empty string. If the token
   *                is invalid, expired, or null/empty, an error is logged and an AppException is
   *                thrown with an appropriate error code.
   */
  @Override
  public void verifyTokenEmail(VerificationEmailRequest request) {
    if (request.code() == null || request.code().isEmpty()) {
      log.error("Token is empty");
      return;
    }
    Optional<RedisVerificationCode> redisCodeOpt = verificationCodeRepository.findById(
        request.userId());
    if (redisCodeOpt.isEmpty()) {
      log.error("Token not found");
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }

    RedisVerificationCode redisCode = redisCodeOpt.get();
    if (!redisCode.getCode().equals(request.code())) {
      log.error("Token invalid");
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }
    log.info("Token verified successfully");
  }

  /**
   * Invalidates a given token by extracting its ID and expiration time, then saving the relevant
   * information into a repository for tracking invalid tokens.
   *
   * @param token     the token to be invalidated
   * @param tokenType the type of the token being invalidated
   */
  private void invalidateToken(String token, TokenType tokenType) {
    String jit = jwtService.extractId(tokenType, token);
    Date expireTime = jwtService.extractExpiration(tokenType, token);
    if (jit == null || expireTime == null) {
      log.error("Invalid token");
      return;
    }

    long ttlSeconds = Duration.between(
        Instant.now(),
        expireTime.toInstant()
    ).getSeconds();
    if (ttlSeconds <= 0) {
      return; // token đã hết hạn rồi, không cần lưu
    }

    String key = "invalid_token:" + jit;
    redisTemplate.opsForValue().set(key, token, ttlSeconds, TimeUnit.SECONDS);
  }


}
