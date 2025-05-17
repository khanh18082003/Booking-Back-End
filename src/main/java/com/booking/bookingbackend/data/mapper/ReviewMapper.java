package com.booking.bookingbackend.data.mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.ReviewCreationRequest;
import com.booking.bookingbackend.data.dto.response.ReviewResponse;
import com.booking.bookingbackend.data.entity.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper extends EntityDtoMapper<Review, ReviewResponse> {
  Review toEntity(ReviewCreationRequest dto);
}
