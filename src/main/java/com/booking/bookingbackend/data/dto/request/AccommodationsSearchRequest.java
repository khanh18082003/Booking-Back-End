package com.booking.bookingbackend.data.dto.request;

import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;
@Builder
public record AccommodationsSearchRequest(
        UUID id,
        LocalDate startDate,
        LocalDate endDate,
        Integer adults,
        Integer children,
        Integer rooms
) implements Serializable {
}
