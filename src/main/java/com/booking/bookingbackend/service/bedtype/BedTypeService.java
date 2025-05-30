package com.booking.bookingbackend.service.bedtype;

import com.booking.bookingbackend.data.dto.response.BedTypeResponse;
import com.booking.bookingbackend.data.entity.Amenities;
import com.booking.bookingbackend.data.entity.BedType;
import com.booking.bookingbackend.data.repository.BedTypeRepository;
import com.booking.bookingbackend.service.BaseEntityService;

import java.util.List;

public interface BedTypeService extends BaseEntityService<Integer, BedType, BedTypeRepository, BedTypeResponse> {
    @Override
    default Class<?> getEntityClass() {
        return Amenities.class;
    }
    List<BedTypeResponse> getAll();
}
