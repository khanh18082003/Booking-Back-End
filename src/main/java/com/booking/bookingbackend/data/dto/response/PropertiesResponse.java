package com.booking.bookingbackend.data.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertiesResponse implements Serializable {
    UUID id;
    String name;
    String description;
    String address;
    String ward;
    String image;
    String district;
    String city;
    String province;
    String country;
    BigDecimal rating;

    @JsonProperty("total_rating")
    Integer totalRating;

    boolean status;
    Double latitude;
    Double longitude;

    @JsonFormat(pattern = "HH:mm")
    @JsonProperty("check_in_time")
    LocalTime checkInTime;

    @JsonFormat(pattern = "HH:mm")
    @JsonProperty("check_out_time")
    LocalTime checkOutTime;

    @JsonProperty("created_at")
    Timestamp createdAt;

    @JsonProperty("updated_at")
    Timestamp updatedAt;

    UserResponse host;

    @JsonProperty("property_type")
    PropertyTypeResponse propertyType;

    Set<AmenitiesResponse> amenities;
}
