package com.booking.bookingbackend.service.amenities;

import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.booking.bookingbackend.constant.AmenityType;
import com.booking.bookingbackend.data.dto.request.AmenitiesRequest;
import com.booking.bookingbackend.data.dto.response.AmenitiesResponse;
import com.booking.bookingbackend.data.entity.Amenities;
import com.booking.bookingbackend.data.mapper.AmenitiesMapper;
import com.booking.bookingbackend.data.projection.AmenitiesPropertiesDTO;
import com.booking.bookingbackend.data.repository.AmenitiesRepository;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AmenitiesServiceImpl implements AmenitiesService {

    AmenitiesRepository repository;
    AmenitiesMapper mapper;

    @Override
    @Transactional
    public AmenitiesResponse save(AmenitiesRequest request) {
        Amenities amenities = mapper.toEntity(request);
        amenities.setType(AmenityType.valueOf(request.type()));
        Amenities savedAmenities = repository.save(amenities);
        return mapper.toDtoResponse(savedAmenities);
    }

    @Override
    public AmenitiesResponse update(UUID id, AmenitiesRequest request) {
        Amenities amenities = repository.findById(id).orElseThrow(() -> new RuntimeException("Amenities not found"));
        amenities.setName(request.name());
        amenities.setType(AmenityType.valueOf(request.type()));
        Amenities updatedAmenities = repository.save(amenities);
        return mapper.toDtoResponse(updatedAmenities);
    }

    @Override
    public List<AmenitiesPropertiesDTO> getAmenitiesByPropertyIds(List<UUID> propertyIds) {
        return repository.findAndCountAmenitiesByProperties(propertyIds);
    }

    @Override
    public boolean delete(UUID id) {
        Amenities amenities = repository.findById(id).orElseThrow(() -> new RuntimeException("Amenities not found"));
        repository.delete(amenities);
        return true;
    }
}
