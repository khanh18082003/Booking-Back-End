package com.booking.bookingbackend.data.dto.response;

import com.booking.bookingbackend.data.entity.User;
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
    @JsonProperty("check_in_time") LocalTime checkInTime;
    @JsonProperty("check_out_time") LocalTime checkOutTime;
    @JsonProperty("created_at") Timestamp createdAt;
    @JsonProperty("updated_at") Timestamp updatedAt;
    UserResponse user;
    PropertyTypeResponse propertyType;
    Set<AmenitiesResponse> amenities;

}
