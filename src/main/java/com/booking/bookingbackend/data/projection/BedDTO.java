package com.booking.bookingbackend.data.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public record BedDTO(
    @JsonProperty("bed_type_name")
    String bedTypeName,
    int quantity
) implements Serializable {

}
