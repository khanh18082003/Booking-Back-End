package com.booking.bookingbackend.data.mapper;

import org.mapstruct.Mapper;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.ReviewCreationRequest;
import com.booking.bookingbackend.data.dto.response.ReviewResponse;
import com.booking.bookingbackend.data.entity.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper extends EntityDtoMapper<Review, ReviewResponse> {
    Review toEntity(ReviewCreationRequest dto);
}
