package com.booking.bookingbackend.data.repository;

import org.springframework.stereotype.Repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.PropertyType;

@Repository
public interface PropertyTypeRepository extends BaseRepository<PropertyType, Integer> {}
