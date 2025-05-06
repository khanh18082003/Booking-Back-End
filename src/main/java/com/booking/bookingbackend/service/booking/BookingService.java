package com.booking.bookingbackend.service.booking;

import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.data.dto.request.BookingRequest;
import com.booking.bookingbackend.data.dto.response.BookingResponse;
import com.booking.bookingbackend.data.entity.Booking;
import com.booking.bookingbackend.data.repository.BookingRepository;
import com.booking.bookingbackend.service.BaseEntityService;

import java.util.List;
import java.util.UUID;

public interface BookingService extends BaseEntityService<UUID, Booking, BookingRepository, BookingResponse> {
    @Override
    default Class<?> getEntityClass() {
        return Booking.class;
    }
    BookingResponse save(BookingRequest bookingRequest);
    BookingResponse changeStatus(UUID id, BookingStatus status);
    List<BookingResponse> BookingHistory(UUID userId);
}
