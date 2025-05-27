package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Amenities;
import com.booking.bookingbackend.data.projection.AmenitiesPropertiesDTO;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenitiesRepository extends BaseRepository<Amenities, UUID> {

  @Query("SELECT a FROM Amenities a WHERE a.id IN :uuids")
  Set<Amenities> findAllById(Set<UUID> uuids);

  @Query(value = """
      select BIN_TO_UUID(ta.id) AS id,
             ta.name AS name,
             COUNT(ta.id) AS quantity
      from tbl_amenities ta
      INNER JOIN (select * from tbl_properties_amenities where properties_id IN :property_ids) tpa ON tpa.amenities_id = ta.id
      INNER JOIN (select id from tbl_properties where id IN :property_ids) tp ON tp.id = tpa.properties_id
      group by ta.id, ta.name
      """, nativeQuery = true)
  List<AmenitiesPropertiesDTO> findAndCountAmenitiesByProperties(@Param("property_ids") List<UUID> propertyIds);
}
