package com.booking.bookingbackend.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record ResetPasswordRequest(
    @JsonProperty("email") String email,
    @JsonProperty("new_password") String newPassword
) implements Serializable {
}
