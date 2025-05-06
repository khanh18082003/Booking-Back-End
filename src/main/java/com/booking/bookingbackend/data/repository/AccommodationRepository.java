package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Accommodation;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;
import java.util.UUID;

public interface AccommodationRepository extends BaseRepository<Accommodation, UUID> {
    @Query("SELECT a FROM Accommodation a WHERE a.id = :id")
    Set<Accommodation> findAllById(Set<UUID> id);
}
