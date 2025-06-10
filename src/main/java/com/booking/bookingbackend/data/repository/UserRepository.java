package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.dto.response.RevenueResponse;
import com.booking.bookingbackend.data.entity.User;
import jakarta.persistence.Tuple;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  @Query("SELECT u FROM User u JOIN FETCH u.roles r JOIN FETCH r.permissions WHERE u.email = :email")
  Optional<User> findByEmailJoinRoleWithPermission(String email);

  @Query(value = """
      SELECT BIN_TO_UUID(u.id) AS id,
             u.email AS email,
             u.is_active AS isActive,
             BIN_TO_UUID(p.id) AS profileId,
             p.avatar AS avatar,
             p.phone_number AS phone,
             p.dob AS dob,
             p.gender AS gender,
             p.address AS address,
             p.first_name AS firstName,
             p.last_name AS lastName,
             CONCAT(p.first_name, ' ', p.last_name) AS name,
             p.country_code AS countryCode,
            p.nationality as nationality
      FROM (SELECT id, email, is_active FROM tbl_user WHERE id = :id) u
               JOIN tbl_profile p ON p.user_id = u.id
      """, nativeQuery = true)
  Optional<Tuple> findByUserProfile(@Param("id") UUID id);

  @Query(value = """
          SELECT COUNT(*) AS totalBookings,
                 SUM(pay.amount) AS totalAmount
          FROM tbl_booking b
                   JOIN (SELECT id, host_id, status
                         FROM tbl_properties
                         WHERE host_id = :hostId
                           AND status = 1) p
                        ON b.properties_id = p.id
                   JOIN (SELECT id, status, amount, booking_id
                         FROM tbl_payment
                         WHERE status = 'COMPLETE') pay
                        ON pay.booking_id = b.id
      """, nativeQuery = true)
  RevenueResponse revenueByHostId(@Param("hostId") UUID userId);

  @Query(value = """
          SELECT COUNT(*)        AS totalBookings,
                 SUM(pay.amount) AS totalAmount
          FROM (SELECT id, properties_id, check_in, check_out
                FROM tbl_booking
                WHERE (MONTH(check_in) = :month OR MONTH(check_out) = :month)
                  AND YEAR(check_in) = :year) b
                   JOIN (SELECT id, host_id, status
                         FROM tbl_properties
                         WHERE host_id = :hostId
                           AND status = 1) p
                        ON b.properties_id = p.id
                   JOIN (SELECT id, status, amount, booking_id
                         FROM tbl_payment
                         WHERE status = 'COMPLETE') pay
                        ON pay.booking_id = b.id
      """, nativeQuery = true)
  RevenueResponse revenueByHostIdWithMonthAndYear(
      @Param("hostId") UUID userId,
      @Param("month") int month,
      @Param("year") int year
  );

  @Query(value = """
      WITH RECURSIVE
          months AS (SELECT 1 AS month
                     UNION ALL
                     SELECT month + 1
                     FROM months
                     WHERE month < 12),
          revenue_by_month AS (SELECT MONTH(b.check_in) AS month,
                                      COUNT(*)            AS totalBookings,
                                      SUM(pay.amount)     AS totalAmount
                               FROM (SELECT id, check_in, check_out, properties_id, created_at
                                     FROM tbl_booking
                                     WHERE YEAR(check_in) = :year) b
                                        JOIN (SELECT id, host_id, status
                                              FROM tbl_properties
                                              WHERE host_id = :hostId
                                                AND status = 1) p ON b.properties_id = p.id
                                        JOIN (SELECT id, status, amount, booking_id
                                              FROM tbl_payment
                                              WHERE status = 'COMPLETE') pay ON pay.booking_id = b.id
                               GROUP BY MONTH(b.check_in))
      SELECT COALESCE(r.totalBookings, 0) AS totalBookings,
             COALESCE(r.totalAmount, 0)   AS totalAmount
      FROM months m
               LEFT JOIN revenue_by_month r ON m.month = r.month
      ORDER BY m.month;
      """, nativeQuery = true)
  List<RevenueResponse> revenueByHostIdWithYear(
      @Param("hostId") UUID userId,
      @Param("year") int year
  );
}
