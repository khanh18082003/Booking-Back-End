package com.booking.bookingbackend.data.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.PropertiesRequest;
import com.booking.bookingbackend.data.dto.response.PropertiesResponse;
import com.booking.bookingbackend.data.entity.Properties;

@Mapper(componentModel = "spring")
public interface PropertiesMapper extends EntityDtoMapper<Properties, PropertiesResponse> {

    Properties toEntity(PropertiesRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(PropertiesRequest request, @MappingTarget Properties entity);
}
