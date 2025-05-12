package com.booking.bookingbackend.service.reviews;

import com.booking.bookingbackend.data.dto.request.ReviewCreationRequest;
import com.booking.bookingbackend.data.dto.response.ReviewResponse;
import com.booking.bookingbackend.data.entity.Review;
import com.booking.bookingbackend.data.repository.ReviewRepository;
import com.booking.bookingbackend.service.BaseEntityService;

public interface ReviewService extends BaseEntityService<
    Integer,
    Review,
    ReviewRepository,
    ReviewResponse> {

  @Override
  default Class<?> getEntityClass() {
    return Review.class;
  }

  ReviewResponse save(ReviewCreationRequest request);
}
