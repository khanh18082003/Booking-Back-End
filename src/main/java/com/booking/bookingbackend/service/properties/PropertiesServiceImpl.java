package com.booking.bookingbackend.service.properties;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.PropertiesRequest;
import com.booking.bookingbackend.data.dto.response.PropertiesResponse;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.mapper.PropertiesMapper;
import com.booking.bookingbackend.data.repository.AmenitiesRepository;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.data.repository.PropertyTypeRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.exception.AppException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PropertiesServiceImpl implements PropertiesService {
    PropertiesRepository repository;
    UserRepository userRepository;
    PropertyTypeRepository propertyTypeRepository;
    AmenitiesRepository amenitiesRepository;
    PropertiesMapper mapper;

    @Override
    @Transactional
    public PropertiesResponse save(PropertiesRequest request) {
        Properties properties = mapper.toEntity(request);
        properties.setHost(userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID , getEntityClass().getSimpleName())));
        properties.setPropertyType(propertyTypeRepository.findById(request.typeId())
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID , getEntityClass().getSimpleName())));
        properties.setAmenities(amenitiesRepository.findAllById(request.amenitiesIds()));
        Properties savedProperties = repository.save(properties);
        return mapper.toDtoResponse(savedProperties);
    }

    @Override
    public List<PropertiesResponse> search(String location, LocalDate startDate, LocalDate endDate, int pageNo, int pageSize) {
        if (startDate.isAfter(endDate)) {
            throw new AppException(ErrorCode.MESSAGE_INVALID_DATE_RANGE, "Start date must be before end date");
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Properties> page = repository.findAvailableProperties(location, startDate, endDate, pageable);
        List<Properties> propertiesList = page.getContent();
        return propertiesList.stream()
                .map(mapper::toDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void changeStatus(UUID id) {
        Properties properties = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID , getEntityClass().getSimpleName()));
        properties.setStatus(false);
        repository.save(properties);
    }

    @Override
    public PropertiesResponse update(UUID id, PropertiesRequest request) {
        Properties properties = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID , getEntityClass().getSimpleName()));
        mapper.merge(request, properties);
        properties.setHost(userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID , getEntityClass().getSimpleName())));
        properties.setPropertyType(propertyTypeRepository.findById(request.typeId())
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID , getEntityClass().getSimpleName())));
        properties.setAmenities(amenitiesRepository.findAllById(request.amenitiesIds()));
        Properties updatedProperties = repository.save(properties);
        return mapper.toDtoResponse(updatedProperties);
    }
}
