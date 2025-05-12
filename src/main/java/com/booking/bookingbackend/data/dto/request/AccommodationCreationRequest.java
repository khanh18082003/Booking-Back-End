package com.booking.bookingbackend.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record AccommodationCreationRequest(
    @NotBlank(message = "MESSAGE_NOT_BLANK") String name,
    @JsonProperty("base_price") BigDecimal basePrice,
    Integer capacity,
    @JsonProperty("total_units") Integer totalUnits,
    String description,
    Float size,
    String unit,
    @JsonProperty("total_rooms") Integer totalRooms,
    @JsonProperty("properties_id") UUID propertiesId,
    @JsonProperty("amenities_ids") Set<UUID> amenitiesIds,
    @JsonProperty("rooms") Set<RoomCreationRequest> rooms
) implements Serializable {

}
