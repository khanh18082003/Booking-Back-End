package com.booking.bookingbackend.data.projection;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PropertiesDetailDTO(
        UUID id,
        String name,
        String description,
        String image,
        String address,
        BigDecimal rating,
        @JsonProperty("total_rating") Integer totalRating,
        Boolean status,
        Double latitude,
        Double longitude,
        @JsonProperty("check_in_time") LocalTime checkInTime,
        @JsonProperty("check_out_time") LocalTime checkOutTime,
        @JsonProperty("property_type") String propertyType,
        List<AmenityDTO> amenities,
        @JsonProperty("image_urls") List<String> imageUrls)
        implements Serializable {

    public PropertiesDetailDTO {
        amenities = amenities == null ? List.of() : amenities;
        imageUrls = imageUrls == null ? List.of() : imageUrls;
    }
}
