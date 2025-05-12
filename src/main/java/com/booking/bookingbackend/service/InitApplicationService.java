package com.booking.bookingbackend.service;

import com.booking.bookingbackend.constant.Gender;
import com.booking.bookingbackend.constant.UserRole;
import com.booking.bookingbackend.data.entity.Profile;
import com.booking.bookingbackend.data.entity.Role;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.repository.ProfileRepository;
import com.booking.bookingbackend.data.repository.RoleRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class InitApplicationService {

  @NonFinal
  @Value("${admin-user.email}")
  String adminEmail;

  @NonFinal
  @Value("${admin-user.password}")
  String adminPassword;

  @NonFinal
  @Value("${admin-profile.firstName}")
  String firstName;

  @NonFinal
  @Value("${admin-profile.lastName}")
  String lastName;

  @NonFinal
  @Value("${admin-profile.countryCode}")
  String countryCode;

  @NonFinal
  @Value("${admin-profile.phone}")
  String phone;

  @NonFinal
  @Value("${admin-profile.gender}")
  Gender gender;

  @NonFinal
  @Value("${admin-profile.address}")
  String address;

  RoleRepository roleRepository;
  UserRepository userRepository;
  ProfileRepository profileRepository;
  PasswordEncoder passwordEncoder;

  @Transactional
  public void init() {
    if (!roleRepository.existsByNameIsIgnoreCase(UserRole.ADMIN.name())) {
      Role role = Role.builder()
          .name(UserRole.ADMIN.name())
          .description("ADMIN has all permissions")
          .build();
      roleRepository.save(role);
      log.info("Create role {} successfully", role.getName());
    }
    if (!userRepository.existsByEmail(adminEmail)) {
      var roles = roleRepository.findAllByName(List.of(UserRole.ADMIN.name()));
      User user = User.builder()
          .email(adminEmail)
          .password(passwordEncoder.encode(adminPassword))
          .active(true)
          .roles(new HashSet<>(roles))
          .build();

      Profile profile = Profile.builder()
          .user(user)
          .firstName(firstName)
          .lastName(lastName)
          .countryCode(countryCode)
          .phone(phone)
          .gender(gender)
          .address(address)
          .build();

      userRepository.save(user);
      profileRepository.save(profile);
      log.info("Admin created successfully");
    }
    log.info("Application init successfully");
  }
}
