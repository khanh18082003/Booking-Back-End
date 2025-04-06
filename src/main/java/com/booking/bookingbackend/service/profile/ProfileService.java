package com.booking.bookingbackend.service.profile;

import com.booking.bookingbackend.data.dto.request.ProfileUpdateRequest;
import com.booking.bookingbackend.data.dto.response.ProfileResponse;
import com.booking.bookingbackend.data.entity.Profile;
import com.booking.bookingbackend.data.repository.ProfileRepository;
import com.booking.bookingbackend.service.BaseEntityService;
import java.util.UUID;

public interface ProfileService extends BaseEntityService<
    UUID,
    Profile,
    ProfileRepository,
    ProfileResponse> {

  @Override
  default Class<?> getEntityClass() {
    return Profile.class;
  }

  void update(UUID id, ProfileUpdateRequest request);

  ProfileResponse findByUserId(UUID userId);
}
