package com.booking.bookingbackend.service.reviews;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.ReviewCreationRequest;
import com.booking.bookingbackend.data.dto.response.ReviewResponse;
import com.booking.bookingbackend.data.entity.*;
import com.booking.bookingbackend.data.mapper.ReviewMapper;
import com.booking.bookingbackend.data.repository.BookingRepository;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.data.repository.ReviewRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.util.SecurityUtils;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class ReviewServiceImpl implements ReviewService {

  // Repositories
  ReviewRepository repository;
  PropertiesRepository propertiesRepository;
  UserRepository userRepository;
  BookingRepository bookingRepository;

  // Mappers
  ReviewMapper mapper;

  @Transactional
  @Override
  public ReviewResponse save(ReviewCreationRequest request) {
    // Get User
    CustomUserDetails userDetails = SecurityUtils.getCurrentUser();
    if (userDetails == null) {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }

    User user = userDetails.user();

    boolean exists = bookingRepository.getBooking(user.getId(),request.propertiesId()) == 1;
    if (!exists){
      throw new AppException(ErrorCode.MESSAGE_REVIEW_NOT_ALLOWED);
    }
    Review entity = mapper.toEntity(request);
    // Get Properties
    Properties properties = propertiesRepository.findById(request.propertiesId())
            .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
                    Properties.class.getSimpleName())
            );
    entity.setProperties(properties);

    entity.setUser(user);
    // Save Review
    log.info("Saving review for user: {}", user.getId());
    Review savedReview = repository.save(entity);

    // Update Properties Rating
    double rating = (properties.getRating().doubleValue() * properties.getTotalRating() +
            request.rating()) / (properties.getTotalRating() + 1);
    properties.setRating(BigDecimal.valueOf(rating));
    properties.setTotalRating(properties.getTotalRating() + 1);
    propertiesRepository.save(properties);

    // Map to DTO
    ReviewResponse response = mapper.toDtoResponse(savedReview);
    Profile profile = user.getProfile();
    response.setAvatar(profile.getAvatar());
    response.setName(profile.getFirstName());
    response.setNationality(profile.getNationality());
    return response;
  }
}
