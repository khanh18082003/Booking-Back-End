package com.booking.bookingbackend.service.user;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.UserCreationRequest;
import com.booking.bookingbackend.data.dto.response.UserCreationResponse;
import com.booking.bookingbackend.data.entity.Profile;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.mapper.UserMapper;
import com.booking.bookingbackend.data.repository.ProfileRepository;
import com.booking.bookingbackend.data.repository.RoleRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.exception.AppException;
import java.util.HashSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
  public UserCreationResponse save(UserCreationRequest request) {
    if (repository.existsByEmail(request.email())) {
      throw new AppException(ErrorCode.MESSAGE_EMAIL_EXISTED);
    }

    User entity = mapper.toEntity(request);
    entity.setPassword(passwordEncoder.encode(request.password()));
    var roles = roleRepository.findAllByName(request.roles());
    entity.setRoles(new HashSet<>(roles));

    UserCreationResponse res = mapper.toDtoResponse(repository.save(entity));
    Profile profile = Profile.builder()
        .user(entity)
        .build();
    profileRepository.save(profile);
    return res;
  }

  @Transactional
  @Override
  public boolean changePassword() {
    return false;
  }

}
