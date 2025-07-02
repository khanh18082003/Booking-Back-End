package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.With;

public record AccommodationCreationRequest(
        @NotBlank(message = "MESSAGE_NOT_BLANK") String name,
        @JsonProperty("base_price") BigDecimal basePrice,
        Integer capacity,
        @JsonProperty("total_units") Integer totalUnits,
        String description,
        @JsonProperty("extra_images") @With List<String> extraImages,
        Float size,
        String unit,
        @JsonProperty("total_rooms") Integer totalRooms,
        @JsonProperty("properties_id") UUID propertiesId,
        @JsonProperty("amenities_ids") Set<UUID> amenitiesIds,
        @JsonProperty("rooms") Set<RoomCreationRequest> rooms)
        implements Serializable {}
