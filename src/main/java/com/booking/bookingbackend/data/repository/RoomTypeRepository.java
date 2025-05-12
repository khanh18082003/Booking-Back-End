package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.RoomType;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends BaseRepository<RoomType, Integer> {
    // Custom query methods can be defined here if needed
}
