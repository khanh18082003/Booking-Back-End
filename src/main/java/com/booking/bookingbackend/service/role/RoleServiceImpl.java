package com.booking.bookingbackend.service.role;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.RoleRequest;
import com.booking.bookingbackend.data.dto.response.RoleResponse;
import com.booking.bookingbackend.data.entity.Role;
import com.booking.bookingbackend.data.mapper.RoleMapper;
import com.booking.bookingbackend.data.repository.PermissionRepository;
import com.booking.bookingbackend.data.repository.RoleRepository;
import com.booking.bookingbackend.exception.AppException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "ROLE-SERVICE")
public class RoleServiceImpl implements RoleService {

    RoleRepository repository;
    PermissionRepository permissionRepository;
    RoleMapper mapper;

    @Transactional
    @Override
    public RoleResponse save(RoleRequest request) {
        Role entity = mapper.toEntity(request);
        var permissions = permissionRepository.findAllById(request.permissions());
        entity.setPermissions(permissions);
        return mapper.toDtoResponse(repository.save(entity));
    }

    @Transactional
    @Override
    public void update(int id, RoleRequest request) {
        Role entity = repository
                .findById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.MESSAGE_INVALID_ENTITY_ID, getEntityClass().getSimpleName()));

        mapper.merge(request, entity);
        var permissions = permissionRepository.findAllById(request.permissions());
        entity.setPermissions(permissions);

        repository.save(entity);
    }
}
