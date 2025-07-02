package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoomCreationRequest(
        @JsonProperty("room_name") String roomName,
        @JsonProperty("room_type_id") Integer roomTypeId,
        @JsonProperty("bed_types") List<BedTypeRequest> bedTypes)
        implements Serializable {}
