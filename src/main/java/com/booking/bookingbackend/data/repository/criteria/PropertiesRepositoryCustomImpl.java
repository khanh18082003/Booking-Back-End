package com.booking.bookingbackend.data.repository.criteria;

import com.booking.bookingbackend.util.ParsePatternUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import java.time.LocalDate;
import java.util.List;
import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PropertiesRepositoryCustomImpl implements PropertiesRepositoryCustom {

  @PersistenceContext
  private final EntityManager entityManager;

  @Override
  public List<Tuple> searchPropertiesCustom(
      Double latitude,
      Double longitude,
      Double radius,
      LocalDate startDate,
      LocalDate endDate,
      Integer nights,
      Integer guests,
      Integer adults,
      Integer children,
      Integer rooms,
      String[] filters,
      String... sort
  ) {

    String orderBy = ParsePatternUtil.parseSortPattern(sort);

    List<SearchOperation> searchOperations = ParsePatternUtil.parseFilterPattern(filters);

    StringBuilder sql = new StringBuilder("""
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
                                      ORDER BY total_price)
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
        """);

    if (!searchOperations.isEmpty()) {
      StringJoiner typeJoiner = new StringJoiner(", ");
      StringJoiner amenitiesJoiner = new StringJoiner(", ");
      for (SearchOperation operation : searchOperations) {
        if (operation.getKey().equals("properties_type")) {
          typeJoiner.add("'" + operation.getValue() + "'");
        } else if (operation.getKey().equals("amenities")) {
          amenitiesJoiner.add("'" + operation.getValue() + "'");
        }
      }
      if (amenitiesJoiner.length() > 0) {
        sql.append("\nJOIN tbl_properties_amenities pa ON pa.properties_id = fp.id\n")
            .append("JOIN tbl_amenities a ON a.id = pa.amenities_id\n");
        sql.append("WHERE a.name IN (")
            .append(amenitiesJoiner)
            .append(")\n");
      }
      if (typeJoiner.length() > 0) {
        if (amenitiesJoiner.length() > 0) {
          sql.append("AND ");
        } else {
          sql.append("WHERE ");
        }
        sql.append("fp.properties_type IN (")
            .append(typeJoiner)
            .append(")\n");
      }
    }

    sql.append(
        "\nGROUP BY fp.id, fp.name, fp.address, fp.city, fp.district, fp.rating, fp.distance");

    Query query = entityManager.createNativeQuery(sql.toString(), Tuple.class);
    query.setParameter("latitude", latitude);
    query.setParameter("longitude", longitude);
    query.setParameter("radius", radius);
    query.setParameter("startDate", startDate);
    query.setParameter("endDate", endDate);
    query.setParameter("nights", nights);
    query.setParameter("guests", guests);
    query.setParameter("adults", adults);
    query.setParameter("children", children);
    query.setParameter("rooms", rooms);
    return query.getResultList();
  }
}
