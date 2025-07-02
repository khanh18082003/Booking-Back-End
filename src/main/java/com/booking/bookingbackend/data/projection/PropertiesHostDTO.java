package com.booking.bookingbackend.data.projection;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record PropertiesHostDTO(
        UUID id,
        String name,
        String description,
        String address,
        String ward,
        String image,
        String district,
        String city,
        String province,
        String country,
        BigDecimal rating,
        @JsonProperty("total_rating") Integer totalRating,
        boolean status,
        Double latitude,
        Double longitude,
        @JsonFormat(pattern = "HH:mm") @JsonProperty("check_in_time") LocalTime checkInTime,
        @JsonFormat(pattern = "HH:mm") @JsonProperty("check_out_time") LocalTime checkOutTime,
        @JsonProperty("created_at") Timestamp createdAt,
        @JsonProperty("updated_at") Timestamp updatedAt,
        @JsonProperty("property_type") String propertyType,
        @JsonProperty("extra_images") List<String> imageUrls,
        @JsonProperty("amenities") List<UUID> amenitiesIds)
        implements Serializable {}
