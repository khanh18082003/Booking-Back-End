package com.booking.bookingbackend.data.mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.AccommodationCreationRequest;
import com.booking.bookingbackend.data.dto.response.AccommodationResponse;
import com.booking.bookingbackend.data.entity.Accommodation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccommodationMapper extends EntityDtoMapper<Accommodation, AccommodationResponse> {

  Accommodation toEntity(AccommodationCreationRequest request);
}
