package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Available;
import com.booking.bookingbackend.data.projection.AvailableAccommodationDTO;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableRepository extends BaseRepository<Available, Integer> {

  @Query("""
      SELECT new com.booking.bookingbackend.data.projection.AvailableAccommodationDTO(a.id, a.date, a.totalInventory, a.price, a.totalReserved) 
            FROM Available a WHERE
      a.accommodation.id = :accommodationId AND
      a.date BETWEEN :checkInDate AND :checkOutDate AND
      a.totalInventory - a.totalReserved >= :quantity
      """)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  List<AvailableAccommodationDTO> findAndLockAvailableAccommodation(
      @Param("accommodationId") UUID accommodationId,
      @Param("checkInDate") LocalDate checkInDate,
      @Param("checkOutDate") LocalDate checkOutDate,
      @Param("quantity") Integer quantity
  );
}
