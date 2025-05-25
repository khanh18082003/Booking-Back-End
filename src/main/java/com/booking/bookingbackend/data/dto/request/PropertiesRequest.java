package com.booking.bookingbackend.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.With;

public record PropertiesRequest(
    String name,
    String description,
    @With
    String image,
    @JsonProperty("extra_images")
    @With
    List<String> extraImages,
    String address,
    String ward,
    String district,
    String city,
    String province,
    String country,
    BigDecimal rating,
    @JsonProperty("total_rating")
    Integer totalRating,
    Boolean status,
    @With
    Double latitude,
    @With
    Double longitude,
    @JsonProperty("check_in_time") LocalTime checkInTime,
    @JsonProperty("check_out_time") LocalTime checkOutTime,
    @JsonProperty("type_id") Integer typeId,
    @JsonProperty("amenities_id") Set<UUID> amenitiesIds
) implements java.io.Serializable {

}
