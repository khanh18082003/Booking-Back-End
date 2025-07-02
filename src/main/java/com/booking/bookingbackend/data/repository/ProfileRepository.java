package com.booking.bookingbackend.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Profile;

@Repository
public interface ProfileRepository extends BaseRepository<Profile, UUID> {

    Optional<Profile> findByUserId(UUID uuid);
}
