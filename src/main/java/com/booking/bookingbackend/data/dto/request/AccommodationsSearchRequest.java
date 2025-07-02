package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;

@Builder
public record AccommodationsSearchRequest(
        UUID id, LocalDate startDate, LocalDate endDate, Integer adults, Integer children, Integer rooms)
        implements Serializable {}
