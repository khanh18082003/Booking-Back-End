package com.booking.bookingbackend.service.authentication;

import com.booking.bookingbackend.data.dto.request.AuthenticationRequest;
import com.booking.bookingbackend.data.dto.request.LogoutRequest;
import com.booking.bookingbackend.data.dto.request.RefreshTokenRequest;
import com.booking.bookingbackend.data.dto.request.VerificationEmailRequest;
import com.booking.bookingbackend.data.dto.response.AuthenticationResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

public interface AuthenticationService {

  AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response)
      throws MessagingException, UnsupportedEncodingException;

  AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest, HttpServletRequest req);

  void logout(LogoutRequest logoutRequest, HttpServletRequest req, HttpServletResponse res);

  void verifyTokenEmail(VerificationEmailRequest request);

}
