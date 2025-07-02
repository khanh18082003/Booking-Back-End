package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record RoleRequest(
        @NotBlank(message = "MESSAGE_NOT_BLANK") String name, String description, List<Integer> permissions)
        implements Serializable {}
