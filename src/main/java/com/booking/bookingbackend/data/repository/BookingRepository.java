package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Booking;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends BaseRepository<Booking, UUID> {
    List<Booking> findAllByUserId(UUID userId);
}
