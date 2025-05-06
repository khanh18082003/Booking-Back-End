package com.booking.bookingbackend.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record AccommodationUpdateRequest(
        String name,
        @JsonProperty("base_price") BigDecimal basePrice,
        Integer capacity,
        @JsonProperty("total_units") Integer totalUnits,
        String description,
        Float size,
        @JsonProperty("properties_id") UUID propertiesId,
        @JsonProperty("amenities_ids") Set<UUID> amenitiesIds
) implements Serializable {
}
