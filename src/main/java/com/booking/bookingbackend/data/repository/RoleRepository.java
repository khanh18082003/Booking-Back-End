package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Role;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseRepository<Role, Integer> {
  @Query("SELECT r FROM Role r WHERE r.name IN :roles")
  Collection<Role> findAllByName(Collection<String> roles);
  Role findByName(String name);
  boolean existsByNameIsIgnoreCase(String name);
}
