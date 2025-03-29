package com.booking.bookingbackend.data.mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.RoleRequest;
import com.booking.bookingbackend.data.dto.response.RoleResponse;
import com.booking.bookingbackend.data.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityDtoMapper<Role, RoleResponse> {

  @Mapping(target = "permissions", ignore = true)
  Role toEntity(RoleRequest request);

  @Mapping(target = "permissions", ignore = true)
  void merge(RoleRequest request, @MappingTarget Role entity);
}
