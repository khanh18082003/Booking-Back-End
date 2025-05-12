package com.booking.bookingbackend.data.dto.request;

import com.booking.bookingbackend.data.entity.BedType;

import java.io.Serializable;
import java.util.List;

public record RoomCreationRequest(
        String roomName,
        Integer roomTypeId,
        List<BedTypeRequest> bedTypes
) implements Serializable {
}
