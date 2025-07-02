package com.booking.bookingbackend.data.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Role;

@Repository
public interface RoleRepository extends BaseRepository<Role, Integer> {

    @Query("SELECT r FROM Role r WHERE r.name IN :roles")
    Collection<Role> findAllByName(Collection<String> roles);

    Role findByName(String name);

    boolean existsByNameIsIgnoreCase(String name);
}
