package com.booking.bookingbackend.data.dto.request;

import java.util.UUID;

public record AccommodationForBookingRequest(
    UUID id,
    Integer quantity
) {

}
