package com.booking.bookingbackend.data.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.GuestBooking;

@Repository
public interface GuestBookingRepository extends BaseRepository<GuestBooking, UUID> {}
