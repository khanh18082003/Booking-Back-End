package com.booking.bookingbackend.service.amenities;

import com.booking.bookingbackend.data.dto.request.AmenitiesRequest;
import com.booking.bookingbackend.data.dto.response.AmenitiesResponse;
import com.booking.bookingbackend.data.entity.Amenities;
import com.booking.bookingbackend.data.mapper.AmenitiesMapper;
import com.booking.bookingbackend.data.repository.AmenitiesRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Getter
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AmenitiesServiceImpl implements AmenitiesService{
    AmenitiesRepository repository;
    AmenitiesMapper mapper;


    @Override
    @Transactional
    public AmenitiesResponse save(AmenitiesRequest request) {
        Amenities amenities = mapper.toEntity(request);
        amenities.setName(request.name());
        amenities.setIcon(request.icon());
        amenities.setType(request.type());
        Amenities savedAmenities = repository.save(amenities);
        return mapper.toDtoResponse(savedAmenities);
    }

    @Override
    public AmenitiesResponse update(UUID id, AmenitiesRequest request) {
        //can viet
        return null;
    }

}
