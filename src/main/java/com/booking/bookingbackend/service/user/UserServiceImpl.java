package com.booking.bookingbackend.service.user;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.Tuple;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.constant.Gender;
import com.booking.bookingbackend.data.dto.request.ResetPasswordRequest;
import com.booking.bookingbackend.data.dto.request.UserCreationRequest;
import com.booking.bookingbackend.data.dto.response.RevenueResponse;
import com.booking.bookingbackend.data.dto.response.UserProfileDto;
import com.booking.bookingbackend.data.dto.response.UserResponse;
import com.booking.bookingbackend.data.entity.CustomUserDetails;
import com.booking.bookingbackend.data.entity.Profile;
import com.booking.bookingbackend.data.entity.RedisVerificationCode;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.mapper.UserMapper;
import com.booking.bookingbackend.data.repository.ProfileRepository;
import com.booking.bookingbackend.data.repository.RoleRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.data.repository.VerificationCodeRepository;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.util.SecurityUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

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
    VerificationCodeRepository verificationCodeRepository;

    @Transactional
    @Override
    public UserResponse save(UserCreationRequest request) {
        if (repository.existsByEmail(request.email().strip())) {
            throw new AppException(ErrorCode.MESSAGE_EMAIL_EXISTED);
        }

        if (request.confirmPassword() == null || !request.confirmPassword().equals(request.password())) {
            throw new AppException(ErrorCode.MESSAGE_INVALID_CONFIRM_PASSWORD);
        }

        User entity = mapper.toEntity(request);
        entity.setPassword(passwordEncoder.encode(request.password()));
        var roles = roleRepository.findAllByName(request.roles());
        entity.setRoles(new HashSet<>(roles));
        entity.setActive(false); // user not active

        UserResponse res = mapper.toDtoResponse(repository.save(entity));

        Profile profile = Profile.builder().user(entity).build();
        profileRepository.save(profile);
        return res;
    }

    @Override
    public UserResponse findByEmail(String email) {
        return mapper.toDtoResponse(
                repository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.MESSAGE_USER_NOT_FOUND)));
    }

    @Override
    public void activeUser(String email) {
        User user = repository
                .findByEmail(email)
                .orElseThrow(() -> new AppException(
                        ErrorCode.MESSAGE_INVALID_ENTITY_ID, getEntityClass().getSimpleName()));
        if (!user.isActive()) {
            user.setActive(true);
            repository.save(user);
        }
    }

    @Override
    public UserProfileDto getMyProfile() {

        CustomUserDetails userDetails = SecurityUtils.getCurrentUser();

        if (userDetails == null) {
            throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
        }
        User user = userDetails.user();
        // Fetch user profile using the repository
        Tuple userProfileTuple = repository
                .findByUserProfile(user.getId())
                .orElseThrow(() -> new AppException(
                        ErrorCode.MESSAGE_INVALID_ENTITY_ID, getEntityClass().getSimpleName()));

        // validate gender
        String gender = userProfileTuple.get("gender", String.class);
        String genderValidated = null;
        if (gender != null) {
            try {
                genderValidated = Gender.valueOf(gender.toUpperCase()).getValue();
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
                userProfileTuple.get("nationality", String.class));
    }

    @Transactional
    @Override
    public void changePassword(ResetPasswordRequest request) {
        Optional<RedisVerificationCode> redisCodeOpt = verificationCodeRepository.findById(request.email());
        //    log.info("Redis code: {}", redisCodeOpt.get().getCode());
        if (redisCodeOpt.isPresent()) {
            log.error("Token not found");
            throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
        }

        User user = repository
                .findByEmail(request.email())
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_USER_NOT_FOUND));
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        repository.save(user);
    }

    public void AddRoleHost(UUID userId, String roleName) {
        User user = repository
                .findById(userId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.MESSAGE_INVALID_ENTITY_ID, getEntityClass().getSimpleName()));
        user.getRoles().add(roleRepository.findByName(roleName));
        repository.save(user);
    }

    @Override
    public RevenueResponse getRevenueByHostId(UUID userId) {
        return repository.revenueByHostId(userId);
    }

    @Override
    public RevenueResponse getRevenueByHostIdWithMonthAndYear(UUID userId, int month, int year) {
        return repository.revenueByHostIdWithMonthAndYear(userId, month, year);
    }

    @Override
    public List<RevenueResponse> getRevenueByHostIdWithYear(UUID userId, int year) {
        return repository.revenueByHostIdWithYear(userId, year);
    }
}
