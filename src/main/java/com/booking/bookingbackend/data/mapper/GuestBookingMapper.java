package com.booking.bookingbackend.data.mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.GuestBookingRequest;
import com.booking.bookingbackend.data.dto.response.GuestBookingResponse;
import com.booking.bookingbackend.data.entity.GuestBooking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GuestBookingMapper extends EntityDtoMapper<GuestBooking, GuestBookingResponse> {
    GuestBooking toEntity(GuestBookingRequest guestBookingRequest);
}
