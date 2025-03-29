package com.booking.bookingbackend.data.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public record AuthenticationRequest(
    @Email(message = "MESSAGE_INVALID_EMAIL")
    @NotBlank(message = "MESSAGE_NOT_BLANK")
    String email,
    String password
) implements Serializable {

}
