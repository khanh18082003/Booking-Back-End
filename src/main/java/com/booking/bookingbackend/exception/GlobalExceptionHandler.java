package com.booking.bookingbackend.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import jakarta.validation.ConstraintViolation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j(topic = "GLOBAL-EXCEPTION")
public class GlobalExceptionHandler {

    private static final String MIN_CONST = "min";
    private static final String MAX_CONST = "max";

    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse<Void>> handleAppException(AppException appException, WebRequest request) {
        ErrorCode errorCode = appException.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.<Void>builder()
                        .timeStamp(LocalDateTime.now())
                        .code(errorCode.getErrorCode())
                        .status(errorCode.getStatus().value())
                        .error(errorCode.getStatus().getReasonPhrase())
                        .path(request.getDescription(false).replace("uri=", ""))
                        .message(appException.getMessage())
                        .build());
    }

    @ExceptionHandler({InternalAuthenticationServiceException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ApiResponse<Void> handleAuthenticationException(RuntimeException e, WebRequest req) {
        return ApiResponse.<Void>builder()
                .timeStamp(LocalDateTime.now())
                .code(ErrorCode.MESSAGE_UN_AUTHENTICATION.getErrorCode())
                .status(ErrorCode.MESSAGE_UN_AUTHENTICATION.getStatus().value())
                .error(ErrorCode.MESSAGE_UN_AUTHENTICATION.getStatus().getReasonPhrase())
                .path(req.getDescription(false).replace("uri=", ""))
                .message(Translator.toLocale(ErrorCode.MESSAGE_UN_AUTHENTICATION.getErrorCode()))
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiResponse<Void> handlerMethodArgumentNotValidationEx(MethodArgumentNotValidException e, WebRequest req) {
        String errorKey = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        log.info("errorKey: {}", errorKey);
        String fieldError = Objects.requireNonNull(e.getFieldError()).getField();
        ErrorCode errorCode;
        try {
            errorCode = ErrorCode.valueOf(errorKey);
        } catch (IllegalArgumentException ex) {
            errorCode = ErrorCode.MESSAGE_INVALID_KEY;
        }

        Map<String, Object> attributes;

        var constraint = e.getBindingResult().getAllErrors().get(0).unwrap(ConstraintViolation.class);
        attributes = constraint.getConstraintDescriptor().getAttributes();

        return ApiResponse.<Void>builder()
                .timeStamp(LocalDateTime.now())
                .code(errorCode.getErrorCode())
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().getReasonPhrase())
                .path(req.getDescription(false).replace("uri=", ""))
                .message(mapMessage(Translator.toLocale(errorCode.getErrorCode()), attributes, fieldError))
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ApiResponse<Void> handleAccessDeniedException(AccessDeniedException e, WebRequest req) {
        return ApiResponse.<Void>builder()
                .timeStamp(LocalDateTime.now())
                .code(ErrorCode.MESSAGE_UN_AUTHORIZATION.getErrorCode())
                .status(ErrorCode.MESSAGE_UN_AUTHORIZATION.getStatus().value())
                .error(ErrorCode.MESSAGE_UN_AUTHORIZATION.getStatus().getReasonPhrase())
                .path(req.getDescription(false).replace("uri=", ""))
                .message(Translator.toLocale(ErrorCode.MESSAGE_UN_AUTHORIZATION.getErrorCode()))
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    ApiResponse<Void> handlerException(RuntimeException ex, WebRequest req) {
        log.error("Unexpected exception: ", ex); // <== Thêm dòng này để in full stack trace

        return ApiResponse.<Void>builder()
                .timeStamp(LocalDateTime.now())
                .code(ErrorCode.MESSAGE_UN_CATEGORIES.getErrorCode())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .path(req.getDescription(false).replace("uri=", ""))
                .message(ex.getMessage())
                .build();
    }

    private String mapMessage(String message, Map<String, Object> attributes, String fieldError) {
        if (attributes.containsKey(MIN_CONST)) {
            String minValue = String.valueOf(attributes.get(MIN_CONST));
            message = message.replace("{" + MIN_CONST + "}", minValue);
        }
        if (attributes.containsKey(MAX_CONST)) {
            String maxValue = String.valueOf(attributes.get(MAX_CONST));
            message = message.replace("{" + MAX_CONST + "}", maxValue);
        }
        return String.format(message, fieldError);
    }
}
