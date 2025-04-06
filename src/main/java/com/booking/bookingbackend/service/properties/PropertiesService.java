package com.booking.bookingbackend.service.properties;

import com.booking.bookingbackend.data.dto.request.PropertiesRequest;
import com.booking.bookingbackend.data.dto.response.PropertiesResponse;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.service.BaseEntityService;

import java.util.UUID;

public interface PropertiesService extends BaseEntityService<UUID, Properties, PropertiesRepository, PropertiesResponse> { ;

    @Override
    default Class<?> getEntityClass(){return Properties.class;}
    PropertiesResponse save(PropertiesRequest request);
}
