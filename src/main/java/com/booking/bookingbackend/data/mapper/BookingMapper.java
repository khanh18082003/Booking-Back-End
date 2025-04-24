package com.booking.bookingbackend.data.mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.BookingRequest;
import com.booking.bookingbackend.data.dto.response.BookingResponse;
import com.booking.bookingbackend.data.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookingMapper extends EntityDtoMapper<Booking, BookingResponse> {

    Booking toEntity(BookingRequest bookingResponse);

    void merge(BookingRequest bookingResponse,@MappingTarget Booking entity);
}
