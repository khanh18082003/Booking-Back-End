package com.booking.bookingbackend.service.user;

import com.booking.bookingbackend.data.dto.request.UserCreationRequest;
import com.booking.bookingbackend.data.dto.response.UserResponse;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.service.BaseEntityService;
import java.util.UUID;

public interface UserService extends BaseEntityService<
    UUID,
    User,
    UserRepository,
    UserResponse> {

  @Override
  default Class<?> getEntityClass() {
    return User.class;
  }

  UserResponse save(UserCreationRequest request);

  UserResponse findByEmail(String email);

  void activeUser(String email);

  boolean changePassword();
}
