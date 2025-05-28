package com.booking.bookingbackend.service.booking;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.exception.AppException;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "BOOKING-VALIDATION-SERVICE")
public class BookingValidationService {

  public void validateRequest(
      LocalDate checkIn,
      LocalDate checkOut,
      Integer adults,
      Integer children
  ) {
    final var currentDate = LocalDate.now();

    if (checkIn.isAfter(checkOut) || checkIn.isBefore(currentDate)) {
      throw new AppException(ErrorCode.MESSAGE_INVALID_CHECKIN_DATE);
    }
    if (adults <= 0 || children < 0) {
      throw new AppException(ErrorCode.MESSAGE_INVALID_GUESTS);
    }
  }
}
