package com.booking.bookingbackend.data.projection;

import java.io.Serializable;

public record AmenityDTO(
    String name,
    String icon
) implements Serializable {

}
