package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.RoomHasBed;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomHasBedRepository extends BaseRepository<RoomHasBed, Integer> {
    // Custom query methods can be defined here if needed
}
