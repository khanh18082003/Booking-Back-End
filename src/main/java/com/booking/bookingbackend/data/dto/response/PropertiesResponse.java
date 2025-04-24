package com.booking.bookingbackend.data.dto.response;

import com.booking.bookingbackend.data.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertiesResponse implements Serializable {

    String name;
    String description;
    String address;
    String city;
    String country;
    String district;
    BigDecimal rating;
    boolean status;
    BigDecimal latitude;
    BigDecimal longitude;
    @JsonFormat(pattern = "HH:mm")
    @JsonProperty("check_in_time") LocalTime checkInTime;
    @JsonFormat(pattern = "HH:mm")
    @JsonProperty("check_out_time") LocalTime checkOutTime;
    @JsonProperty("created_at") Timestamp createdAt;
    @JsonProperty("updated_at") Timestamp updatedAt;
    UserResponse host;
    @JsonProperty("property_type")
    PropertyTypeResponse propertyType;
    Set<AmenitiesResponse> amenities;
}
