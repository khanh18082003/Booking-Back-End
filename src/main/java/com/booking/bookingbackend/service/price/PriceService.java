package com.booking.bookingbackend.service.price;

import java.math.BigDecimal;
import java.util.List;

import com.booking.bookingbackend.data.dto.response.AccommodationBookingResponse;

public interface PriceService {

    BigDecimal calculatePrice(List<AccommodationBookingResponse> availableAccommodationDTOs);
}
