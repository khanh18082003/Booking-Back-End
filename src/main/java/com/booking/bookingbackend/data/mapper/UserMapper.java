package com.booking.bookingbackend.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.UserCreationRequest;
import com.booking.bookingbackend.data.dto.response.UserResponse;
import com.booking.bookingbackend.data.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityDtoMapper<User, UserResponse> {
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserCreationRequest request);
}
