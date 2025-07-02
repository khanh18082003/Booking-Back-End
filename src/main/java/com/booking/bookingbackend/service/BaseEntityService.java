package com.booking.bookingbackend.service;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.base.AbstractIdentifiable;
import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.exception.AppException;

@NoRepositoryBean
public interface BaseEntityService<
        I extends Serializable,
        E extends AbstractIdentifiable<I>,
        R extends BaseRepository<E, I>,
        D extends Serializable> {

    R getRepository();

    EntityDtoMapper<E, D> getMapper();

    Class<?> getEntityClass();

    default D findById(I id) {
        return getMapper()
                .toDtoResponse(getRepository()
                        .findById(id)
                        .orElseThrow(() -> new AppException(
                                ErrorCode.MESSAGE_INVALID_ENTITY_ID,
                                getEntityClass().getSimpleName())));
    }

    default Page<D> findAll(Pageable pageable) {
        return getRepository().findAll(pageable).map(entity -> getMapper().toDtoResponse(entity));
    }

    default Page<D> findAll(Specification<E> specification, Pageable pageable) {
        return getRepository().findAll(specification, pageable).map(entity -> getMapper()
                .toDtoResponse(entity));
    }

    default boolean delete(I id) {
        var repository = getRepository();

        return repository
                .findById(id)
                .map(found -> {
                    repository.delete(found);

                    return true;
                })
                .isPresent();
    }

    default boolean delete(Iterable<I> ids) {
        var matchedByIds = getRepository().findAllById(ids);

        return deleteInternal(matchedByIds);
    }

    default boolean delete(Specification<E> specification) {
        var matchedByIds = getRepository().findAll(specification);

        return deleteInternal(matchedByIds);
    }

    private boolean deleteInternal(Iterable<? extends E> matchedByIds) {
        getRepository().deleteAll(matchedByIds);

        return true;
    }

    // END: Basic CRUD operations
}
