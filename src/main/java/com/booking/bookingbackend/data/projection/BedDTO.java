package com.booking.bookingbackend.data.projection;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BedDTO(
    String bedTypeName,
    int quantity
) implements Serializable {

}
