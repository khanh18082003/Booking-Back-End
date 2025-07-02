package com.booking.bookingbackend.data.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Image;

@Repository
public interface ImageRepository extends BaseRepository<Image, UUID> {

    List<Image> findAllByReferenceId(String referenceId);
}
