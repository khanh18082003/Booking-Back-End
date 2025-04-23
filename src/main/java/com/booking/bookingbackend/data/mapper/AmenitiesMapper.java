package com.booking.bookingbackend.data.mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.AmenitiesRequest;
import com.booking.bookingbackend.data.dto.response.AmenitiesResponse;
import com.booking.bookingbackend.data.entity.Amenities;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AmenitiesMapper extends EntityDtoMapper<Amenities, AmenitiesResponse> {
    @Mapping(target = "type", ignore = true)
    Amenities toEntity(AmenitiesRequest request);
    void merge(AmenitiesResponse response, @MappingTarget Amenities entity);
}
