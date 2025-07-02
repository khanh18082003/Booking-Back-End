package com.booking.bookingbackend.data.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface CheckedAvailableAccommodationBookingResponse {

    UUID getId();

    String getName();

    Integer getQuantity();

    BigDecimal getTotalPrice();
}
