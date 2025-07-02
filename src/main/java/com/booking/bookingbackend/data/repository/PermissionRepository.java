package com.booking.bookingbackend.data.repository;

import org.springframework.stereotype.Repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Permission;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Integer> {}
