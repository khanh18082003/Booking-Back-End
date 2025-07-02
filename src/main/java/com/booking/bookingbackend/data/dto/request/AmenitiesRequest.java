package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

public record AmenitiesRequest(
        @NotBlank(message = "MESSAGE_NOT_BLANK") String name,
        String icon,
        @NotBlank(message = "MESSAGE_NOT_BLANK") String type)
        implements Serializable {}
;
