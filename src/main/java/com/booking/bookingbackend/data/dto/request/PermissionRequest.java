package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

import com.booking.bookingbackend.data.validator.HttpMethodValidator;

public record PermissionRequest(
        @HttpMethodValidator(message = "MESSAGE_INVALID_HTTP_METHOD") @NotBlank(message = "MESSAGE_NOT_BLANK")
                String method,
        @NotBlank(message = "MESSAGE_NOT_BLANK") String url,
        String description)
        implements Serializable {}
