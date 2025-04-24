package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Properties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

public interface PropertiesRepository extends BaseRepository<Properties, UUID> {
    Properties findByName(String name);
    Properties findByIdAndHostId(UUID id, UUID hostId);
    @Query("SELECT p FROM Properties p WHERE p.address = :location AND p.status = true AND " +
            "p.id NOT IN (SELECT b.properties.id FROM Booking b WHERE " +
            "(:startDate < b.checkOut AND :endDate > b.checkIn))")
    Page<Properties> findAvailableProperties(
            @Param("location") String location,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

}
