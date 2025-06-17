package com.booking.bookingbackend.data.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PropertiesDTO(
    UUID propertiesId,
    String propertiesName,
    String image,
    Double latitude,
    Double longitude,
    String address,
    String city,
    String district,
    BigDecimal rating,
    Integer totalRating,
    Double distance,
    Double totalPrice,
    LocalDate checkIn,
    LocalDate checkOut,
    String propertiesType,
    Long nights,
    Long adults,
    Long children,
    List<AccommodationDTO> accommodations
) implements Serializable {

}
