package com.booking.bookingbackend.data.dto.response;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.booking.bookingbackend.data.projection.AvailableAccommodationDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class AccommodationBookingResponse implements Serializable {

    UUID id;
    List<AvailableAccommodationDTO> availableAccommodations;
    Integer quantity;
}
