package com.booking.bookingbackend.data.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AvailableAccommodationDTO(
    @JsonProperty("id")
    Integer id,
    LocalDate date,
    @JsonProperty("total_inventory")
    Integer totalInventory,
    BigDecimal price,
    @JsonProperty("total_reserved")
    Integer totalReserved
) implements Serializable {

}
