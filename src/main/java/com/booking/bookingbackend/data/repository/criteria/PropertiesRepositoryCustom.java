package com.booking.bookingbackend.data.repository.criteria;

import jakarta.persistence.Tuple;
import java.time.LocalDate;
import java.util.List;

public interface PropertiesRepositoryCustom {

  List<Tuple> searchPropertiesCustom(
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
  );
}
