package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CheckExistEmailRequest(
        @Email(message = "MESSAGE_INVALID_EMAIL") @NotBlank(message = "MESSAGE_NOT_BLANK") String email)
        implements Serializable {}
