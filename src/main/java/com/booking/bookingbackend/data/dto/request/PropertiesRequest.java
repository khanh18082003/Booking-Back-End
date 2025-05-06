package com.booking.bookingbackend.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

public record PropertiesRequest(
        String name,
        String description,
        String address,
        String city,
        String country,
        String district,
        BigDecimal rating,
        boolean status,
        Double latitude,
        Double longitude,
        @JsonProperty("check_in_time") LocalTime checkInTime,
        @JsonProperty("check_out_time") LocalTime checkOutTime,
        @JsonProperty("type_id") int typeId,
        @JsonProperty("amenities_id") Set<UUID> amenitiesIds,
        Set<String> urls
) implements java.io.Serializable {
}
