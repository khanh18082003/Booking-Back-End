package com.booking.bookingbackend.service.jwt;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.booking.bookingbackend.constant.DeviceType;
import com.booking.bookingbackend.constant.TokenType;

public interface JwtService {

    String generateAccessToken(
            String username, Collection<? extends GrantedAuthority> authorities, DeviceType deviceType);

    String generateRefreshToken(String username, Collection<? extends GrantedAuthority> authorities);

    String extractUsername(TokenType type, String token);

    Date extractExpiration(TokenType type, String token);

    String extractId(TokenType type, String token);

    boolean validateToken(TokenType type, String token, UserDetails userDetails);
}
