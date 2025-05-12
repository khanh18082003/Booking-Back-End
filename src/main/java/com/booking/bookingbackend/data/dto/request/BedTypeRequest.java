package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;

public record BedTypeRequest(
        Integer id,
        int quantity
) implements Serializable {
}
