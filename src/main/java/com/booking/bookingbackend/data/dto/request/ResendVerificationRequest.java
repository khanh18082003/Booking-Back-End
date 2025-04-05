package com.booking.bookingbackend.data.dto.request;

import java.util.UUID;

public record ResendVerificationRequest(
    UUID userId
) {

}
