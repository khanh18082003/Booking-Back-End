package com.booking.bookingbackend.service.properties;

import static com.booking.bookingbackend.constant.CommonConstant.SORT_BY;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.PropertiesRequest;
import com.booking.bookingbackend.data.dto.request.PropertiesSearchRequest;
import com.booking.bookingbackend.data.dto.response.Meta;
import com.booking.bookingbackend.data.dto.response.PaginationResponse;
import com.booking.bookingbackend.data.dto.response.PropertiesResponse;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.mapper.PropertiesMapper;
import com.booking.bookingbackend.data.projection.AccommodationDTO;
import com.booking.bookingbackend.data.projection.PropertiesDTO;
import com.booking.bookingbackend.data.repository.AmenitiesRepository;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.data.repository.PropertyTypeRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.util.GeometryUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Getter
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class PropertiesServiceImpl implements PropertiesService {

  PropertiesRepository repository;
  UserRepository userRepository;
  PropertyTypeRepository propertyTypeRepository;
  AmenitiesRepository amenitiesRepository;
  PropertiesMapper mapper;

  @Override
  @Transactional
  @PreAuthorize(value = "hasRole('HOST')")
  public PropertiesResponse save(PropertiesRequest request) {
    Properties properties = mapper.toEntity(request);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    // Lấy thông tin người dùng từ Authentication
    UUID userId = auth.getPrincipal() instanceof User user ? user.getId() : null;
    if (userId == null) {
      throw new AppException(ErrorCode.MESSAGE_UN_AUTHENTICATION);
    }
    User userEntity = userRepository.findById(userId)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName()));
    properties.setHost(userEntity);

    properties.setPropertyType(propertyTypeRepository.findById(request.typeId())
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName())));

    properties.setAmenities(amenitiesRepository.findAllById(request.amenitiesIds()));

    // Tạo và gán Point geom từ latitude & longitude
    if (request.latitude() != null && request.longitude() != null) {
      Point geom = GeometryUtil.toWebMercator(request.longitude(), request.latitude());
      properties.setGeom(geom);
    }

    Properties savedProperties = repository.save(properties);
    return mapper.toDtoResponse(savedProperties);
  }


  @Override
  public PaginationResponse<PropertiesDTO> searchProperties(
      PropertiesSearchRequest request,
      int pageNo,
      int pageSize,
      String[] filters,
      String... sorts
  ) {
    // sorts: [rating:asc, rating:desc]
    List<Sort.Order> orders = new ArrayList<>();
    if (sorts != null) {
      for (String sortBy : sorts) {
        log.info("SortBy: {}", sortBy);
        if (StringUtils.hasLength(sortBy)) {
          Pattern pattern = Pattern.compile(SORT_BY);
          Matcher matcher = pattern.matcher(sortBy);

          if (matcher.find()) {
            log.info(
                "Sorting field: {}, direction: {}",
                matcher.group(1),
                matcher.group(3)
            ); // Ghi log

            Direction direction = matcher.group(3).equalsIgnoreCase("asc")
                ? Direction.ASC
                : Direction.DESC;
            orders.add(new Order(direction, matcher.group(1)));
          } else {
            log.warn("Invalid sort parameter: {}", sortBy);
          }
        }
      }
    }

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(orders));

    var startDate = request.startDate();
    var endDate = request.endDate();

    int nights = (int) ChronoUnit.DAYS.between(startDate, endDate);
    endDate = endDate.minusDays(1);

    log.info("nights: {}", nights);

    double[] transformedCoordinates = GeometryUtil.transformLatLong(
        request.longitude(),
        request.latitude()
    );
    log.info("Transformed coordinates: longitude: {}, latitude: {}",
        transformedCoordinates[0],
        transformedCoordinates[1]
    );
    List<Tuple> raw = repository.searchProperties(
        transformedCoordinates[1],
        transformedCoordinates[0],
        request.radius(),
        startDate,
        endDate,
        nights,
        request.adults() + request.children(),
        request.adults(),
        request.children(),
        request.rooms()
    );

    ObjectMapper objectMapper = new ObjectMapper();
    List<PropertiesDTO> dtos = raw.stream().map(row -> {
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
            row.get("address", String.class),
            row.get("city", String.class),
            row.get("district", String.class),
            row.get("rating", BigDecimal.class),
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
    }).toList();

    // You may optionally sort or filter further in Java

    long total = raw.size(); // or call countQuery if accuracy needed
    Page<PropertiesDTO> page = new PageImpl<>(dtos, pageable, total);

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
      log.info("Selected: {}", selected);
      // Bước 2: Sinh mọi cách phân phối số phòng cho danh sách selected
      List<List<Integer>> allocations = allocateRooms(selected, requiredRooms);
      log.info("Allocations: {}", allocations);

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
              // Có thể sửa nếu cần scale theo quantity
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


  private List<List<Integer>> allocateRooms(List<AccommodationDTO> accs, int requiredRooms) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(accs, 0, requiredRooms, new ArrayList<>(), result);
    return result;
  }

  private void backtrack(
      List<AccommodationDTO> accs,
      int idx,
      int roomsLeft,
      List<Integer> current,
      List<List<Integer>> result
  ) {
    if (idx == accs.size()) {
      if (roomsLeft == 0) {
        result.add(new ArrayList<>(current));
      }
      return;
    }

    AccommodationDTO acc = accs.get(idx);
    int max = Math.min(acc.suggestedQuantity(), roomsLeft);

    for (int q = 0; q <= max; q++) {
      current.add(q);
      backtrack(accs, idx + 1, roomsLeft - q, current, result);
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

  @Transactional
  @Override
  public PropertiesResponse update(UUID id, PropertiesRequest request) {
    Properties properties = repository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName()));
    mapper.merge(request, properties);

    properties.setPropertyType(propertyTypeRepository.findById(request.typeId())
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName())));
    properties.setAmenities(amenitiesRepository.findAllById(request.amenitiesIds()));
    Properties updatedProperties = repository.save(properties);
    return mapper.toDtoResponse(updatedProperties);
  }
}
