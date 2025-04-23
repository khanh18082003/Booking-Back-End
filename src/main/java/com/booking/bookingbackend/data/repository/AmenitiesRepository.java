package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Amenities;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenitiesRepository extends BaseRepository<Amenities, UUID> {

  @Query("SELECT a FROM Amenities a WHERE a.id IN :uuids")
  Set<Amenities> findAllById(Set<UUID> uuids);
}
