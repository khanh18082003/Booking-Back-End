package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.PropertyType;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyTypeRepository extends BaseRepository<PropertyType, Integer> {
}
