package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.BookingDetail;
import com.booking.bookingbackend.data.entity.ids.BookingDetailId;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingDetailsRepository extends BaseRepository<BookingDetail, BookingDetailId> {

}
