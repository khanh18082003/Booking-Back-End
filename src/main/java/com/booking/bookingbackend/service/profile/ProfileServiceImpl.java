package com.booking.bookingbackend.service.profile;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.constant.Gender;
import com.booking.bookingbackend.data.dto.request.ProfileUpdateRequest;
import com.booking.bookingbackend.data.dto.response.ProfileResponse;
import com.booking.bookingbackend.data.entity.CustomUserDetails;
import com.booking.bookingbackend.data.entity.Profile;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.mapper.ProfileMapper;
import com.booking.bookingbackend.data.repository.ProfileRepository;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.util.SecurityUtils;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "PROFILE-SERVICE")
public class ProfileServiceImpl implements ProfileService {

  ProfileRepository repository;
  ProfileMapper mapper;

  @NonFinal
  @Value("${spring.servlet.multipart.max-file-size}")
  DataSize MAX_SIZE_AVATAR;

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
            () -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
                getEntityClass().getSimpleName())
        );

    mapper.merge(request, entity);
    if (request.gender() != null) {
      entity.setGender(Gender.valueOf(request.gender().toUpperCase()));
    }
    repository.save(entity);
  }

  @Override
  public ProfileResponse updateAvatar(MultipartFile avatarFile) throws IOException {
    CustomUserDetails userDetails = SecurityUtils.getCurrentUser();
    User user = userDetails.getUser();
    if (user == null) {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }
    Profile profile = repository.findByUserId(user.getId())
        .orElseThrow(
            () -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID, getEntityClass())
        );

    if (avatarFile.getSize() > MAX_SIZE_AVATAR.toBytes()) {
      throw new AppException(ErrorCode.FILE_TOO_LARGE, MAX_SIZE_AVATAR.toBytes());
    }

    String oldAvatar = profile.getAvatar();
    if (oldAvatar != null) {
      try {
        Path oldAvatarPath = Paths.get(oldAvatar.substring(37)); // Remove base URL part
        Files.deleteIfExists(oldAvatarPath);
      } catch (IOException e) {
        log.error("Failed to delete old avatar file: {}", e.getMessage());
        throw new AppException(ErrorCode.FILE_DELETE_FAILED);
      }
    }

    String avatar = UUID.randomUUID() + "_" + avatarFile.getOriginalFilename();
    Path uploadPath = Paths.get("uploads/users/", avatar);

    Files.createDirectories(uploadPath.getParent());
    Files.write(uploadPath, avatarFile.getBytes());
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
    String avatarUrl = baseUrl + "/uploads/users/" + avatar;
    profile.setAvatar(avatarUrl);

    return mapper.toDtoResponse(repository.save(profile));
  }

  @Override
  public ProfileResponse findByUserId(UUID userId) {
    return mapper.toDtoResponse(repository.findByUserId(userId)
        .orElseThrow(
            () -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID, getEntityClass())
        )
    );
  }

  @Override
  public void deleteAvatar() {
    CustomUserDetails userDetails = SecurityUtils.getCurrentUser();
    User user = userDetails.getUser();
    if (user == null) {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }
    Profile profile = repository.findByUserId(user.getId())
        .orElseThrow(
            () -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID, getEntityClass())
        );
    if (profile.getAvatar() != null) {
      try {
        Path avatarPath = Paths.get(profile.getAvatar());
        Files.deleteIfExists(avatarPath);
      } catch (IOException e) {
        log.error("Failed to delete avatar file: {}", e.getMessage());
        throw new AppException(ErrorCode.FILE_DELETE_FAILED);
      }
      profile.setAvatar(null);
      repository.save(profile);
    }
  }


}
