package com.booking.bookingbackend.service.jwt;

import com.booking.bookingbackend.constant.TokenType;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

  String generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities);

  String generateRefreshToken(String username, Collection<? extends GrantedAuthority> authorities);

  String extractUsername(TokenType type, String token);

  Date extractExpiration(TokenType type, String token);

  String extractId(TokenType type, String token);

  boolean validateToken(TokenType type, String token, UserDetails userDetails);
}
