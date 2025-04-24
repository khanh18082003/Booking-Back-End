package com.booking.bookingbackend.service.user;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.constant.Gender;
import com.booking.bookingbackend.data.dto.request.UserCreationRequest;
import com.booking.bookingbackend.data.dto.response.UserProfileDto;
import com.booking.bookingbackend.data.dto.response.UserResponse;
import com.booking.bookingbackend.data.entity.Profile;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.mapper.UserMapper;
import com.booking.bookingbackend.data.repository.ProfileRepository;
import com.booking.bookingbackend.data.repository.RoleRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.exception.AppException;
import jakarta.persistence.Tuple;
import java.sql.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "USER-SERVICE")
public class UserServiceImpl implements UserService {

  UserRepository repository;
  RoleRepository roleRepository;
  ProfileRepository profileRepository;
  UserMapper mapper;
  PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public UserResponse save(UserCreationRequest request) {
    if (repository.existsByEmail(request.email().strip())) {
      throw new AppException(ErrorCode.MESSAGE_EMAIL_EXISTED);
    }

    if (request.confirmPassword() == null || !request.confirmPassword()
        .equals(request.password())) {
      throw new AppException(ErrorCode.MESSAGE_INVALID_CONFIRM_PASSWORD);
    }

    User entity = mapper.toEntity(request);
    entity.setPassword(passwordEncoder.encode(request.password()));
    var roles = roleRepository.findAllByName(request.roles());
    entity.setRoles(new HashSet<>(roles));
    entity.setActive(false); // user not active

    UserResponse res = mapper.toDtoResponse(repository.save(entity));

    Profile profile = Profile.builder()
        .user(entity)
        .build();
    profileRepository.save(profile);
    return res;
  }

  @Override
  public UserResponse findByEmail(String email) {
    return mapper.toDtoResponse(
        repository
            .findByEmail(email)
            .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
                getEntityClass().getSimpleName())

            ));
  }

  @Override
  public void activeUser(String email) {
    User user = repository.findByEmail(email).orElseThrow(
        () -> new AppException(
            ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName()
        )
    );
    if (!user.isActive()) {
      user.setActive(true);
      repository.save(user);
    }
  }


  @Override
  public UserProfileDto getMyProfile() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      UUID userId = authentication.getPrincipal() instanceof User
          ? ((User) authentication.getPrincipal()).getId()
          : null;
      if (userId == null) {
        throw new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName());
      }
      log.info("User ID: {}", userId);

      // Fetch user profile using the repository
      Tuple userProfileTuple = repository.findByUserProfile(userId)
          .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
              getEntityClass().getSimpleName())

          );

      // validate gender
      String gender = userProfileTuple.get("gender", String.class);
      Gender genderValidated = null;
      if (gender != null) {
        try {
          genderValidated = Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
          throw new AppException(ErrorCode.MESSAGE_INVALID_FORMAT, "Invalid gender value");
        }
      }

      return new UserProfileDto(
          UUID.fromString(userProfileTuple.get("id", String.class)),
          userProfileTuple.get("email", String.class),
          userProfileTuple.get("isActive", Boolean.class),
          UUID.fromString(userProfileTuple.get("profileId", String.class)),
          userProfileTuple.get("avatar", String.class),
          userProfileTuple.get("phone", String.class),
          Optional.ofNullable(userProfileTuple.get("dob", Date.class))
              .map(Date::toLocalDate)
              .orElse(null),
          genderValidated,
          userProfileTuple.get("address", String.class),
          userProfileTuple.get("firstName", String.class),
          userProfileTuple.get("lastName", String.class),
          userProfileTuple.get("name", String.class),
          userProfileTuple.get("countryCode", String.class),
          userProfileTuple.get("nationality", String.class)
      );
    } else {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }
  }

  @Transactional
  @Override
  public boolean changePassword() {
    return false;
  }

}
