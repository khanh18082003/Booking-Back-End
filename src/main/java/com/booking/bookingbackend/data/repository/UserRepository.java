package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.dto.response.RevenueResponse;
import com.booking.bookingbackend.data.entity.User;
import jakarta.persistence.Tuple;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {

  boolean existsByEmail(String email);

  @EntityGraph(attributePaths = "roles")
  Optional<User> findByEmail(String email);

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
          SELECT
              b.created_at,
              p.name,
              a.name,
              bd.booked_units,
              b.check_in,
              b.check_out,
              b.total_price,
              b.status
          FROM tbl_booking b
          JOIN tbl_booking_detail bd ON b.id = bd.booking_id
          JOIN tbl_accommodation a ON bd.accommodation_id = a.id
          JOIN tbl_properties p ON b.properties_id = p.id
          WHERE p.host_id = :userId
          ORDER BY b.created_at DESC
      """, nativeQuery = true)
  List<RevenueResponse> RevenueByHostId(@Param("userId") UUID userId);

}
