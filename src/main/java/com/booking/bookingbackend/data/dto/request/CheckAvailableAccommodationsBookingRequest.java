package com.booking.bookingbackend.data.dto.request;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CheckAvailableAccommodationsBookingRequest(
    LocalDate checkIn,
    LocalDate checkOut,
    Integer adults,
    Integer children,
    Integer rooms,
    String... accommodations
) {

}
