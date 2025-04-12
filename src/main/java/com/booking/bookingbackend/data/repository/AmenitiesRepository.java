package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Amenities;

import java.util.Set;
import java.util.UUID;

public interface AmenitiesRepository extends BaseRepository<Amenities, Integer> {
    Set<Amenities> findAllById(Set<UUID> uuids);
}
