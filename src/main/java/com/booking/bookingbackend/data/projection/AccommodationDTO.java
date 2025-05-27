package com.booking.bookingbackend.data.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record AccommodationDTO(
    @JsonProperty("accommodation_id")
    UUID accommodationId,
    @JsonProperty("accommodation_name")
    String accommodationName,
    @JsonProperty("suggested_quantity")
    int suggestedQuantity,
    @JsonProperty("total_capacity")
    int totalCapacity,
    @JsonProperty("total_beds")
    int totalBeds,
    @JsonProperty("total_price")
    double totalPrice,
    @JsonProperty("bed_names")
    List<BedDTO> bedNames
) implements Serializable {

}
