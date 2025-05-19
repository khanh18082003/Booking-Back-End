package com.booking.bookingbackend.data.mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.BookingRequest;
import com.booking.bookingbackend.data.dto.response.BookingResponse;
import com.booking.bookingbackend.data.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookingMapper extends EntityDtoMapper<Booking, BookingResponse> {

    @Mapping(target = "adultUnits", source = "adults")
    @Mapping(target = "childUnits", source = "children")
    Booking toEntity(BookingRequest bookingRequest);

    void merge(BookingRequest bookingRequest,@MappingTarget Booking entity);
}
