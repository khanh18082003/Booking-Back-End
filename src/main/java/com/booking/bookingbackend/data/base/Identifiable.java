package com.booking.bookingbackend.data.base;

import java.io.Serializable;

@FunctionalInterface
public interface Identifiable<I> extends Serializable {

    I getId();
}
