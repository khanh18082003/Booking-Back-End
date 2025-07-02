package com.booking.bookingbackend.service.permission;

import com.booking.bookingbackend.data.dto.request.PermissionRequest;
import com.booking.bookingbackend.data.dto.response.PermissionResponse;
import com.booking.bookingbackend.data.entity.Permission;
import com.booking.bookingbackend.data.repository.PermissionRepository;
import com.booking.bookingbackend.service.BaseEntityService;

public interface PermissionService
        extends BaseEntityService<Integer, Permission, PermissionRepository, PermissionResponse> {

    @Override
    default Class<?> getEntityClass() {
        return Permission.class;
    }

    PermissionResponse save(PermissionRequest request);

    void update(int id, PermissionRequest request);
}
