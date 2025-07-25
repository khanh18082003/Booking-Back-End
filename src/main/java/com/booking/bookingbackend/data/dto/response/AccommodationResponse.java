package com.booking.bookingbackend.data.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@JsonInclude(Include.NON_NULL)
public class AccommodationResponse implements Serializable {

    UUID id;
    String name;
    String description;

    @JsonProperty("base_price")
    BigDecimal basePrice;

    Integer capacity;

    @JsonProperty("total_units")
    Integer totalUnits;

    Float size;
    String unit;

    @JsonProperty("total_rooms")
    Integer totalRooms;

    @JsonProperty("amenities")
    Set<AmenitiesResponse> amenities;

    @JsonProperty("rooms")
    Set<RoomResponse> rooms;

    @JsonProperty("properties_name")
    String propertiesName;

    @JsonProperty("images")
    List<String> images;

    @JsonProperty("created_at")
    Timestamp createdAt;

    @JsonProperty("updated_at")
    Timestamp updatedAt;
}
