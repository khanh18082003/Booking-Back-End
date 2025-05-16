package com.booking.bookingbackend.data.projection;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BedDTO(
    @JsonProperty("bed_type_name")
    String bedTypeName,
    int quantity
) {

}
