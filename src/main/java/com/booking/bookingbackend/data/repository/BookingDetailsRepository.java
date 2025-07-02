package com.booking.bookingbackend.data.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.BookingDetail;
import com.booking.bookingbackend.data.entity.ids.BookingDetailId;

@Repository
public interface BookingDetailsRepository extends BaseRepository<BookingDetail, BookingDetailId> {

    @Query("SELECT bd FROM BookingDetail bd WHERE bd.id.bookingId = :bookingId")
    List<BookingDetail> findAllByBookingId(UUID bookingId);
}
