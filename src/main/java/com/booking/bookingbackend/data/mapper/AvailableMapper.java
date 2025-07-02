package com.booking.bookingbackend.data.mapper;

import org.mapstruct.Mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.response.AvailableResponse;
import com.booking.bookingbackend.data.entity.Available;

@Mapper(componentModel = "spring")
public interface AvailableMapper extends EntityDtoMapper<Available, AvailableResponse> {}
