package com.booking.bookingbackend.service.amenities;

import com.booking.bookingbackend.data.dto.request.AmenitiesRequest;
import com.booking.bookingbackend.data.dto.response.AmenitiesResponse;
import com.booking.bookingbackend.data.entity.Amenities;
import com.booking.bookingbackend.data.projection.AmenitiesPropertiesDTO;
import com.booking.bookingbackend.data.repository.AmenitiesRepository;
import com.booking.bookingbackend.service.BaseEntityService;
import java.util.List;
import java.util.UUID;

public interface AmenitiesService extends
    BaseEntityService<UUID, Amenities, AmenitiesRepository, AmenitiesResponse> {

  @Override
  default Class<?> getEntityClass() {
    return Amenities.class;
  }

  AmenitiesResponse save(AmenitiesRequest request);

  AmenitiesResponse update(UUID id, AmenitiesRequest request);

  List<AmenitiesPropertiesDTO> getAmenitiesByPropertyIds(List<UUID> propertyIds);
}
