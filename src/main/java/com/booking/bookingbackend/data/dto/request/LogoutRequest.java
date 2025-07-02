package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LogoutRequest(@NotBlank(message = "MESSAGE_NOT_BLANK") @JsonProperty("access_token") String accessToken)
        implements Serializable {}
