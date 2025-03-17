package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.User;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {

}
