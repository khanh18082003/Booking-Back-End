package com.booking.bookingbackend.service.properties;

import com.booking.bookingbackend.data.dto.request.PropertiesRequest;
import com.booking.bookingbackend.data.dto.request.PropertiesSearchRequest;
import com.booking.bookingbackend.data.dto.response.PaginationResponse;
import com.booking.bookingbackend.data.dto.response.PropertiesResponse;
import com.booking.bookingbackend.data.dto.response.ReviewResponse;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.projection.PropertiesDTO;
import com.booking.bookingbackend.data.projection.PropertiesDetailDTO;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.service.BaseEntityService;
import java.util.UUID;

public interface PropertiesService extends
    BaseEntityService<UUID, Properties, PropertiesRepository, PropertiesResponse> {

  @Override
  default Class<?> getEntityClass() {
    return Properties.class;
  }

  PropertiesResponse save(PropertiesRequest request);

  PaginationResponse<PropertiesDTO> searchProperties(
      PropertiesSearchRequest request,
      int pageNo,
      int pageSize,
      String[] filters,
      String... sorts
  );

  void changeStatus(UUID id);

  PropertiesResponse update(UUID id, PropertiesRequest request);

  PropertiesDetailDTO getPropertiesDetail(UUID id);

  PaginationResponse<ReviewResponse> getPropertiesReviews(UUID id, int pageNo, int pageSize);
}
