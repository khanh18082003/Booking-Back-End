package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Profile;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends BaseRepository<Profile, UUID> {

}
