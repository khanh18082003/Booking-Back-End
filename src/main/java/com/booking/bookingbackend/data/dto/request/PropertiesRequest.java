package com.booking.bookingbackend.data.dto.request;

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
        BigDecimal latitude,
        BigDecimal longitude,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        String email,
        int typeId,
        Set<UUID> amenitiesIds,
        Set<String> urls
) implements java.io.Serializable {
}
