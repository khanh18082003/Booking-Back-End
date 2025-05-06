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
      WITH destination AS (
          SELECT ST_GeomFromText(CONCAT('POINT(', :longitude, ' ', :latitude, ')'), 3857) AS geom
      ),
           filtered_properties AS (
               SELECT p.*, pt.name as properties_type, ST_Distance(p.geom, d.geom) AS distance
               FROM tbl_properties p
                JOIN destination d ON 1 = 1
                JOIN tbl_property_type pt ON p.type_id = pt.id
               WHERE ST_Distance(p.geom, d.geom) <= :radius
           ),
           available_accommodations AS (
               SELECT
                   accommodation_id,
                   COUNT(DISTINCT date) AS available_days,
                   MIN(total_inventory - total_reserved) AS min_available_rooms,
                   SUM(price) AS total_price
               FROM tbl_available
               WHERE date BETWEEN :startDate AND :endDate
               GROUP BY accommodation_id
               HAVING COUNT(DISTINCT date) = :nights
           ),
           valid_accommodations AS (
               SELECT
                   a.id AS accommodation_id,
                   a.name,
                   a.capacity,
                   a.properties_id,
      
                   LEAST(aa.min_available_rooms, :rooms) AS suggested_quantity,
                   a.capacity * LEAST(aa.min_available_rooms, :rooms) AS total_capacity,
                   SUM(rhbed.quantity * LEAST(aa.min_available_rooms, :rooms)) AS total_beds,
                   aa.total_price * LEAST(aa.min_available_rooms, :rooms) AS total_price,
                   JSON_ARRAYAGG(bt.name) AS bed_names
      
               FROM tbl_accommodation a
                        JOIN accommodation_has_room ahroom ON ahroom.accommodation_id = a.id
                        JOIN room_has_bed rhbed ON rhbed.accommodation_room_id = ahroom.id
                        JOIN tbl_bed_type bt ON bt.id = rhbed.bed_type_id
                        JOIN available_accommodations aa ON aa.accommodation_id = a.id
      
               GROUP BY
                   a.id, a.name, a.capacity, a.properties_id,
                   aa.min_available_rooms, aa.total_price
                ORDER BY total_price / total_capacity
           )
      
      SELECT
            BIN_TO_UUID(fp.id) AS propertiesId,
            fp.name AS propertiesName,
            fp.image AS image,
            fp.address AS address,
            fp.city AS city,
            fp.district AS district,
            fp.rating AS rating,
            fp.distance AS distance,
            fp.properties_type AS propertiesType,
            :nights AS nights,
            :adults AS adults,
            :children AS children,
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
          ) AS accommodations
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

}
