package com.booking.bookingbackend.service.accommodation;

import static java.util.stream.Collectors.toList;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.constant.ImageReferenceType;
import com.booking.bookingbackend.data.dto.request.AccommodationCreationRequest;
import com.booking.bookingbackend.data.dto.request.AccommodationUpdateRequest;
import com.booking.bookingbackend.data.dto.request.AccommodationsSearchRequest;
import com.booking.bookingbackend.data.dto.response.AccommodationBookingResponse;
import com.booking.bookingbackend.data.dto.response.AccommodationResponse;
import com.booking.bookingbackend.data.dto.response.AmenitiesResponse;
import com.booking.bookingbackend.data.dto.response.BedTypeResponse;
import com.booking.bookingbackend.data.dto.response.RoomResponse;
import com.booking.bookingbackend.data.entity.Accommodation;
import com.booking.bookingbackend.data.entity.AccommodationHasRoom;
import com.booking.bookingbackend.data.entity.Amenities;
import com.booking.bookingbackend.data.entity.Available;
import com.booking.bookingbackend.data.entity.Image;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.entity.RoomHasBed;
import com.booking.bookingbackend.data.mapper.AccommodationMapper;
import com.booking.bookingbackend.data.projection.*;
import com.booking.bookingbackend.data.repository.AccommodationRepository;
import com.booking.bookingbackend.data.repository.AmenitiesRepository;
import com.booking.bookingbackend.data.repository.AvailableRepository;
import com.booking.bookingbackend.data.repository.BedTypeRepository;
import com.booking.bookingbackend.data.repository.ImageRepository;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.data.repository.RoomHasBedRepository;
import com.booking.bookingbackend.data.repository.RoomTypeRepository;
import com.booking.bookingbackend.exception.AppException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    AvailableRepository availableRepository;
    RoomHasBedRepository roomHasBedRepository;
    ImageRepository imageRepository;

    private static final int NIGHTS_MAX = 365;

    @Transactional
    @Override
    public AccommodationResponse save(final AccommodationCreationRequest request) {
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
        // Lưu danh sách ảnh
        List<Image> imageList = request.extraImages().stream()
                .map(url -> Image.builder()
                        .referenceId(savedEntity.getId().toString())
                        .referenceType(ImageReferenceType.ACCOMMODATION.name())
                        .url(url)
                        .build()).toList();
        imageRepository.saveAll(imageList);

        response.setImages(
                imageList.stream()
                        .map(Image::getUrl)
                        .collect(toList())
        );

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
                                        .collect(toList()))
                                .build())
                        .collect(Collectors.toSet())
        );

        return response;
    }

    @Override
    public void saveAll(List<AccommodationBookingResponse> aDays) {
        final List<Available> availabilities = new ArrayList<>();
        for (var accommodation : aDays) {
            for (var availableAccommodation : accommodation.getAvailableAccommodations()) {
                var availableEntity = availableRepository.findById(availableAccommodation.id())
                        .orElseThrow(() -> new AppException(
                                ErrorCode.MESSAGE_INVALID_ENTITY_ID,
                                Available.class.getSimpleName())
                        );
                availableEntity.setTotalReserved(
                        availableEntity.getTotalReserved() + accommodation.getQuantity()
                );
                availabilities.add(availableEntity);
            }
        }

        availableRepository.saveAll(availabilities);
    }

    @Transactional
    @Override
    public List<AvailableAccommodationDTO> checkAvailabilityForBooking(
            final UUID id,
            final LocalDate checkInDate,
            final LocalDate checkOutDate,
            Integer quantity
    ) {
        final int nights = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        log.info("nights: {}", nights);
        if (nights > NIGHTS_MAX) {
            throw new AppException(ErrorCode.MESSAGE_INVALID_NIGHTS);
        }
        final var aDays = availableRepository.findAndLockAvailableAccommodation(
                id,
                checkInDate,
                checkOutDate.minusDays(1),
                quantity
        );
        log.info("aDays: {}", aDays);
        if (aDays.isEmpty() || aDays.size() < nights) {
            throw new AppException(ErrorCode.MESSAGE_NO_AVAILABLE_ACCOMMODATION);
        }
        return aDays;
    }

    @Transactional
    @Override
    public AccommodationResponse update(final UUID id, final AccommodationUpdateRequest request) {
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

        // Handle image updates
        List<Image> existingImages = imageRepository.findAllByReferenceId(id.toString());
        List<String> existingUrls = existingImages.stream().map(Image::getUrl).toList();

        // 1. Delete images that are no longer in the request
        existingImages.forEach(image -> {
            if (!request.extraImages().contains(image.getUrl())) {
                imageRepository.delete(image);
            }
        });

        // 2. Add new images that don't exist yet
        List<Image> newImages = request.extraImages().stream()
                .filter(url -> !existingUrls.contains(url))
                .map(url -> Image.builder()
                        .referenceId(id.toString())
                        .referenceType(ImageReferenceType.ACCOMMODATION.name())
                        .url(url)
                        .build())
                .toList();

        if (!newImages.isEmpty()) {
            imageRepository.saveAll(newImages);
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
                                        .collect(toList()))
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

        response.setImages(
                imageRepository.findAllByReferenceId(id.toString()).stream()
                        .map(Image::getUrl)
                        .collect(toList())
        );
        return response;
    }

    @Override
    public List<AccommodationSearchDTO> findAccommodationByPropertyId(
            final AccommodationsSearchRequest request
    ) {
        var id = request.id();
        var rooms = request.rooms();
        var startDate = request.startDate();
        var endDate = request.endDate();
        var nights = (int) ChronoUnit.DAYS.between(startDate, endDate);
        endDate = endDate.minusDays(1);

        List<Tuple> tuples = repository.searchAllByPropertyId(
                id.toString(), startDate, endDate, rooms, nights, request.children() + request.adults()
        );
        log.info("Tuples: {}", tuples);
        ObjectMapper objectMapper = new ObjectMapper();

        List<AccommodationSearchDTO> accommodationSearchDTOList = tuples.stream().map(row -> {
            try {
                // Parse JSON chuỗi sang list đối tượng
                String roomsJson = row.get("rooms", String.class);
                String amenitiesJson = row.get("amenities", String.class);
                List<RoomDTO> roomList = objectMapper.readValue(roomsJson, new TypeReference<>() {
                });
                List<AmenityDTO> amenityList = objectMapper.readValue(amenitiesJson,
                        new TypeReference<>() {
                        });
                BigDecimal totalPriceDecimal = row.get("total_price", BigDecimal.class);
                double totalPrice = totalPriceDecimal != null ? totalPriceDecimal.doubleValue() : 0.0;
                List<String> imageUrls = objectMapper.readValue(
                        row.get("image_urls", String.class), new TypeReference<>() {
                        });
                // Trả về DTO
                return new AccommodationSearchDTO(
                        UUID.fromString(row.get("accommodation_id", String.class)), //
                        row.get("name", String.class),
                        row.get("capacity", Integer.class),
                        imageUrls,
                        row.get("size", Float.class),
                        row.get("available_rooms", Long.class),
                        totalPrice,
                        row.get("description", String.class),
                        roomList,
                        amenityList
                );
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse tuple data", e);
            }
        }).toList();

        log.info("AccommodationSearchDTOList: {}", accommodationSearchDTOList);
        return accommodationSearchDTOList;
    }

    @Override
    public List<AccommodationHostDTO> getAccommodationsByPropertyId(UUID id) {
        List<Accommodation> accommodations = repository.findAllByPropertiesId(id);
        return accommodations.stream()
                .map(accommodation -> {
                    List<String> images = imageRepository.findAllByReferenceId(accommodation.getId().toString())
                            .stream()
                            .map(Image::getUrl)
                            .collect(toList());
                    List<RoomDTO> rooms= accommodation.getAccommodationHasRooms().stream()
                            .map(room -> new RoomDTO(
                                    room.getRoomName(),
                                    room.getRoomHasBedList().stream()
                                            .map(bed -> new BedDTO(
                                                    bed.getBedType().getName(),
                                                    bed.getQuantity()
                                            )).toList()
                            )).toList();
                    return new AccommodationHostDTO(
                            accommodation.getId(),
                            accommodation.getName(),
                            accommodation.getBasePrice(),
                            accommodation.getCapacity(),
                            accommodation.getTotalRooms(),
                            accommodation.getTotalUnits(),
                            accommodation.getDescription(),
                            accommodation.getSize(),
                            accommodation.getUnit(),
                            images,
                            rooms
                    );
                }).toList();
    }
}
