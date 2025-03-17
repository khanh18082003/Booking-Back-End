package com.booking.bookingbackend.data.mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.PermissionRequest;
import com.booking.bookingbackend.data.dto.response.PermissionResponse;
import com.booking.bookingbackend.data.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper extends EntityDtoMapper<Permission, PermissionResponse> {

  Permission toEntity(PermissionRequest request);

  void merge(PermissionRequest request, @MappingTarget Permission entity);
}
