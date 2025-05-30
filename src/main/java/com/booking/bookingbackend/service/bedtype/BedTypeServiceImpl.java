package com.booking.bookingbackend.service.bedtype;

import com.booking.bookingbackend.data.dto.response.BedTypeResponse;
import com.booking.bookingbackend.data.mapper.BedTypeMapper;
import com.booking.bookingbackend.data.repository.BedTypeRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BedTypeServiceImpl implements BedTypeService{
    BedTypeRepository repository;
    BedTypeMapper mapper;

    @Override
    public List<BedTypeResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDtoResponse)
                .toList();
    }
}
