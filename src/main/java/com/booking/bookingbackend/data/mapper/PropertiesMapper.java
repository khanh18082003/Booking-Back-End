package com.booking.bookingbackend.data.mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.PropertiesRequest;
import com.booking.bookingbackend.data.dto.response.PropertiesResponse;
import com.booking.bookingbackend.data.entity.Properties;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PropertiesMapper extends EntityDtoMapper<Properties, PropertiesResponse> {
     Properties toEntity(PropertiesRequest request);
}
