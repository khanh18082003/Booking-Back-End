package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;

public record ForgotPasswordRequest(
        String email
) implements Serializable {
}
