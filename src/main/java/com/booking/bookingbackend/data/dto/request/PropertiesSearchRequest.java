package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record PropertiesSearchRequest(
    String location,
    Double radius,
    LocalDate startDate,
    LocalDate endDate,
    Integer adults,
    Integer children,
    Integer rooms
) implements Serializable {

}
