package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Properties;

import java.util.List;
import java.util.UUID;

public interface PropertiesRepository extends BaseRepository<Properties, UUID> {
    Properties findByName(String name);
    Properties findByIdAndHostId(UUID id, UUID hostId);

}
