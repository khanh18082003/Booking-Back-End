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
    Double distance,
    @JsonProperty("total_price")
    Double totalPrice,
    @JsonProperty("properties_type")
    String propertiesType,
    @JsonProperty("nights")
    Integer nights,
    @JsonProperty("adults")
    Integer adults,
    @JsonProperty("children")
    Integer children,
    List<AccommodationDTO> accommodations
) {

}
