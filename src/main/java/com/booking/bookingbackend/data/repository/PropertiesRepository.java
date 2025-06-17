package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.projection.PropertiesDetailDTO;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertiesRepository extends BaseRepository<Properties, UUID> {

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

    List<Properties> findAllByHostId(UUID hostId);

    @Query(value = """
            select BIN_TO_UUID(p.id)  as id,
                   p.name             as name,
                   p.description      as description,
                   p.address          as address,
                   p.city             as city,
                   p.latitude         as latitude,
                   p.longitude        as longitude,
                   p.image            as image,
                   p.rating           as rating,
                   p.total_rating     as totalRating,
                   p.status           as status,
                   p.check_in_time    as checkInTime,
                   p.check_out_time   as checkOutTime,
                   p.typeName         as propertyType
            from (select properties.*, tbl_property_type.name AS typeName
                  from tbl_properties properties
                           inner join tbl_property_type on properties.type_id = tbl_property_type.id
                  where properties.id = :id) as p
                     inner join (select a.id, a.properties_id
                                 from (select id, properties_id from tbl_accommodation where properties_id = :id) a
                                          inner join
                                      (select id, accommodation_id, date
                                       from tbl_available
                                       where date between :checkIn and :checkOut) av
                                      on a.id = av.accommodation_id
                                 group by a.id
                                 having count(av.id) = DATEDIFF(:checkOut, :checkIn) + 1) as acc_avai
                                on acc_avai.properties_id = p.id
            group by p.id
            """, nativeQuery = true)
    Tuple findPropertiesDetailByIdWithCheckInAndCheckOut(
            UUID id,
            LocalDate checkIn,
            LocalDate checkOut
    );
}
