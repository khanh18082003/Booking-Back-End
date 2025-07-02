package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResetPasswordRequest(
        @JsonProperty("email") String email, @JsonProperty("new_password") String newPassword)
        implements Serializable {}
