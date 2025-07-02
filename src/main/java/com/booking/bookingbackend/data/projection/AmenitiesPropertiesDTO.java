package com.booking.bookingbackend.data.projection;

import java.io.Serializable;
import java.util.UUID;

public interface AmenitiesPropertiesDTO extends Serializable {

    UUID getId();

    String getName();

    Integer getQuantity();
}
