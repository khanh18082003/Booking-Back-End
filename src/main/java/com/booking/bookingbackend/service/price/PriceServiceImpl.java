package com.booking.bookingbackend.service.price;

import com.booking.bookingbackend.data.dto.response.AccommodationBookingResponse;
import com.booking.bookingbackend.data.projection.AvailableAccommodationDTO;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class PriceServiceImpl implements PriceService {

  @Override
  public BigDecimal calculatePrice(
      List<AccommodationBookingResponse> availableAccommodationDTOs
  ) {
    // Calculate the total price based on the available accommodations
    BigDecimal totalPrice = BigDecimal.ZERO;
    for (AccommodationBookingResponse accommodationList : availableAccommodationDTOs) {
      for (AvailableAccommodationDTO accommodation : accommodationList.getAvailableAccommodations()) {
        totalPrice = totalPrice.add(accommodation.price());
      }
      // Multiply by the quantity of accommodations
      totalPrice = totalPrice.multiply(BigDecimal.valueOf(accommodationList.getQuantity()));
    }
    return totalPrice;
  }
}
