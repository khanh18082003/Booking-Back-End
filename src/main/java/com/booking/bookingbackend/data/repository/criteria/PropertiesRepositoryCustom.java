package com.booking.bookingbackend.data.repository.criteria;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Tuple;

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
            String... sort);
}
