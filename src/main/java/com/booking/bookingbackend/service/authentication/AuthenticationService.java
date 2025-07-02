package com.booking.bookingbackend.service.authentication;

import java.io.UnsupportedEncodingException;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.booking.bookingbackend.constant.RefreshTokenType;
import com.booking.bookingbackend.data.dto.request.AuthenticationRequest;
import com.booking.bookingbackend.data.dto.request.LogoutRequest;
import com.booking.bookingbackend.data.dto.request.OutboundAuthenticationAppRequest;
import com.booking.bookingbackend.data.dto.request.RefreshTokenRequest;
import com.booking.bookingbackend.data.dto.request.VerificationEmailRequest;
import com.booking.bookingbackend.data.dto.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response)
            throws MessagingException, UnsupportedEncodingException;

    AuthenticationResponse outboundAuthenticate(String code, HttpServletResponse res);

    AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest, HttpServletRequest req);

    void logout(LogoutRequest logoutRequest, HttpServletRequest req, HttpServletResponse res);

    void verifyTokenEmail(VerificationEmailRequest request);

    default Cookie createRefreshTokenCookie(String refreshToken, RefreshTokenType type, int maxAge) {
        Cookie cookie = new Cookie(type.getType(), refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    default String getRefreshTokenFromCookies(HttpServletRequest request, RefreshTokenType type) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (type.getType().equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    AuthenticationResponse outboundAuthenticateApp(OutboundAuthenticationAppRequest request);
}
