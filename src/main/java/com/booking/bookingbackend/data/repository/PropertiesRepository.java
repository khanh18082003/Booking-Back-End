package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Properties;
import jakarta.persistence.Tuple;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertiesRepository extends BaseRepository<Properties, UUID> {

  Properties findByName(String name);

  Properties findByIdAndHostId(UUID id, UUID hostId);


  @Query(value = """
      -- Create spatial point for the search location
      WITH destination AS (SELECT ST_GeomFromText(CONCAT('POINT(', :longitude, ' ', :latitude, ')'),
                                                  3857) AS geom),
           -- Filter properties by distance with optimized join
           filtered_properties AS (SELECT p.*,
                                          pt.name                     AS properties_type,
                                          ST_Distance(p.geom, d.geom) AS distance
                                   FROM tbl_properties p
                                            CROSS JOIN destination d
                                            JOIN tbl_property_type pt ON p.type_id = pt.id
                                   -- Apply distance filter early
                                   WHERE ST_Distance(p.geom, d.geom) <= :radius),
           -- Find accommodations with availability for the entire stay period
           available_accommodations AS (SELECT accommodation_id,
                                               COUNT(DISTINCT date)                  AS available_days,
                                               MIN(total_inventory - total_reserved) AS min_available_rooms,
                                               SUM(price)                            AS total_price
                                        FROM tbl_available
                                        WHERE date BETWEEN :startDate AND :endDate
                                          AND (total_inventory - total_reserved) > 0 -- Only consider rooms with actual availability
                                        GROUP BY accommodation_id
                                        HAVING COUNT(DISTINCT date) = :nights),
           -- Calculate accommodation capacity and pricing details
           valid_accommodations AS (SELECT a.id                                                        AS accommodation_id,
                                           a.name,
                                           a.capacity,
                                           a.properties_id,
                                           LEAST(aa.min_available_rooms, :rooms)                       AS suggested_quantity,
                                           a.capacity * LEAST(aa.min_available_rooms, :rooms)          AS total_capacity,
                                           SUM(rhbed.quantity * LEAST(aa.min_available_rooms, :rooms)) AS total_beds,
                                           aa.total_price * LEAST(aa.min_available_rooms, :rooms)      AS total_price,
                                           JSON_ARRAYAGG(
                                                   JSON_OBJECT(
                                                           'bed_type_name',  bt.name,
                                                           'quantity', rhbed.quantity
                                                   )
                                           )                                                           AS bed_names
                                    FROM tbl_accommodation a
                                             JOIN available_accommodations aa ON aa.accommodation_id = a.id
                                             JOIN accommodation_has_room ahroom
                                                  ON ahroom.accommodation_id = a.id
                                             JOIN room_has_bed rhbed
                                                  ON rhbed.accommodation_room_id = ahroom.id
                                             JOIN tbl_bed_type bt ON bt.id = rhbed.bed_type_id
                                    GROUP BY a.id, a.name, a.capacity, a.properties_id,
                                             aa.min_available_rooms, aa.total_price
                                    HAVING (total_capacity >= :guests AND suggested_quantity <= :rooms)
                                        OR (total_capacity < :guests AND suggested_quantity < :rooms)
                                    ORDER BY total_price / total_capacity)
      -- Final result with property details and accommodation options
      SELECT BIN_TO_UUID(fp.id) AS propertiesId,
             fp.name            AS propertiesName,
             fp.image           AS image,
             fp.latitude        AS latitude,
             fp.longitude       AS longitude,
             fp.address         AS address,
             fp.city            AS city,
             fp.district        AS district,
             fp.rating          AS rating,
             fp.total_rating    AS totalRating,
             fp.distance        AS distance,
             fp.properties_type AS propertiesType,
             :nights            AS nights,
             :adults            AS adults,
             :children          AS children,
             JSON_ARRAYAGG(
                     JSON_OBJECT(
                             'accommodation_id', BIN_TO_UUID(va.accommodation_id),
                             'accommodation_name', va.name,
                             'suggested_quantity', va.suggested_quantity,
                             'total_capacity', va.total_capacity,
                             'total_beds', va.total_beds,
                             'total_price', va.total_price,
                             'bed_names', va.bed_names
                     )
             )                  AS accommodations
      FROM filtered_properties fp
               JOIN valid_accommodations va ON va.properties_id = fp.id
      GROUP BY fp.id, fp.name, fp.address, fp.city, fp.district, fp.rating, fp.distance
      ORDER BY fp.distance;
      """,
      nativeQuery = true)
  List<Tuple> searchProperties(
      @Param("latitude") Double latitude,
      @Param("longitude") Double longitude,
      @Param("radius") Double radius,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("nights") Integer nights,
      @Param("guests") Integer guests,
      @Param("adults") Integer adults,
      @Param("children") Integer children,
      @Param("rooms") Integer rooms
  );

  @Query(value = """
            SELECT\s
          BIN_TO_UUID(p.id) AS id,
          p.name AS name,
          p.description AS description,
          p.image AS image,
          p.address AS address,
          p.rating AS rating,
          p.total_rating AS totalRating,
          p.status AS status,
          p.latitude AS latitude,
          p.longitude AS longitude,
          p.check_in_time AS checkInTime,
          p.check_out_time AS checkOutTime,
          pt.name AS propertyType,
          (SELECT\s
              JSON_ARRAYAGG(
                  JSON_OBJECT(
                      'name', DISTINCT_NAME.name,
                      'icon', DISTINCT_NAME.icon
                  )
              )
           FROM (
               SELECT DISTINCT a.name, a.icon
               FROM tbl_amenities a
               JOIN tbl_properties_amenities pa ON a.id = pa.amenities_id
               WHERE pa.properties_id = p.id
           ) AS DISTINCT_NAME
          ) AS amenities,
          (SELECT\s
              JSON_ARRAYAGG(i.url)
           FROM tbl_images i\s
           WHERE UUID_TO_BIN(i.reference_id) = p.id AND i.reference_type = 'PROPERTIES'
          ) AS image_urls
      FROM
          (SELECT * FROM tbl_properties WHERE id = :id) p
      JOIN\s
          tbl_property_type pt ON pt.id = p.type_id
      """, nativeQuery = true)
  Tuple findPropertiesDetail(@Param("id") UUID id);



}
