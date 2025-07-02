package com.booking.bookingbackend.data.dto.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BookingRequest(
        @JsonProperty("check_in") LocalDate checkIn,
        @JsonProperty("check_out") LocalDate checkOut,
        Integer adults,
        Integer children,
        @JsonProperty("payment_method") String paymentMethod,
        @JsonProperty("user_id") UUID userId,
        GuestCreationRequest guest,
        @JsonProperty("properties_id") UUID propertiesID,
        @JsonProperty("accommodations") List<AccommodationForBookingRequest> accommodations)
        implements java.io.Serializable {}
