package com.booking.bookingbackend.data.base;

public interface EntityDtoMapper<E extends AbstractIdentifiable<?>, D> {

    D toDtoResponse(E entity);

    E toEntityFromDto(D dto);
}
