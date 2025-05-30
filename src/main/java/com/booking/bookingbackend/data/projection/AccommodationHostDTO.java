package com.booking.bookingbackend.data.projection;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AccommodationHostDTO(
        UUID id,
        String name,
        BigDecimal basePrice,
        int capacity,
        int totalRooms,
        int totalUnits,
        String description,
        float size,
        String unit,
        @JsonProperty("extra_images")
        List<String> images,
        List<RoomDTO> rooms
) implements Serializable {
}
