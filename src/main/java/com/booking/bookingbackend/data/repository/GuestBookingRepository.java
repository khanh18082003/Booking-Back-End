package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Booking;
import com.booking.bookingbackend.data.entity.GuestBooking;

import java.util.UUID;

public interface GuestBookingRepository  extends BaseRepository<GuestBooking, UUID> {
}
