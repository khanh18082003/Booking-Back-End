package com.booking.bookingbackend.data.repository;

import org.springframework.stereotype.Repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.BedType;

@Repository
public interface BedTypeRepository extends BaseRepository<BedType, Integer> {
    // Custom query methods can be defined here if needed
}
