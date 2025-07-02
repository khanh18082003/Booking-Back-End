package com.booking.bookingbackend.data.repository;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Tuple;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Properties;

public interface PropertiesRepository extends BaseRepository<Properties, UUID> {

    @Query(
            value =
                    """
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
			""",
            nativeQuery = true)
    Tuple findPropertiesDetail(@Param("id") UUID id);

    List<Properties> findAllByHostId(UUID hostId);
}
