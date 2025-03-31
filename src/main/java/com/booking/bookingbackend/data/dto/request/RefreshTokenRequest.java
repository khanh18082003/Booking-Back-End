package com.booking.bookingbackend.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank(message = "MESSAGE_NOT_BLANK")
    @JsonProperty("access_token")
    String accessToken,
    @NotBlank(message = "MESSAGE_NOT_BLANK")
    @JsonProperty("refresh_token")
    String refreshToken
) {

}
