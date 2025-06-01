package com.booking.bookingbackend.data.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record AvailableUpdatePriceRequest(
    UUID id,
    List<LocalDate> dates,
    BigDecimal price
) {

}
