package com.booking.bookingbackend.data.dto.request;

import com.booking.bookingbackend.constant.AmenityType;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record AmenitiesRequest (
        @NotBlank(message = "MESSAGE_NOT_BLANK")
        String name,
        String icon,
        @NotBlank(message = "MESSAGE_NOT_BLANK")
        AmenityType type
) implements Serializable{};
