package com.booking.bookingbackend.data.mapper;

import org.mapstruct.Mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.response.BedTypeResponse;
import com.booking.bookingbackend.data.entity.BedType;

@Mapper(componentModel = "spring")
public interface BedTypeMapper extends EntityDtoMapper<BedType, BedTypeResponse> {}
