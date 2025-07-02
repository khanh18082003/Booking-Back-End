package com.booking.bookingbackend.data.dto.request;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshTokenRequest(
        @NotBlank(message = "MESSAGE_NOT_BLANK") @JsonProperty("access_token") String accessToken) {}
