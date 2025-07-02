package com.booking.bookingbackend.data.dto.response;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertiesBookingResponse {

    UUID id;
    String name;
    String image;
    String description;
    String address;
    String ward;
    String district;
    String city;
    String province;
    String country;
    BigDecimal rating;

    @JsonProperty("total_rating")
    Integer totalRating;

    @JsonFormat(pattern = "HH:mm")
    @JsonProperty("check_in_time")
    LocalTime checkInTime;

    @JsonFormat(pattern = "HH:mm")
    @JsonProperty("check_out_time")
    LocalTime checkOutTime;

    @JsonProperty("properties_type")
    String propertiesType;

    Set<AmenitiesResponse> amenities;
}
