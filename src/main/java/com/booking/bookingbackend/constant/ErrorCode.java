package com.booking.bookingbackend.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  MESSAGE_UN_CATEGORIES(HttpStatus.BAD_REQUEST, "M9999"),
  MESSAGE_SUCCESS(HttpStatus.OK, "M000"),
  MESSAGE_INVALID_ENTITY_ID(HttpStatus.NOT_FOUND, "M0011"),
  MESSAGE_INVALID_KEY(HttpStatus.BAD_REQUEST, "M0001"),

  // Validator
  MESSAGE_INVALID_HTTP_METHOD(HttpStatus.BAD_REQUEST, "M0100"),
  MESSAGE_NOT_BLANK(HttpStatus.BAD_REQUEST, "M0101"),
  ;


  private final HttpStatus status;
  private final String errorCode;


  ErrorCode(HttpStatus status, String errorCode) {
    this.errorCode = errorCode;
    this.status = status;
  }
}
