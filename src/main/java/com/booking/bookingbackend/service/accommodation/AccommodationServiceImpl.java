package com.booking.bookingbackend.service.accommodation;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.AccommodationCreationRequest;
import com.booking.bookingbackend.data.dto.request.AccommodationUpdateRequest;
import com.booking.bookingbackend.data.dto.response.AccommodationResponse;
import com.booking.bookingbackend.data.dto.response.AmenitiesResponse;
import com.booking.bookingbackend.data.dto.response.BedTypeResponse;
import com.booking.bookingbackend.data.dto.response.RoomResponse;
import com.booking.bookingbackend.data.entity.*;
import com.booking.bookingbackend.data.mapper.AccommodationMapper;
import com.booking.bookingbackend.data.repository.*;
import com.booking.bookingbackend.exception.AppException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AccommodationServiceImpl implements AccommodationService {

    AccommodationRepository repository;
    AccommodationMapper mapper;
    PropertiesRepository propertiesRepository;
    AmenitiesRepository amenitiesRepository;
    RoomTypeRepository roomTypeRepository;
    BedTypeRepository bedTypeRepository;
    RoomHasBedRepository roomHasBedRepository;

    @Transactional
    @Override
    public AccommodationResponse save(AccommodationCreationRequest request) {
        Accommodation entity = mapper.toEntity(request);

        // Lấy properties
        Properties properties = propertiesRepository.findById(request.propertiesId())
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
                        getEntityClass().getSimpleName()));
        entity.setProperties(properties);

        // Lấy amenities
        if (!request.amenitiesIds().isEmpty()) {
            Set<Amenities> amenities = amenitiesRepository.findAllById(request.amenitiesIds());
            entity.setAmenities(amenities);
        }
        log.info(entity.getAmenities().stream()
                .map(Amenities::getName)
                .toList()
                .toString());
        // Xử lý danh sách phòng (rooms)
        var rooms = request.rooms();
        if (rooms != null) {
            Set<AccommodationHasRoom> accommodationHasRooms = rooms.stream().map(room -> {
                // Lấy roomType
                var roomType = roomTypeRepository.findById(room.roomTypeId())
                        .orElseThrow(() -> new AppException(
                                ErrorCode.MESSAGE_INVALID_ENTITY_ID,
                                getEntityClass().getSimpleName()
                        ));

                // Tạo AccommodationHasRoom (chưa set bedList)
                AccommodationHasRoom accommodationHasRoom = AccommodationHasRoom.builder()
                        .accommodation(entity)
                        .roomType(roomType)
                        .roomName(room.roomName())
                        .build();

                // Tạo danh sách RoomHasBed và gán accommodationRoom cho từng phần tử
                Set<RoomHasBed> roomHasBeds = room.bedTypes().stream().map(bt -> {
                    var bedType = bedTypeRepository.findById(bt.id())
                            .orElseThrow(() -> new AppException(
                                    ErrorCode.MESSAGE_INVALID_ENTITY_ID,
                                    getEntityClass().getSimpleName()
                            ));
                    return RoomHasBed.builder()
                            .bedType(bedType)
                            .quantity(bt.quantity())
                            .accommodationRoom(accommodationHasRoom) // 🔥 gán liên kết ngược
                            .build();
                }).collect(Collectors.toSet());

                // Gán lại roomHasBeds cho accommodationHasRoom
                accommodationHasRoom.setRoomHasBedList(roomHasBeds);

                return accommodationHasRoom;
            }).collect(Collectors.toSet());

            entity.setAccommodationHasRooms(accommodationHasRooms);
        }

        // Lưu thực thể
        Accommodation savedEntity = repository.save(entity);

        // Map sang response
        AccommodationResponse response = mapper.toDtoResponse(savedEntity);
        response.setPropertiesName(properties.getName());

        // Set amenities cho response
        response.setAmenities(
                savedEntity.getAmenities().stream()
                        .map(a -> AmenitiesResponse.builder()
                                .id(a.getId())
                                .name(a.getName())
                                .build())
                        .collect(Collectors.toSet())
        );

        // Set rooms cho response
        response.setRooms(
                savedEntity.getAccommodationHasRooms().stream()
                        .map(room -> RoomResponse.builder()
                                .id(room.getId())
                                .roomName(room.getRoomName())
                                .roomType(room.getRoomType().getName())
                                .bedTypeList(room.getRoomHasBedList().stream()
                                        .map(bedType -> BedTypeResponse.builder()
                                                .name(bedType.getBedType().getName())
                                                .quantity(bedType.getQuantity())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toSet())
        );

        return response;
    }

    @Transactional
    @Override
    public AccommodationResponse update(UUID id, AccommodationUpdateRequest request) {
        Accommodation accommodation = repository.findById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.MESSAGE_INVALID_ENTITY_ID,
                        getEntityClass().getSimpleName()
                ));

        // Cập nhật thông tin cơ bản
        mapper.merge(request, accommodation);

        // Cập nhật amenities nếu có
        if (request.amenitiesIds() != null) {
            Set<Amenities> amenities = amenitiesRepository.findAllById(request.amenitiesIds());
            accommodation.setAmenities(amenities);
        }

        // Cập nhật room và bed nếu có
        if (request.rooms() != null) {
            for (var roomReq : request.rooms()) {
                AccommodationHasRoom existingRoom = accommodation.getAccommodationHasRooms().stream()
                        .filter(r -> r.getRoomName().equals(roomReq.roomName()))
                        .findFirst()
                        .orElse(null);

                if (existingRoom != null) {
                    // Cập nhật roomType nếu có
                    if (roomReq.roomTypeId() != null) {
                        var roomType = roomTypeRepository.findById(roomReq.roomTypeId())
                                .orElseThrow(() -> new AppException(
                                        ErrorCode.MESSAGE_INVALID_ENTITY_ID,
                                        getEntityClass().getSimpleName()
                                ));
                        existingRoom.setRoomType(roomType);
                    }

                    if (roomReq.bedTypes() != null) {
                        Map<Integer, RoomHasBed> currentBeds = existingRoom.getRoomHasBedList().stream()
                                .collect(Collectors.toMap(b -> b.getBedType().getId(), b -> b));

                        for (var bt : roomReq.bedTypes()) {
                            var bedType = bedTypeRepository.findById(bt.id())
                                    .orElseThrow(() -> new AppException(
                                            ErrorCode.MESSAGE_INVALID_ENTITY_ID,
                                            getEntityClass().getSimpleName()
                                    ));

                            if (currentBeds.containsKey(bt.id())) {
                                currentBeds.get(bt.id()).setQuantity(bt.quantity()); // cập nhật số lượng
                            } else {
                                RoomHasBed newBed = RoomHasBed.builder()
                                        .bedType(bedType)
                                        .quantity(bt.quantity())
                                        .accommodationRoom(existingRoom)
                                        .build();
                                existingRoom.getRoomHasBedList().add(newBed);
                            }
                        }
                    }
                }
            }
        }

        // Lưu và trả về DTO
        Accommodation updatedAccommodation = repository.save(accommodation);
        AccommodationResponse response = mapper.toDtoResponse(updatedAccommodation);
        response.setRooms(
                updatedAccommodation.getAccommodationHasRooms().stream()
                        .map(room -> RoomResponse.builder()
                                .id(room.getId())
                                .roomName(room.getRoomName())
                                .roomType(room.getRoomType().getName())
                                .bedTypeList(room.getRoomHasBedList().stream()
                                        .map(bedType -> BedTypeResponse.builder()
                                                .name(bedType.getBedType().getName())
                                                .quantity(bedType.getQuantity())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toSet())
        );
        response.setAmenities(
                updatedAccommodation.getAmenities().stream()
                        .map(a -> AmenitiesResponse.builder()
                                .id(a.getId())
                                .name(a.getName())
                                .build())
                        .collect(Collectors.toSet())
        );
        return response;
    }

}
