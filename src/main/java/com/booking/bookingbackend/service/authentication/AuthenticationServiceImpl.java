package com.booking.bookingbackend.service.authentication;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.constant.RefreshTokenType;
import com.booking.bookingbackend.constant.TokenType;
import com.booking.bookingbackend.constant.UserRole;
import com.booking.bookingbackend.data.dto.request.AuthenticationRequest;
import com.booking.bookingbackend.data.dto.request.ExchangeTokenRequest;
import com.booking.bookingbackend.data.dto.request.LogoutRequest;
import com.booking.bookingbackend.data.dto.request.RefreshTokenRequest;
import com.booking.bookingbackend.data.dto.request.VerificationEmailRequest;
import com.booking.bookingbackend.data.dto.response.AuthenticationResponse;
import com.booking.bookingbackend.data.dto.response.ProfileResponse;
import com.booking.bookingbackend.data.entity.CustomUserDetails;
import com.booking.bookingbackend.data.entity.Profile;
import com.booking.bookingbackend.data.entity.RedisVerificationCode;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.repository.ProfileRepository;
import com.booking.bookingbackend.data.repository.RoleRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.data.repository.VerificationCodeRepository;
import com.booking.bookingbackend.data.repository.httpclient.OutboundIdentityClient;
import com.booking.bookingbackend.data.repository.httpclient.OutboundUserClient;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.service.jwt.JwtService;
import com.booking.bookingbackend.service.notification.MailService;
import com.booking.bookingbackend.service.profile.ProfileService;
import com.booking.bookingbackend.service.user.UserInfoService;
import com.booking.bookingbackend.util.SecurityUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
  OutboundIdentityClient outboundIdentityClient;
  OutboundUserClient outboundUserClient;
  UserRepository userRepository;
  RoleRepository roleRepository;
  ProfileRepository profileRepository;

  @Value("${jwt.expirationDay}")
  @NonFinal
  int expirationDay;

  @Value("${outbound.identity.client-id}")
  @NonFinal
  String CLIENT_ID;

  @Value("${outbound.identity.client-secret}")
  @NonFinal
  String CLIENT_SECRET;

  @Value("${outbound.identity.redirect-uri}")
  @NonFinal
  String REDIRECT_URI;

  @Value("${outbound.identity.grant-type}")
  @NonFinal
  String GRANT_TYPE;

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
            request.password().strip()
        )
    );

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    userDetails.getAuthorities().stream()
        .filter(authority -> authority.getAuthority().equals("ROLE_USER"))
        .findFirst()
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION));
    User user = userDetails.user();

    if (user != null && !userDetails.isEnabled()) {
      ProfileResponse profileResponse = profileService.findByUserId(user.getId());
      String firstName = profileResponse.getFirstName();
      String lastName = profileResponse.getLastName();
      String name = firstName != null && lastName != null ? firstName + " " + lastName : null;

      log.info("User is not active");
      mailService.sendVerificationEmail(
          user.getEmail(),
          name,
          SecurityUtils.generateVerificationCode()
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

      response.addCookie(createRefreshTokenCookie(
          refreshToken,
          RefreshTokenType.USER,
          expirationDay * 24 * 60 * 60
      ));

    } else {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }
    return AuthenticationResponse.builder()
        .accessToken(accessToken)
        .build();
  }

  @Override
  @Transactional
  public AuthenticationResponse outboundAuthenticate(String code, HttpServletResponse res) {
    var response = outboundIdentityClient.exchangeToken(
        ExchangeTokenRequest.builder()
            .code(code)
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .redirectUri(REDIRECT_URI)
            .grantType(GRANT_TYPE)
            .build()
    );
    log.info("Outbound authentication successful for code: {}", code);

    var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());

    var roles = roleRepository.findAllByName(List.of(UserRole.USER.name()));

    var user = userRepository.findByEmailJoinRoleWithPermission(userInfo.getEmail())
        .orElseGet(
            () -> {
              User userCreation = userRepository.save(
                  User.builder()
                      .email(userInfo.getEmail())
                      .password("")
                      .active(userInfo.isVerifiedEmail())
                      .roles(new HashSet<>(roles))
                      .build()
              );
              profileRepository.save(
                  Profile.builder()
                      .user(userCreation)
                      .firstName(userInfo.getGivenName())
                      .lastName(userInfo.getFamilyName())
                      .avatar(userInfo.getPicture())
                      .build()
              );

              return userCreation;
            }
        );
    var authorities = SecurityUtils.getAuthorities(user);

    var accessToken = jwtService.generateAccessToken(
        user.getEmail(),
        authorities
    );

    var refreshToken = jwtService.generateRefreshToken(
        user.getEmail(),
        authorities
    );

    res.addCookie(createRefreshTokenCookie(
        refreshToken,
        RefreshTokenType.USER,
        expirationDay * 24 * 60 * 60
    ));

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

    String refreshToken = getRefreshTokenFromCookies(req, RefreshTokenType.USER);
    if (refreshToken == null || refreshToken.isEmpty()) {
      // Throw an exception if the refresh token is not found or empty
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }
    log.info("Refresh token request received");

    String username = jwtService.extractUsername(TokenType.REFRESH_TOKEN, refreshToken);

    // Load the user by username
    CustomUserDetails userDetails = (CustomUserDetails) userInfoService.loadUserByUsername(
        username
    );

    // Validate the refresh token and access token
    if (!jwtService.validateToken(TokenType.REFRESH_TOKEN, refreshToken, userDetails)
        && !jwtService.extractUsername(TokenType.ACCESS_TOKEN, refreshTokenRequest.accessToken())
        .equals(username)) {
      // Throw an exception if both tokens are invalid
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }

    // Invalidate the old access token
    invalidateToken(refreshTokenRequest.accessToken(), TokenType.ACCESS_TOKEN);

    // Generate a new access token for the authenticated user
    String newAccessToken = jwtService.generateAccessToken(
        userDetails.getUsername(),
        userDetails.getAuthorities()
    );

    // Return the new access token in the response
    return AuthenticationResponse.builder()
        .accessToken(newAccessToken)
        .build();
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
    //delete token
    verificationCodeRepository.deleteById(request.email());
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
    String refreshToken = getRefreshTokenFromCookies(req, RefreshTokenType.USER);

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
