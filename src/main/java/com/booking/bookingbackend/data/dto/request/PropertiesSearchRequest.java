package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record PropertiesSearchRequest(
    Double latitude,
    Double longitude,
    Double radius,
    LocalDate startDate,
    LocalDate endDate,
    Integer adults,
    Integer children
) implements Serializable {

}
