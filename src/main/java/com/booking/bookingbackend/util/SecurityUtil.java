package com.booking.bookingbackend.util;

import java.security.SecureRandom;

public class SecurityUtil {
  public static String generateVerificationCode() {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    SecureRandom random = new SecureRandom();
    StringBuilder code = new StringBuilder(6);
    for (int i = 0; i < 6; i++) {
      code.append(characters.charAt(random.nextInt(characters.length())));
    }
    return code.toString();
  }
}
