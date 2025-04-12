package com.booking.bookingbackend.data.dto.request;

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
        String rating,
        boolean status,
        String latitude,
        String longitude,
        String checkInTime,
        String checkOutTime,
        String email,
        int typeId,
        Set<UUID> amenitiesIds
) implements java.io.Serializable {
}
