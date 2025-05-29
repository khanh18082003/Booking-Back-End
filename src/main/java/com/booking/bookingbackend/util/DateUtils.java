package com.booking.bookingbackend.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {
  public static long daysBetween(LocalDate start, LocalDate end) {
    if (start == null || end == null) return 0;
    return ChronoUnit.DAYS.between(start, end);
  }
}
