package com.booking.bookingbackend.data.projection;

import java.io.Serializable;

public interface AccommodationBaseBookingResponse extends Serializable {

  String getName();

  Integer getQuantity();
}
