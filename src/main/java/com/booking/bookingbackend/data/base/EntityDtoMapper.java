package com.booking.bookingbackend.data.base;

import org.mapstruct.MappingTarget;

public interface EntityDtoMapper<E extends AbstractIdentifiable<?>, D> {

  D toDtoResponse(E entity);

}
