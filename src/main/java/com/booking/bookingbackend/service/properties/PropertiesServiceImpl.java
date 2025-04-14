package com.booking.bookingbackend.service.properties;

import com.booking.bookingbackend.data.dto.request.PropertiesRequest;
import com.booking.bookingbackend.data.dto.response.PropertiesResponse;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.mapper.PropertiesMapper;
import com.booking.bookingbackend.data.repository.AmenitiesRepository;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.data.repository.PropertyTypeRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
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
        properties.setHost(userRepository.findByEmail(request.email()).orElseThrow());
        properties.setPropertyType(propertyTypeRepository.findById(request.typeId()).orElseThrow());
        properties.setAmenities(amenitiesRepository.findAllById(request.amenitiesIds()));
        Properties savedProperties = repository.save(properties);
        return mapper.toDtoResponse(savedProperties);
    }

    @Override
    public PropertiesResponse search(String location, Long startDate, Long endDate) {
//        List<Properties> propertiesList = repository.findByLocationAndDateBetween(location, startDate, endDate);
//        return (PropertiesResponse) propertiesList.stream()
//                .map(mapper::toDtoResponse)
//                .collect(Collectors.toList());
        return null;
    }


}
