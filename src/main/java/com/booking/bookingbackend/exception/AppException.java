package com.booking.bookingbackend.exception;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.ErrorCode;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode, Object... args) {
        super(String.format(Translator.toLocale(errorCode.getErrorCode()), args));
        this.errorCode = errorCode;
    }
}
