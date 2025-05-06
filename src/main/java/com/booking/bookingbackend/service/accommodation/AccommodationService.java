package com.booking.bookingbackend.service.accommodation;

import com.booking.bookingbackend.data.dto.request.AccommodationCreationRequest;
import com.booking.bookingbackend.data.dto.request.AccommodationUpdateRequest;
import com.booking.bookingbackend.data.dto.response.AccommodationResponse;
import com.booking.bookingbackend.data.entity.Accommodation;
import com.booking.bookingbackend.data.repository.AccommodationRepository;
import com.booking.bookingbackend.service.BaseEntityService;

import java.util.Set;
import java.util.UUID;

public interface AccommodationService extends BaseEntityService<
    UUID,
    Accommodation,
    AccommodationRepository,
    AccommodationResponse> {
  default Class<?> getEntityClass(){return Accommodation.class;}
  AccommodationResponse save(AccommodationCreationRequest request);
  AccommodationResponse update(UUID id, AccommodationUpdateRequest request);
}
