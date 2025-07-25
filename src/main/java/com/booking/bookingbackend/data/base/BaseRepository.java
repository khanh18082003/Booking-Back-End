package com.booking.bookingbackend.data.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E extends AbstractIdentifiable<I>, I>
        extends JpaRepository<E, I>, JpaSpecificationExecutor<E> {}
