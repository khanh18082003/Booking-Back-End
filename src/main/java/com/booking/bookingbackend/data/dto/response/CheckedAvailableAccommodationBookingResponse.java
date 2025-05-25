package com.booking.bookingbackend.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.UUID;


@JsonInclude(JsonInclude.Include.NON_NULL)
public interface CheckedAvailableAccommodationBookingResponse {

  UUID getId();

  String getName();

  Integer getQuantity();

  BigDecimal getTotalPrice();
}
