package com.booking.bookingbackend.service.properties;

import static com.booking.bookingbackend.constant.CommonConstant.ACCOMMODATION_ID;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.constant.ImageReferenceType;
import com.booking.bookingbackend.data.dto.request.CheckAvailableAccommodationsBookingRequest;
import com.booking.bookingbackend.data.dto.request.PropertiesRequest;
import com.booking.bookingbackend.data.dto.request.PropertiesSearchRequest;
import com.booking.bookingbackend.data.dto.response.CheckedAvailableAccommodationBookingResponse;
import com.booking.bookingbackend.data.dto.response.Meta;
import com.booking.bookingbackend.data.dto.response.PaginationResponse;
import com.booking.bookingbackend.data.dto.response.PropertiesBookingResponse;
import com.booking.bookingbackend.data.dto.response.PropertiesResponse;
import com.booking.bookingbackend.data.dto.response.PropertyAvailableAccommodationBookingResponse;
import com.booking.bookingbackend.data.dto.response.ReviewResponse;
import com.booking.bookingbackend.data.entity.Amenities;
import com.booking.bookingbackend.data.entity.CustomUserDetails;
import com.booking.bookingbackend.data.entity.Image;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.mapper.AmenitiesMapper;
import com.booking.bookingbackend.data.mapper.PropertiesMapper;
import com.booking.bookingbackend.data.projection.AccommodationDTO;
import com.booking.bookingbackend.data.projection.AmenityDTO;
import com.booking.bookingbackend.data.projection.PropertiesDTO;
import com.booking.bookingbackend.data.projection.PropertiesDetailDTO;
import com.booking.bookingbackend.data.projection.PropertiesHostDTO;
import com.booking.bookingbackend.data.repository.AmenitiesRepository;
import com.booking.bookingbackend.data.repository.AvailableRepository;
import com.booking.bookingbackend.data.repository.ImageRepository;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.data.repository.PropertyTypeRepository;
import com.booking.bookingbackend.data.repository.ReviewRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.data.repository.criteria.PropertiesRepositoryCustom;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.service.booking.BookingValidationService;
import com.booking.bookingbackend.service.googlemap.GoogleMapService;
import com.booking.bookingbackend.util.GeometryUtils;
import com.booking.bookingbackend.util.SecurityUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Tuple;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Time;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class PropertiesServiceImpl implements PropertiesService {

  PropertiesRepository repository;
  PropertiesRepositoryCustom propertiesRepositoryCustom;
  UserRepository userRepository;
  PropertyTypeRepository propertyTypeRepository;
  AmenitiesRepository amenitiesRepository;
  ImageRepository imageRepository;
  AvailableRepository availableRepository;
  PropertiesMapper mapper;
  GoogleMapService googleMapService;
  ReviewRepository reviewRepository;
  BookingValidationService bookingValidationService;
  AmenitiesMapper amenitiesMapper;

  @Override
  @Transactional
  @PreAuthorize(value = "hasRole('HOST')")
  public PropertiesResponse save(PropertiesRequest request) {
    Properties properties = mapper.toEntity(request);

    CustomUserDetails hostDetails = SecurityUtils.getCurrentUser();
    if (hostDetails == null) {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }

    User host = hostDetails.user();
    properties.setHost(userRepository.findById(host.getId())
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            User.class.getSimpleName())));

    properties.setPropertyType(propertyTypeRepository.findById(request.typeId())
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName())));

    properties.setAmenities(amenitiesRepository.findAllById(request.amenitiesIds()));

    // Tạo và gán Point geom từ latitude & longitude
    if (request.latitude() != null && request.longitude() != null) {
      Point geom = GeometryUtils.toWebMercator(request.longitude(), request.latitude());
      properties.setGeom(geom);
    }

    PropertiesResponse response = mapper.toDtoResponse(repository.save(properties));
    // Save extra-images
    List<Image> imageList = request.extraImages().stream()
        .map(url -> Image.builder()
            .referenceId(properties.getId().toString())
            .referenceType(ImageReferenceType.PROPERTIES.name())
            .url(url)
            .build()).toList();
    imageRepository.saveAll(imageList);

    return response;
  }


  @Override
  public PaginationResponse<PropertiesDTO> searchProperties(
      PropertiesSearchRequest request,
      int pageNo,
      int pageSize,
      String[] filters, // e.g., "amenities:pool", "type:apartment"
      String... sort
  ) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

    String[] filteredSort = null;
    String totalPriceDirection = null;
    String ratingDirection = null;
    if (sort != null) {
      List<String> filteredSortList = new ArrayList<>();
      for (String s : sort) {
        if (s.startsWith("total_price")) {
          // Extract direction for later use
          totalPriceDirection = s.contains("desc") ? "desc" : "asc";
        } else if (s.startsWith("rating")) {
          ratingDirection = s.contains("desc") ? "desc" : "asc";
        } else {
          filteredSortList.add(s);
        }
      }
      filteredSort = filteredSortList.isEmpty() ? null : filteredSortList.toArray(new String[0]);
    }

    var startDate = request.startDate();
    var endDate = request.endDate();

    int nights = (int) ChronoUnit.DAYS.between(startDate, endDate);
    endDate = endDate.minusDays(1);

    double[] latLng = googleMapService.getLatLng(request.location());
    double[] transformedCoordinates = GeometryUtils.transformLatLong(latLng[1], latLng[0]);

    List<Tuple> raw = propertiesRepositoryCustom.searchPropertiesCustom(
        transformedCoordinates[1],
        transformedCoordinates[0],
        request.radius(),
        startDate,
        endDate,
        nights,
        request.adults() + request.children(),
        request.adults(),
        request.children(),
        request.rooms(),
        filters,
        filteredSort
    );

    ObjectMapper objectMapper = new ObjectMapper();
    List<PropertiesDTO> dtos = new ArrayList<>(raw.stream().map(row -> {
      try {
        List<AccommodationDTO> accommodations = objectMapper.readValue(
            row.get("accommodations", String.class),
            new TypeReference<>() {
            }
        );

        // TODO: Insert combination logic here to choose best accommodations set
        List<AccommodationDTO> best = selectBestCombination(
            accommodations,
            request.adults() + request.children(),
            request.rooms()
        );
        double totalPrice = best.stream().mapToDouble(AccommodationDTO::totalPrice).sum();
        return new PropertiesDTO(
            UUID.fromString(row.get("propertiesId", String.class)),
            row.get("propertiesName", String.class),
            row.get("image", String.class),
            row.get("latitude", Double.class),
            row.get("longitude", Double.class),
            row.get("address", String.class),
            row.get("city", String.class),
            row.get("district", String.class),
            row.get("rating", BigDecimal.class),
            row.get("totalRating", Integer.class),
            row.get("distance", Double.class),
            totalPrice,
            row.get("propertiesType", String.class),
            row.get("nights", Long.class),
            row.get("adults", Long.class),
            row.get("children", Long.class),
            best
        );
      } catch (Exception e) {
        throw new RuntimeException("Failed to map accommodations JSON", e);
      }
    }).toList());

    Comparator<PropertiesDTO> comparator = Comparator.comparing(p -> 0);
    if (ratingDirection != null) {
      comparator = Comparator
          .comparing(
              PropertiesDTO::rating,
              ratingDirection.equals("desc")
                  ? Comparator.reverseOrder()
                  : Comparator.naturalOrder()
          );
    }
    if (totalPriceDirection != null) {
      Comparator<PropertiesDTO> priceComparator = Comparator
          .comparing(
              PropertiesDTO::totalPrice,
              totalPriceDirection.equals("desc")
                  ? Comparator.reverseOrder()
                  : Comparator.naturalOrder()
          );
      comparator = comparator.thenComparing(priceComparator);
    }
    dtos.sort(comparator);

    Page<PropertiesDTO> page = new PageImpl<>(dtos, pageable, raw.size());

    return PaginationResponse.<PropertiesDTO>builder()
        .meta(Meta.builder()
            .page(page.getNumber() + 1)
            .pageSize(page.getSize())
            .pages(page.getTotalPages())
            .total(page.getTotalElements())
            .build())
        .data(page.getContent())
        .build();
  }

  private List<AccommodationDTO> selectBestCombination(
      List<AccommodationDTO> all,
      int requiredGuests,
      int requiredRooms
  ) {
    List<AccommodationDTO> bestCombination = new ArrayList<>();
    double minTotalPrice = Double.MAX_VALUE;

    int n = all.size();
    for (int mask = 1; mask < (1 << n); mask++) {
      // Bước 1: Chọn danh sách acc trong tổ hợp hiện tại
      List<AccommodationDTO> selected = new ArrayList<>();
      for (int i = 0; i < n; i++) {
        if ((mask & (1 << i)) != 0) {
          selected.add(all.get(i)); // mask: 1 -> selected [Phong Deluxe]
        }
      }
      // Bước 2: Sinh mọi cách phân phối số phòng cho danh sách selected
      List<List<Integer>> allocations = allocateRooms(selected, requiredRooms, requiredGuests);

      for (List<Integer> allocation : allocations) {
        int totalCapacity = 0;
        double totalPrice = 0;
        int totalBeds = 0;

        List<AccommodationDTO> current = new ArrayList<>();

        for (int i = 0; i < selected.size(); i++) {
          AccommodationDTO acc = selected.get(i);
          int quantity = allocation.get(i);
          int totalCapacity1 = acc.totalCapacity() / acc.suggestedQuantity() * quantity;
          totalCapacity += totalCapacity1;
          double totalPrice1 = acc.totalPrice() / acc.suggestedQuantity() * quantity;
          totalPrice += totalPrice1;
          int totalBeds1 = acc.totalBeds() / acc.suggestedQuantity() * quantity;
          totalBeds += totalBeds1;

          current.add(new AccommodationDTO(
              acc.accommodationId(),
              acc.accommodationName(),
              quantity,
              totalCapacity1,
              totalBeds1,
              totalPrice1,
              acc.bedNames()
          ));
        }

        if (totalCapacity >= requiredGuests && totalPrice < minTotalPrice) {
          minTotalPrice = totalPrice;
          bestCombination = current;
        }
      }
    }

    return bestCombination;
  }


  private List<List<Integer>> allocateRooms(
      List<AccommodationDTO> accs,
      int requiredRooms,
      int requiredCapacity) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(accs, 0, requiredRooms, requiredCapacity, new ArrayList<>(), result);
    return result;
  }

  private void backtrack(
      List<AccommodationDTO> accs,
      int idx,
      int roomsLeft,
      int requiredCapacity,
      List<Integer> current,
      List<List<Integer>> result
  ) {
    if (idx == accs.size()) {
      if (roomsLeft == 0 || accs.get(idx - 1).totalCapacity() >= requiredCapacity) {
        result.add(new ArrayList<>(current));
      }
      return;
    }

    AccommodationDTO acc = accs.get(idx);
    int max = Math.min(acc.suggestedQuantity(), roomsLeft);

    for (int q = 0; q <= max; q++) {
      current.add(q);
      backtrack(accs, idx + 1, roomsLeft - q, requiredCapacity, current, result);
      current.remove(current.size() - 1);
    }
  }


  @Override
  @Transactional
  public void changeStatus(UUID id) {
    Properties properties = repository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName()));
    properties.setStatus(false);
    repository.save(properties);
  }

  @PreAuthorize(value = "hasRole('HOST')")
  @Transactional
  @Override
  public PropertiesHostDTO update(UUID id, PropertiesRequest request) {
    CustomUserDetails userDetails = SecurityUtils.getCurrentUser();
    if (userDetails == null) {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }
    Properties properties = repository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName())
        );
    mapper.merge(request, properties);

    if (!properties.getHost().getId().equals(userDetails.user().getId())) {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }

    if (request.typeId() != null) {
      properties.setPropertyType(propertyTypeRepository.findById(request.typeId())
          .orElseThrow(
              () -> new AppException(
                  ErrorCode.MESSAGE_INVALID_ENTITY_ID,
                  getEntityClass().getSimpleName()
              )
          )
      );
    }

    if (request.amenitiesIds() != null) {
      properties.setAmenities(amenitiesRepository.findAllById(request.amenitiesIds()));
    }

    // Update geom if latitude and longitude are provided
    if (request.latitude() != null && request.longitude() != null) {
      Point geom = GeometryUtils.toWebMercator(request.longitude(), request.latitude());
      properties.setGeom(geom);
    }

    properties = repository.save(properties);

    // Handle image updates
    List<Image> existingImages = imageRepository.findAllByReferenceId(id.toString());
    List<String> existingUrls = new ArrayList<>(
        existingImages.stream().map(Image::getUrl).toList());

    // 1. Delete images that are no longer in the request
    existingImages.forEach(image -> {
      if (!request.extraImages().contains(image.getUrl())) {
        imageRepository.delete(image);
        existingUrls.remove(image.getUrl());
      }
    });

    // 2. Add new images that don't exist yet
    List<Image> newImages = request.extraImages().stream()
        .filter(url -> !existingUrls.contains(url))
        .map(url -> {
          existingUrls.add(url);
          return Image.builder()
              .referenceId(id.toString())
              .referenceType(ImageReferenceType.PROPERTIES.name())
              .url(url)
              .build();
        }).toList();

    if (!newImages.isEmpty()) {
      imageRepository.saveAll(newImages);
    }

    return PropertiesHostDTO.builder()
        .id(properties.getId())
        .name(properties.getName())
        .description(properties.getDescription())
        .address(properties.getAddress())
        .ward(properties.getWard())
        .image(properties.getImage())
        .city(properties.getCity())
        .district(properties.getDistrict())
        .province(properties.getProvince())
        .country(properties.getCountry())
        .rating(properties.getRating())
        .totalRating(properties.getTotalRating())
        .status(properties.isStatus())
        .latitude(properties.getLatitude())
        .longitude(properties.getLongitude())
        .checkInTime(properties.getCheckInTime())
        .checkOutTime(properties.getCheckOutTime())
        .createdAt(properties.getCreatedAt())
        .updatedAt(properties.getUpdatedAt())
        .propertyType(properties.getPropertyType().getName())
        .imageUrls(existingUrls)
        .amenitiesIds(
            properties.getAmenities().stream().map(Amenities::getId).toList()
        )
        .build();
  }

  @Override
  public PropertiesDetailDTO getPropertiesDetail(UUID id) {
    Tuple raw = repository.findPropertiesDetail(id);
    if (raw == null) {
      throw new AppException(
          ErrorCode.MESSAGE_INVALID_ENTITY_ID,
          getEntityClass().getSimpleName()
      );
    }

    ObjectMapper objectMapper = new ObjectMapper();
    try {
      List<String> imageUrls = objectMapper.readValue(
          raw.get("image_urls", String.class),
          new TypeReference<>() {
          }
      );
      List<AmenityDTO> amenities = objectMapper.readValue(
          raw.get("amenities", String.class),
          new TypeReference<>() {
          }
      );
      return new PropertiesDetailDTO(
          UUID.fromString(raw.get("id", String.class)),
          raw.get("name", String.class),
          raw.get("description", String.class),
          raw.get("image", String.class),
          raw.get("address", String.class),
          raw.get("rating", BigDecimal.class),
          raw.get("totalRating", Integer.class),
          raw.get("status", Boolean.class),
          raw.get("latitude", Double.class),
          raw.get("longitude", Double.class),
          raw.get("checkInTime", Time.class).toLocalTime(),
          raw.get("checkOutTime", Time.class).toLocalTime(),
          raw.get("propertyType", String.class),
          amenities,
          imageUrls
      );
    } catch (Exception e) {
      throw new RuntimeException("Failed to map accommodations JSON", e);
    }
  }

  @Override
  public PaginationResponse<ReviewResponse> getPropertiesReviews(
      UUID id,
      int pageNo,
      int pageSize
  ) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    Page<ReviewResponse> page = reviewRepository.findAllByPropertiesId(id, pageable);

    return PaginationResponse.<ReviewResponse>builder()
        .meta(Meta.builder()
            .page(page.getNumber() + 1)
            .pageSize(page.getSize())
            .pages(page.getTotalPages())
            .total(page.getTotalElements())
            .build())
        .data(page.getContent())
        .build();
  }

  @Override
  public PropertyAvailableAccommodationBookingResponse checkAvailableAccommodationsBooking(
      UUID id,
      CheckAvailableAccommodationsBookingRequest request,
      HttpServletResponse httpServletResponse
  ) {
    bookingValidationService.validateRequest(
        request.checkIn(),
        request.checkOut(),
        request.adults(),
        request.children()
    );
    Map<UUID, Integer> accommodationQuantities = getAccommodationQuantities(
        request.accommodations()
    );
    log.info("Accommodation quantities: {}", accommodationQuantities);
    List<CheckedAvailableAccommodationBookingResponse> availableAccommodationBooking = accommodationQuantities.entrySet()
        .stream()
        .map(v -> {
              return availableRepository.checkAvailableAccommodation(
                  v.getKey(),
                  request.checkIn(),
                  request.checkOut().minusDays(1),
                  v.getValue(),
                  (int) ChronoUnit.DAYS.between(
                      request.checkIn(),
                      request.checkOut()
                  )
              ).orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NO_AVAILABLE_ACCOMMODATION));
            }
        ).toList();

    Properties properties = repository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName()));
    PropertiesBookingResponse propertiesBookingResponse = PropertiesBookingResponse.builder()
        .id(properties.getId())
        .name(properties.getName())
        .description(properties.getDescription())
        .address(properties.getAddress())
        .ward(properties.getWard())
        .district(properties.getDistrict())
        .city(properties.getCity())
        .province(properties.getProvince())
        .country(properties.getCountry())
        .rating(properties.getRating())
        .totalRating(properties.getTotalRating())
        .checkInTime(properties.getCheckInTime())
        .checkOutTime(properties.getCheckOutTime())
        .propertiesType(properties.getPropertyType().getName())
        .amenities(
            properties.getAmenities()
                .stream()
                .map(amenitiesMapper::toDtoResponse)
                .collect(Collectors.toSet())
        )
        .build();

    return PropertyAvailableAccommodationBookingResponse.builder()
        .properties(propertiesBookingResponse)
        .accommodations(availableAccommodationBooking)
        .checkIn(request.checkIn())
        .checkOut(request.checkOut())
        .adults(request.adults())
        .children(request.children())
        .rooms(request.rooms())
        .totalPrice(availableAccommodationBooking.stream()
            .map(CheckedAvailableAccommodationBookingResponse::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add))
        .build();
  }

  private Map<UUID, Integer> getAccommodationQuantities(
      String... accommodations
  ) {
    Map<UUID, Integer> accommodationQuantities = new HashMap<>();
    if (accommodations != null) {
      for (String s : accommodations) {
        Pattern pattern = Pattern.compile(ACCOMMODATION_ID);
        Matcher matcher = pattern.matcher(s);

        if (matcher.find()) {
          accommodationQuantities.put(
              UUID.fromString(matcher.group(1)),
              Integer.parseInt(matcher.group(3))
          );
        } else {
          log.warn("Invalid search parameter: {}", s);
        }
      }
    }
    return accommodationQuantities;
  }

  @Override
  @PreAuthorize(value = "hasRole('HOST')")
  public List<PropertiesHostDTO> getMyProperties() {
    CustomUserDetails userDetails = SecurityUtils.getCurrentUser();
    if (userDetails == null) {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }
    List<Properties> propertiesList = repository.findAllByHostId(userDetails.user().getId());

    return propertiesList.stream()
        .map(properties -> PropertiesHostDTO.builder()
            .id(properties.getId())
            .name(properties.getName())
            .description(properties.getDescription())
            .address(properties.getAddress())
            .ward(properties.getWard())
            .image(properties.getImage())
            .city(properties.getCity())
            .district(properties.getDistrict())
            .province(properties.getProvince())
            .country(properties.getCountry())
            .rating(properties.getRating())
            .totalRating(properties.getTotalRating())
            .status(properties.isStatus())
            .latitude(properties.getLatitude())
            .longitude(properties.getLongitude())
            .checkInTime(properties.getCheckInTime())
            .checkOutTime(properties.getCheckOutTime())
            .createdAt(properties.getCreatedAt())
            .updatedAt(properties.getUpdatedAt())
            .propertyType(properties.getPropertyType().getName())
            .imageUrls(
                imageRepository.findAllByReferenceId(properties.getId().toString()).stream().map(
                    Image::getUrl
                ).toList()
            )
            .amenitiesIds(
                properties.getAmenities().stream().map(Amenities::getId).toList()
            )
            .build())
        .toList();
  }

}