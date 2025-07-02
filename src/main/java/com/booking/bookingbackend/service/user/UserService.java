package com.booking.bookingbackend.service.user;

import java.util.List;
import java.util.UUID;

import com.booking.bookingbackend.data.dto.request.ResetPasswordRequest;
import com.booking.bookingbackend.data.dto.request.UserCreationRequest;
import com.booking.bookingbackend.data.dto.response.RevenueResponse;
import com.booking.bookingbackend.data.dto.response.UserProfileDto;
import com.booking.bookingbackend.data.dto.response.UserResponse;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.service.BaseEntityService;

public interface UserService extends BaseEntityService<UUID, User, UserRepository, UserResponse> {

    @Override
    default Class<?> getEntityClass() {
        return User.class;
    }

    UserResponse save(UserCreationRequest request);

    UserResponse findByEmail(String email);

    void activeUser(String email);

    UserProfileDto getMyProfile();

    void changePassword(ResetPasswordRequest request);

    void AddRoleHost(UUID userId, String roleName);

    RevenueResponse getRevenueByHostId(UUID userId);

    RevenueResponse getRevenueByHostIdWithMonthAndYear(UUID userId, int month, int year);

    List<RevenueResponse> getRevenueByHostIdWithYear(UUID userId, int year);
}
