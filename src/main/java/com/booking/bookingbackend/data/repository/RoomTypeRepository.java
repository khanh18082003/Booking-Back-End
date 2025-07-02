package com.booking.bookingbackend.data.repository;

import org.springframework.stereotype.Repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.RoomType;

@Repository
public interface RoomTypeRepository extends BaseRepository<RoomType, Integer> {
    // Custom query methods can be defined here if needed
}
