package com.booking.bookingbackend.service.profile;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.constant.Gender;
import com.booking.bookingbackend.data.dto.request.ProfileUpdateRequest;
import com.booking.bookingbackend.data.dto.response.ProfileResponse;
import com.booking.bookingbackend.data.entity.Profile;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.mapper.ProfileMapper;
import com.booking.bookingbackend.data.repository.ProfileRepository;
import com.booking.bookingbackend.exception.AppException;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "ROLE-SERVICE")
public class ProfileServiceImpl implements ProfileService {

  ProfileRepository repository;
  ProfileMapper mapper;

  @Override
  public void update(UUID id, ProfileUpdateRequest request) {
    Profile entity = repository.findById(id)
        .orElseThrow(
            () -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID, getEntityClass())
        );

    mapper.merge(request, entity);
    if (request.gender() != null) {
      entity.setGender(Gender.valueOf(request.gender().toUpperCase()));
    }

    repository.save(entity);
  }

  @Transactional
  @Override
  public void updateMyProfile(ProfileUpdateRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }
    UUID id = ((User) authentication.getPrincipal()).getId();

    Profile entity = repository.findByUserId(id)
        .orElseThrow(
            () -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID, getEntityClass().getSimpleName())
        );

    mapper.merge(request, entity);
    if (request.gender() != null) {
      entity.setGender(Gender.valueOf(request.gender().toUpperCase()));
    }
    repository.save(entity);
  }

  @Override
  public ProfileResponse findByUserId(UUID userId) {
    return mapper.toDtoResponse(repository.findByUserId(userId)
        .orElseThrow(
            () -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID, getEntityClass())
        )
    );
  }


}
