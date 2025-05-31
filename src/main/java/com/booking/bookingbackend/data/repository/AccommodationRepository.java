package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Accommodation;
import jakarta.persistence.Tuple;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccommodationRepository extends BaseRepository<Accommodation, UUID> {

  @Query("SELECT a FROM Accommodation a WHERE a.id = :id")
  Set<Accommodation> findAllById(Set<UUID> id);

  @Query(value = """
      WITH available_accommodations AS (SELECT accommodation_id,
                                               COUNT(DISTINCT date)                  AS available_days,
                                               MIN(total_inventory - total_reserved) AS min_available_rooms,
                                               SUM(price)                            AS total_price
                                        FROM tbl_available
                                        WHERE date BETWEEN :startDate AND :endDate
                                        GROUP BY accommodation_id
                                        HAVING COUNT(DISTINCT date) = :nights),
           room_with_beds AS (SELECT ahroom.accommodation_id,
                                     ahroom.id AS room_id,
                                     ahroom.room_name,
                                     JSON_ARRAYAGG(
                                             JSON_OBJECT(
                                                     'bed_type_name', bt.name,
                                                     'quantity', rhbed.quantity
                                             )
                                     )         AS beds
                              FROM accommodation_has_room ahroom
                                       JOIN room_has_bed rhbed ON rhbed.accommodation_room_id = ahroom.id
                                       JOIN tbl_bed_type bt ON bt.id = rhbed.bed_type_id
                              GROUP BY ahroom.accommodation_id, ahroom.id, ahroom.room_name),
           rooms_grouped AS (SELECT r.accommodation_id,
                                    JSON_ARRAYAGG(
                                            JSON_OBJECT(
                                                    'room_name', r.room_name,
                                                    'beds', r.beds
                                            )
                                    ) AS rooms
                             FROM room_with_beds r
                             GROUP BY r.accommodation_id),
           amenities_list AS (SELECT aca.accommodation_id,
                                     JSON_ARRAYAGG(
                                             JSON_OBJECT(
                                                     'name', am.name,
                                                     'icon', am.icon
                                             )
                                     ) AS amenities
                              FROM tbl_accommodation_amenities aca
                                       JOIN tbl_amenities am ON am.id = aca.amenities_id
                              GROUP BY aca.accommodation_id),
           images_list AS (SELECT i.reference_id,
                                  JSON_ARRAYAGG(i.url) AS image_urls
                           FROM tbl_images i
                           WHERE i.reference_type = 'ACCOMMODATION'
                           GROUP BY i.reference_id),
           valid_accommodations AS (SELECT BIN_TO_UUID(a.id)                                      AS accommodation_id,
                                           a.name,
                                           a.capacity,
                                           il.image_urls as image_urls,
                                           aa.min_available_rooms                                 as available_rooms,
                                           a.size,
                                           aa.total_price AS total_price,
                                           rg.rooms,
                                           a.description,
                                           al.amenities
                                    FROM tbl_accommodation a
                                             JOIN available_accommodations aa ON aa.accommodation_id = a.id
                                             JOIN rooms_grouped rg ON rg.accommodation_id = a.id
                                             JOIN amenities_list al ON al.accommodation_id = a.id
                                             JOIN images_list il ON UUID_TO_BIN(il.reference_id)=a.id
                                    WHERE a.properties_id = UUID_TO_BIN(:propertyId)
                                    GROUP BY a.id, a.name, a.capacity, aa.min_available_rooms, aa.total_price, rg.rooms,
                                             al.amenities,il.image_urls
                                    ORDER BY ABS(a.capacity - :capacity))
      SELECT *
      FROM valid_accommodations;
      
      """, nativeQuery = true)
  List<Tuple> searchAllByPropertyId(
      @Param("propertyId") String propertyId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("rooms") Integer rooms,
      @Param("nights") Integer nights,
      @Param("capacity") Integer capacity
  );

  List<Accommodation> findAllByPropertiesId(UUID id);
}
