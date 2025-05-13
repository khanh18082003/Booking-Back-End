package com.booking.bookingbackend.data.dto.request;

import com.booking.bookingbackend.data.entity.BedType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public record RoomCreationRequest(
        @JsonProperty("room_name")
        String roomName,
        @JsonProperty("room_type_id")
        Integer roomTypeId,
        @JsonProperty("bed_types")
        List<BedTypeRequest> bedTypes
) implements Serializable {
}
