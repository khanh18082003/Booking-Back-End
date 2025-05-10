package com.booking.bookingbackend.data.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PropertiesDTO(
    @JsonProperty("properties_id")
    UUID propertiesId,
    @JsonProperty("properties_name")
    String propertiesName,
    String image,
    String address,
    String city,
    String district,
    BigDecimal rating,
    @JsonProperty("total_rating")
    Integer totalRating,
    Double distance,
    @JsonProperty("total_price")
    Double totalPrice,
    @JsonProperty("properties_type")
    String propertiesType,
    @JsonProperty("nights")
    Long nights,
    @JsonProperty("adults")
    Long adults,
    @JsonProperty("children")
    Long children,
    List<AccommodationDTO> accommodations
) {

}
