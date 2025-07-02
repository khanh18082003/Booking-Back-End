package com.booking.bookingbackend.data.projection;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AccommodationDTO(
        UUID accommodationId,
        String accommodationName,
        int suggestedQuantity,
        int totalCapacity,
        int totalBeds,
        double totalPrice,
        List<BedDTO> bedNames)
        implements Serializable {}
