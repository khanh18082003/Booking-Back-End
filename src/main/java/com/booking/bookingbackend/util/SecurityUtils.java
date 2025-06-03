package com.booking.bookingbackend.util;

import com.booking.bookingbackend.data.entity.CustomUserDetails;
import com.booking.bookingbackend.data.entity.User;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

  public static String generateVerificationCode() {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    SecureRandom random = new SecureRandom();
    StringBuilder code = new StringBuilder(6);
    for (int i = 0; i < 6; i++) {
      code.append(characters.charAt(random.nextInt(characters.length())));
    }
    return code.toString();
  }

  public static CustomUserDetails getCurrentUser() {
    return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
  }

  public static Collection<? extends GrantedAuthority> getAuthorities(User user) {

    Set<SimpleGrantedAuthority> authorities = new HashSet<>();

    // Process each role
    user.getRoles().forEach(role -> {
      // Add the role itself as an authority
      authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

      // Add all permissions from this role
      role.getPermissions().forEach(permission ->
          authorities.add(new SimpleGrantedAuthority(
              permission.getMethod() + permission.getUrl())
          )
      );
    });
    return authorities;
  }
}
