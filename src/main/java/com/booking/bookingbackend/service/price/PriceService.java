package com.booking.bookingbackend.service.price;

import com.booking.bookingbackend.data.dto.response.AccommodationBookingResponse;
import java.math.BigDecimal;
import java.util.List;

public interface PriceService {

  BigDecimal calculatePrice(List<AccommodationBookingResponse> availableAccommodationDTOs);

}
