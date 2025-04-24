package com.booking.bookingbackend.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  MESSAGE_UN_CATEGORIES(HttpStatus.BAD_REQUEST, "M9999"),
  MESSAGE_SUCCESS(HttpStatus.OK, "M000"),
  MESSAGE_INVALID_ENTITY_ID(HttpStatus.NOT_FOUND, "M0011"),
  MESSAGE_INVALID_KEY(HttpStatus.BAD_REQUEST, "M0001"),
  MESSAGE_USER_EXISTED(HttpStatus.CONFLICT, "M0002"),
  MESSAGE_EMAIL_EXISTED(HttpStatus.CONFLICT, "M0003"),
  MESSAGE_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "M0004"),

  // Validator
  MESSAGE_INVALID_HTTP_METHOD(HttpStatus.BAD_REQUEST, "M0100"),
  MESSAGE_NOT_BLANK(HttpStatus.BAD_REQUEST, "M0101"),
  MESSAGE_INVALID_SIZE(HttpStatus.BAD_REQUEST, "M0102"),
  MESSAGE_INVALID_EMAIL(HttpStatus.BAD_REQUEST, "M0103"),
  MESSAGE_INVALID_CONFIRM_PASSWORD(HttpStatus.BAD_REQUEST, "M0104"),
  MESSAGE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "M0105"),

  // Authentication and Authorization
  MESSAGE_UN_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "M0401"),
  MESSAGE_UN_AUTHORIZATION(HttpStatus.FORBIDDEN, "M0403"),
  MESSAGE_USER_NOT_ACTIVE(HttpStatus.UNAUTHORIZED, "M0404"),

  ;



  private final HttpStatus status;
  private final String errorCode;


  ErrorCode(HttpStatus status, String errorCode) {
    this.errorCode = errorCode;
    this.status = status;
  }
}
