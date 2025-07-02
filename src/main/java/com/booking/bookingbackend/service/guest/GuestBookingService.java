package com.booking.bookingbackend.service.guest;

import java.util.UUID;

import com.booking.bookingbackend.data.dto.request.GuestBookingRequest;
import com.booking.bookingbackend.data.dto.response.GuestBookingResponse;
import com.booking.bookingbackend.data.entity.GuestBooking;
import com.booking.bookingbackend.data.repository.GuestBookingRepository;
import com.booking.bookingbackend.service.BaseEntityService;

public interface GuestBookingService
        extends BaseEntityService<UUID, GuestBooking, GuestBookingRepository, GuestBookingResponse> {

    @Override
    default Class<?> getEntityClass() {
        return GuestBooking.class;
    }

    GuestBookingResponse save(GuestBookingRequest guestBookingRequest);
}
