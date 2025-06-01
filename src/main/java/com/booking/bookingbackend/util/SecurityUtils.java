package com.booking.bookingbackend.util;

import com.booking.bookingbackend.data.entity.CustomUserDetails;
import com.booking.bookingbackend.data.entity.User;
import java.security.SecureRandom;
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
    return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
