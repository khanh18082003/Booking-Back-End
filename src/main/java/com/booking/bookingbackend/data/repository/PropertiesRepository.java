package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.projection.PropertiesDTO;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertiesRepository extends BaseRepository<Properties, UUID> {

  Properties findByName(String name);

  Properties findByIdAndHostId(UUID id, UUID hostId);


  @Query(value = """
      WITH destination AS (SELECT ST_GeomFromText(CONCAT('POINT(', :longitude, ' ', :latitude, ')'),
                                                  3857) AS geom)
      SELECT name,
             address,
             city,
             country,
             rating,
             latitude,
             longitude,
             vh.night_amount,
             vh.total_amount,
             ST_Distance(tp.geom, d.geom) AS distance
      FROM tbl_properties tp
               JOIN (SELECT p.id,
                            AVG(t_acc_avai.price) AS night_amount,
                            SUM(t_acc_avai.price) AS total_amount
                     FROM (SELECT t_pro.*
                           FROM tbl_properties t_pro
                                    JOIN destination d2 on 1 = 1
                           WHERE ST_Distance(t_pro.geom, d2.geom) <= :radius) p
                              JOIN (SELECT ta.id, properties_id, t_avai.price, t_avai.date
                                    FROM (SELECT * FROM tbl_accommodation WHERE capacity >= :guests) ta
                                             JOIN (SELECT *
                                                   FROM tbl_available
                                                   WHERE (date BETWEEN :startDate AND :endDate)
                                                     AND (total_inventory - total_reserved > 0)) t_avai
                                                  ON ta.id = t_avai.accommodation_id) t_acc_avai
                                   ON p.id = t_acc_avai.properties_id
                     GROUP BY p.id
                     HAVING COUNT(t_acc_avai.date) = :nights) AS vh
                    ON tp.id = vh.id
               JOIN destination d ON 1 = 1
      ORDER BY distance;
      """,
      countQuery = """
          
            WITH destination AS (
              SELECT ST_GeomFromText(CONCAT('POINT(', :longitude, ' ', :latitude, ')'), 3857) AS geom
          )
          SELECT COUNT(*)
          FROM tbl_properties tp
                   JOIN (
                       SELECT p.id
                       FROM (
                                SELECT t_pro.*
                                FROM tbl_properties t_pro
                                         JOIN destination d2 ON 1 = 1
                                WHERE ST_Distance(t_pro.geom, d2.geom) <= :radius
                            ) p
                                JOIN (
                                    SELECT ta.id, properties_id, t_avai.price, t_avai.date
                                    FROM (SELECT * FROM tbl_accommodation WHERE capacity >= :guests) ta
                                             JOIN (
                                                SELECT *
                                                FROM tbl_available
                                                WHERE (date BETWEEN :startDate AND :endDate)
                                                  AND (total_inventory - total_reserved > 0)
                                             ) t_avai ON ta.id = t_avai.accommodation_id
                                ) t_acc_avai ON p.id = t_acc_avai.properties_id
                       GROUP BY p.id
                       HAVING COUNT(t_acc_avai.date) = :nights
                   ) AS vh ON tp.id = vh.id
                   JOIN destination d ON 1 = 1;
          """,
      nativeQuery = true)
  Page<PropertiesDTO> searchProperties(
      @Param("latitude") Double latitude,
      @Param("longitude") Double longitude,
      @Param("radius") Double radius,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("nights") Integer nights,
      @Param("guests") Integer guests,
      Pageable pageable
  );

}
