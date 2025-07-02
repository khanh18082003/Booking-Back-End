package com.booking.bookingbackend.data.projection;

import java.io.Serializable;
import java.util.List;

public record RoomDTO(String room_name, List<BedDTO> beds) implements Serializable {}
