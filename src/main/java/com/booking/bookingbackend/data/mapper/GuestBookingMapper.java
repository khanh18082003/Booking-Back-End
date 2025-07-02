package com.booking.bookingbackend.data.mapper;

import org.mapstruct.Mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.GuestBookingRequest;
import com.booking.bookingbackend.data.dto.response.GuestBookingResponse;
import com.booking.bookingbackend.data.entity.GuestBooking;

@Mapper(componentModel = "spring")
public interface GuestBookingMapper extends EntityDtoMapper<GuestBooking, GuestBookingResponse> {
    GuestBooking toEntity(GuestBookingRequest guestBookingRequest);
}
