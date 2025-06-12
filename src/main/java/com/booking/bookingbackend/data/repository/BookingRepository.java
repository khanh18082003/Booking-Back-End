package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Booking;
import com.booking.bookingbackend.data.projection.BookingDetailResponse;
import com.booking.bookingbackend.data.projection.UserBookingsHistoryDTO;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Tuple;
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
            b.check_in                    AS checkIn,
            b.check_out                   AS checkOut,
            b.adult_units                 AS adults,
            b.children_units              AS children,
            b.total_price                 AS totalPrice,
            b.status,
            BIN_TO_UUID(u.id)             AS user_id,
            gb.first_name                 AS firstName,
            gb.last_name                  AS lastName,
            gb.email                      AS email,
            gb.phone_number               AS phone,
            gb.country                    AS country,
            gb.note                       AS note,
            BIN_TO_UUID(p.id)             AS propertyId,
            p.name                        AS propertyName,
            p.image                       AS propertyImage,
            p.address                     AS propertyAddress,
            p.province                    AS propertyProvince,
            BIN_TO_UUID(pay.id)                        AS paymentId,
            pay.status                    AS paymentStatus,
            pay.url_image                 AS paymentImage,
            pay.payment_method            AS paymentMethod,
            pay.transaction_id            AS transactionId
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
            INNER JOIN tbl_payment AS pay ON pay.booking_id = b.id
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

  @Query(value = """
          SELECT exists (
              SELECT 1
              FROM tbl_booking b
              WHERE b.user_id = :userId
                AND b.properties_id = :propertiesId
                AND b.status = 'COMPLETE'
          )
      """, nativeQuery = true)
  Long getBooking(@Param("userId") UUID userId, @Param("propertiesId") UUID propertiesId);

  @Query(value = """
          
              WITH accommodation AS (
              SELECT
                  tbd.booking_id AS booking_id,
                  ta.name,
                  tbd.booked_units AS quantity
              FROM tbl_booking_detail tbd
                       JOIN tbl_accommodation ta ON tbd.accommodation_id = ta.id
          )
          SELECT
              BIN_TO_UUID(b.id) AS id,
              b.check_in AS check_in,
              b.check_out AS check_out,
              b.adult_units AS adult_units,
              b.children_units AS children_units,
              b.total_price AS total_price,
              b.status AS status,
              prop.name AS property_name,
              CONCAT(gb.first_name, ' ', gb.last_name) AS full_name,
              gb.email AS email,
              gb.phone_number AS phone,
              p.status AS payment_status,
              p.payment_method AS payment_method,
              JSON_ARRAYAGG(
                      JSON_OBJECT(
                              'name', acc.name,
                              'quality', acc.quantity
                      )
              ) AS accommodations,
            b.created_at AS created_at
          FROM tbl_booking b
                   JOIN tbl_properties prop ON b.properties_id = prop.id
                   JOIN tbl_guest_booking gb ON gb.id = b.guest_id
                   JOIN tbl_payment p ON p.booking_id = b.id
                   JOIN accommodation acc ON acc.booking_id = b.id
          WHERE b.properties_id = :propertiesId
          GROUP BY
              b.id,
              b.check_in,
              b.check_out,
              b.adult_units,
              b.children_units,
              b.total_price,
              b.status,
              prop.name,
              gb.first_name,
              gb.last_name,
              gb.email,
              gb.phone_number,
              p.status,
              p.payment_method,
              b.created_at
          """,
      countQuery = """
              
                  SELECT COUNT(DISTINCT b.id)
              FROM tbl_booking b
              WHERE b.properties_id = :propertiesId
              """, nativeQuery = true)
  Page<Tuple> findAllByPropertiesId(
      UUID propertiesId,
      Pageable pageable
  );

  @Query(value = """
          
              WITH
              property AS (
                  SELECT id, name
                  FROM tbl_properties
                  WHERE host_id = :hostId
              ),
              accommodation AS (
                  SELECT
                      tbd.booking_id AS booking_id,
                      ta.name,
                      tbd.booked_units AS quantity
                  FROM tbl_booking_detail tbd
                           JOIN tbl_accommodation ta ON tbd.accommodation_id = ta.id
              )
          SELECT
              BIN_TO_UUID(b.id) AS id,
              b.check_in AS check_in,
              b.check_out AS check_out,
              b.adult_units AS adult_units,
              b.children_units AS children_units,
              b.total_price AS total_price,
              b.status AS status,
              property.name AS property_name,
              CONCAT(gb.first_name, ' ', gb.last_name) AS full_name,
              gb.email AS email,
              gb.phone_number AS phone,
              p.status AS payment_status,
              p.payment_method AS payment_method,
              JSON_ARRAYAGG(
                      JSON_OBJECT(
                              'name', acc.name,
                              'quality', acc.quantity
                      )
              ) AS accommodations,
            b.created_at AS created_at
          FROM tbl_booking b
                   JOIN property ON b.properties_id = property.id
                   JOIN tbl_guest_booking gb ON gb.id = b.guest_id
                   JOIN tbl_payment p ON p.booking_id = b.id
                   JOIN accommodation acc ON acc.booking_id = b.id
          GROUP BY
              b.id,
              b.check_in,
              b.check_out,
              b.adult_units,
              b.children_units,
              b.total_price,
              b.status,
              property.name,
              gb.first_name,
              gb.last_name,
              gb.email,
              gb.phone_number,
              p.status,
              p.payment_method,
            b.created_at
          """,
      countQuery = """
              
                  SELECT COUNT(DISTINCT b.id)
              FROM tbl_booking b
              JOIN tbl_properties p ON b.properties_id = p.id
              WHERE p.host_id = :hostId
              """, nativeQuery = true)
  Page<Tuple> findAllOfHost(
      UUID hostId,
      Pageable pageable
  );
}
