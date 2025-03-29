package com.booking.bookingbackend.service.role;

import com.booking.bookingbackend.data.dto.request.RoleRequest;
import com.booking.bookingbackend.data.dto.response.RoleResponse;
import com.booking.bookingbackend.data.entity.Role;
import com.booking.bookingbackend.data.repository.RoleRepository;
import com.booking.bookingbackend.service.BaseEntityService;

public interface RoleService extends BaseEntityService<
    Integer,
    Role,
    RoleRepository,
    RoleResponse> {

  @Override
  default Class<?> getEntityClass() {
    return Role.class;
  }

  RoleResponse save(RoleRequest request);

  void update(int id, RoleRequest request);
}
