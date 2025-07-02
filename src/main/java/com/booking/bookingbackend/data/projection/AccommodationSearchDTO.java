package com.booking.bookingbackend.data.projection;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccommodationSearchDTO(
        @JsonProperty("accommodation_id") UUID accommodationId,
        String name,
        int capacity,
        @JsonProperty("image_urls") List<String> imageUrls,
        Float size,
        @JsonProperty("available_rooms") Long availableRooms,
        @JsonProperty("total_price") double totalPrice,
        String description,
        List<RoomDTO> rooms,
        List<AmenityDTO> amenities)
        implements Serializable {}
