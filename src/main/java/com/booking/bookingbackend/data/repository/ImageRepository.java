package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.base.BaseRepository;
import com.booking.bookingbackend.data.entity.Image;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends BaseRepository<Image, UUID> {

  List<Image> findAllByReferenceId(String referenceId);
}
