package com.booking.bookingbackend.service.accommodation;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.AccommodationCreationRequest;
import com.booking.bookingbackend.data.dto.response.AccommodationResponse;
import com.booking.bookingbackend.data.entity.Accommodation;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.mapper.AccommodationMapper;
import com.booking.bookingbackend.data.repository.AccommodationRepository;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.exception.AppException;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AccommodationServiceImpl implements AccommodationService {

  AccommodationRepository repository;
  AccommodationMapper mapper;
  PropertiesRepository propertiesRepository;

  @Transactional
  @Override
  public AccommodationResponse save(AccommodationCreationRequest request) {
    Accommodation entity = mapper.toEntity(request);
    Properties properties = propertiesRepository.findById(request.propertiesId())
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName()));
    entity.setProperties(properties);
    AccommodationResponse response = mapper.toDtoResponse(repository.save(entity));
    response.setPropertiesName(properties.getName());
    return response;
  }

  @Override
  public void update(UUID id, AccommodationCreationRequest request) {

  }
}
