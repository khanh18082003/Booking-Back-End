package com.booking.bookingbackend.data.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.dto.response.CheckedAvailableAccommodationBookingResponse;
import com.booking.bookingbackend.data.entity.Available;
import com.booking.bookingbackend.data.projection.AvailableAccommodationDTO;

@Repository
public interface AvailableRepository extends BaseRepository<Available, Integer> {

    @Query(
            """
	SELECT new com.booking.bookingbackend.data.projection.AvailableAccommodationDTO(
			a.id,
			a.date,
			a.totalInventory,
			a.price,
			a.totalReserved)
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
            @Param("quantity") Integer quantity);

    @Query(
            value =
                    """
		select BIN_TO_UUID(t.id) AS id,
			t.name AS name,
				LEAST(MIN(a.total_inventory - a.total_reserved), :quantity) as quantity,
			SUM(a.price) * :quantity as totalPrice
		from (select * from tbl_accommodation where id = :id) as t
		inner join (select * from tbl_available where accommodation_id = :id) as a
		on t.id = a.accommodation_id
		where a.date between :checkInDate and :checkOutDate
		and a.total_inventory - a.total_reserved >= :quantity
		group by t.id, t.name
		having count(a.date) = :nights
	""",
            nativeQuery = true)
    Optional<CheckedAvailableAccommodationBookingResponse> checkAvailableAccommodation(
            @Param("id") UUID id,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("quantity") Integer quantity,
            @Param("nights") Integer nights);

    @Query(
            "SELECT av FROM Available av WHERE av.accommodation.id = :accommodationId AND av.date BETWEEN :checkInDate AND :checkOutDate")
    List<Available> findAllByAccommodationIdBetweenCheckInAndCheckOut(
            UUID accommodationId, LocalDate checkInDate, LocalDate checkOutDate);

    @Query("SELECT MAX(date) FROM Available")
    LocalDate findMaxDateInAvailable();

    List<Available> findAllByAccommodationId(UUID accommodationId);

    @Query("SELECT av FROM Available av WHERE av.accommodation.id = :accommodationId AND av.date IN :date")
    List<Available> findAllByAccommodationIdAndDate(UUID accommodationId, List<LocalDate> date);
}
