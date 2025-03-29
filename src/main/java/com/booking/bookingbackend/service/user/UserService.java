package com.booking.bookingbackend.service.user;

import com.booking.bookingbackend.data.dto.request.RoleRequest;
import com.booking.bookingbackend.data.dto.request.UserCreationRequest;
import com.booking.bookingbackend.data.dto.response.UserCreationResponse;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.service.BaseEntityService;
import java.util.UUID;

public interface UserService extends BaseEntityService<
    UUID,
    User,
    UserRepository,
    UserCreationResponse> {

  @Override
  default Class<?> getEntityClass() {
    return User.class;
  }

  UserCreationResponse save(UserCreationRequest request);

  boolean changePassword();
}
