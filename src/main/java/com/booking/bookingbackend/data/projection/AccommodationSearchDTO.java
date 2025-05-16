package com.booking.bookingbackend.data.projection;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record AccommodationSearchDTO(
        @JsonProperty("accommodation_id")
        UUID accommodationId,
        String name,
        int capacity,
        Float size,
        @JsonProperty("available_rooms")
        Long availableRooms,
        @JsonProperty("total_price")
        double totalPrice,
        List<RoomDTO> rooms,
        List<AmenityDTO> amenities
) {
}
