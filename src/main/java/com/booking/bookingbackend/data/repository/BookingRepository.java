package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Booking;
import com.booking.bookingbackend.data.projection.UserBookingsHistoryDTO;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends BaseRepository<Booking, UUID> {

  List<Booking> findAllByUserId(UUID userId);


  @Query(value = """
      SELECT BIN_TO_UUID(b.id)             AS bookingId,
             b.check_in       AS checkIn,
             b.check_out      AS checkOut,
             b.adult_units    AS adults,
             b.children_units AS children,
             b.total_price    AS totalPrice,
             b.status,
             BIN_TO_UUID(u.id)             AS user_id,
             gb.first_name    AS firstName,
             gb.last_name     AS lastName,
             gb.email         AS email,
             gb.phone_number  AS phone,
             gb.country       AS country,
             gb.note          AS note,
             BIN_TO_UUID(p.id)             AS propertyId,
             p.name           AS propertyName,
             p.image          AS propertyImage,
             p.address        AS propertyAddress,
             p.province       AS propertyProvince
      FROM (SELECT id,
                   check_in,
                   check_out,
                   adult_units,
                   children_units,
                   total_price,
                   status,
                   properties_id,
                   guest_id,
                   user_id
            FROM tbl_booking
            WHERE user_id = :user_id
            ORDER BY created_at DESC) AS b
               INNER JOIN (SELECT id FROM tbl_user WHERE id = :user_id) AS u
                          ON b.user_id = u.id
               INNER JOIN tbl_properties AS p ON b.properties_id = p.id
               INNER JOIN tbl_guest_booking AS gb ON b.guest_id = gb.id
      """,
      countQuery = """
              SELECT COUNT(*)
              FROM (SELECT id
                    FROM tbl_booking
                    WHERE user_id = :user_id) AS b
          """
      , nativeQuery = true)
  Page<UserBookingsHistoryDTO> findUserBookingsHistory(
      @Param("user_id") UUID userId,
      Pageable pageable
  );
}
