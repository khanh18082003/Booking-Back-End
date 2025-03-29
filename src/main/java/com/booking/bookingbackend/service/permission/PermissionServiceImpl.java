package com.booking.bookingbackend.service.permission;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.PermissionRequest;
import com.booking.bookingbackend.data.dto.response.PermissionResponse;
import com.booking.bookingbackend.data.entity.Permission;
import com.booking.bookingbackend.data.mapper.PermissionMapper;
import com.booking.bookingbackend.data.repository.PermissionRepository;
import com.booking.bookingbackend.exception.AppException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "PERMISSION-SERVICE")
public class PermissionServiceImpl implements PermissionService {

  PermissionRepository repository;
  PermissionMapper mapper;


  @Transactional
  @Override
  public PermissionResponse save(PermissionRequest request) {
    Permission entity = mapper.toEntity(request);

    return mapper.toDtoResponse(repository.save(entity));
  }

  @Transactional
  @Override
  public void update(int id, PermissionRequest request) {
    Permission entity = repository.findById(id).orElseThrow(() -> new AppException(
        ErrorCode.MESSAGE_INVALID_ENTITY_ID, getEntityClass().getSimpleName()));

    mapper.merge(request, entity);
    repository.save(entity);
  }


}
