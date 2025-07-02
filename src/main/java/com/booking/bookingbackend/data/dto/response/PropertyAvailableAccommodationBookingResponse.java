package com.booking.bookingbackend.data.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
public class PropertyAvailableAccommodationBookingResponse {
    PropertiesBookingResponse properties;
    List<CheckedAvailableAccommodationBookingResponse> accommodations;

    @JsonProperty("check_in")
    LocalDate checkIn;

    @JsonProperty("check_out")
    LocalDate checkOut;

    Integer adults;
    Integer children;
    Integer rooms;

    @JsonProperty("total_price")
    BigDecimal totalPrice;
}
