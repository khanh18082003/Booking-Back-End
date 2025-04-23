package com.booking.bookingbackend.service.authentication;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.constant.TokenType;
import com.booking.bookingbackend.data.dto.request.AuthenticationRequest;
import com.booking.bookingbackend.data.dto.request.LogoutRequest;
import com.booking.bookingbackend.data.dto.request.RefreshTokenRequest;
import com.booking.bookingbackend.data.dto.request.VerificationEmailRequest;
import com.booking.bookingbackend.data.dto.response.AuthenticationResponse;
import com.booking.bookingbackend.data.dto.response.ProfileResponse;
import com.booking.bookingbackend.data.entity.RedisVerificationCode;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.repository.VerificationCodeRepository;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.service.jwt.JwtService;
import com.booking.bookingbackend.service.mail.MailService;
import com.booking.bookingbackend.service.profile.ProfileService;
import com.booking.bookingbackend.service.user.UserInfoService;
import com.booking.bookingbackend.util.SecurityUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
  MailService mailService;
  ProfileService profileService;
  UserInfoService userInfoService;

  @Value("${jwt.expirationDay}")
  @NonFinal
  int expirationDay;

  /**
   * Authenticates a user based on the provided authentication request.
   *
   * @param request the authentication request containing user credentials (email and password)
   * @return an authentication response containing the generated access and refresh tokens
   * @throws AppException if the authentication process fails due to invalid credentials
   */
  @Override
  public AuthenticationResponse authenticate(
      AuthenticationRequest request,
      HttpServletResponse response
  ) throws MessagingException, UnsupportedEncodingException {
    log.info("Authentication request received for email: {}", request.email());
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.email().strip(),
            request.password()
        )
    );

    User user = (User) authentication.getPrincipal();
    if (!user.isActive()) {
      ProfileResponse profileResponse = profileService.findByUserId(user.getId());
      String firstName = profileResponse.getFirstName();
      String lastName = profileResponse.getLastName();
      String name = firstName != null && lastName != null ? firstName + " " + lastName : null;

      log.info("User is not active");
      mailService.sendVerificationEmail(
          user.getEmail(),
          name,
          SecurityUtil.generateVerificationCode()
      );
      throw new AppException(ErrorCode.MESSAGE_USER_NOT_ACTIVE);
    }

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

      // Store refresh token in HttpOnly cookie
      Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
      refreshTokenCookie.setHttpOnly(true);
      refreshTokenCookie.setSecure(true);
      refreshTokenCookie.setPath("/");
      refreshTokenCookie.setMaxAge(expirationDay * 24 * 60 * 60);
      response.addCookie(refreshTokenCookie);

    } else {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }
    return AuthenticationResponse.builder()
        .accessToken(accessToken)
        .build();
  }


  /**
   * Refreshes the access token using the provided refresh token and access token.
   *
   * @param refreshTokenRequest the request containing the current access token
   * @param req                 the HTTP servlet request, used to retrieve cookies
   * @return an AuthenticationResponse containing the new access token
   * @throws AppException if the refresh token is missing, invalid, or the user is not
   *                      authenticated
   */
  @Override
  public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest,
      HttpServletRequest req) {
    // Retrieve the refresh token from cookies
    log.info("Refresh token request received");
    String refreshToken = getRefreshTokenFromCookies(req);
    if (refreshToken == null || refreshToken.isEmpty()) {
      // Throw an exception if the refresh token is not found or empty
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }

    String username = jwtService.extractUsername(TokenType.REFRESH_TOKEN, refreshToken);

    // Load the user by username
    User user = (User) userInfoService.loadUserByUsername(username);

    // Validate the refresh token and access token
    if (!jwtService.validateToken(TokenType.REFRESH_TOKEN, refreshToken, user)
    && !jwtService.extractUsername(TokenType.ACCESS_TOKEN, refreshTokenRequest.accessToken()).equals(username)) {
      // Throw an exception if both tokens are invalid
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }

    // Invalidate the old access token
    invalidateToken(refreshTokenRequest.accessToken(), TokenType.ACCESS_TOKEN);

    // Generate a new access token for the authenticated user
    String newAccessToken = jwtService.generateAccessToken(
        user.getUsername(),
        user.getAuthorities()
    );


    // Return the new access token in the response
    return AuthenticationResponse.builder()
        .accessToken(newAccessToken)
        .build();
  }

  private String getRefreshTokenFromCookies(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("refresh_token".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null; // Return null if the cookie is not found
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
        request.email());
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
   * Logs out a user by invalidating their access and refresh tokens.
   * <p>
   * This method retrieves the refresh token from the cookies in the HTTP request, invalidates the
   * provided access token, and optionally invalidates the refresh token if it is present and not
   * empty.
   *
   * @param logoutRequest the request containing the access token to be invalidated
   * @param req           the HTTP servlet request, used to retrieve cookies containing the refresh
   *                      token
   */
  @Override
  public void logout(LogoutRequest logoutRequest, HttpServletRequest req, HttpServletResponse res) {
    // Retrieve the refresh token from cookies
    String refreshToken = getRefreshTokenFromCookies(req);

    // Invalidate access token
    invalidateToken(logoutRequest.accessToken(), TokenType.ACCESS_TOKEN);

    // Invalidate refresh token if it exists and is not empty
    if (refreshToken != null && !refreshToken.isEmpty()) {
      invalidateToken(refreshToken, TokenType.REFRESH_TOKEN);
      // Remove the refresh_token cookie
      Cookie refreshTokenCookie = new Cookie("refresh_token", null);
      refreshTokenCookie.setHttpOnly(true);
      refreshTokenCookie.setSecure(true); // Set to true in production
      refreshTokenCookie.setPath("/"); // Match the original path
      refreshTokenCookie.setMaxAge(0); // Expire the cookie immediately
      res.addCookie(refreshTokenCookie);
    }
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
