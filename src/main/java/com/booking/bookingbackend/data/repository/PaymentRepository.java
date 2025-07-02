package com.booking.bookingbackend.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Payment;

@Repository
public interface PaymentRepository extends BaseRepository<Payment, UUID> {

    Optional<Payment> findByBookingId(UUID bookingId);
}
