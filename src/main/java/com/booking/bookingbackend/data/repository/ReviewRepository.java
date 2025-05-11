package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Review;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends BaseRepository<Review, Integer> {

}
