package com.booking.bookingbackend.data.projection;

import java.util.List;

public record RoomDTO(
        String room_name,
        List<BedDTO> beds
) {
}
