package com.booking.bookingbackend.data.mapper;


import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.response.BedTypeResponse;
import com.booking.bookingbackend.data.entity.BedType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BedTypeMapper extends EntityDtoMapper<BedType, BedTypeResponse> {
}
