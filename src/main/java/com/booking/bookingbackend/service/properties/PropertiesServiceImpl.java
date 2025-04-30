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
import com.booking.bookingbackend.data.projection.PropertiesDTO;
import com.booking.bookingbackend.data.repository.AmenitiesRepository;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.data.repository.PropertyTypeRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.util.GeometryUtil;
import jakarta.transaction.Transactional;
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
    Page<PropertiesDTO> propertiesPage = repository.searchProperties(
        transformedCoordinates[1],
        transformedCoordinates[0],
        request.radius(),
        startDate,
        endDate,
        nights,
        request.adults() + request.children(),
        pageable
    );

    return PaginationResponse.<PropertiesDTO>builder()
        .meta(Meta.builder()
            .page(propertiesPage.getNumber() + 1)
            .pageSize(propertiesPage.getSize())
            .pages(propertiesPage.getTotalPages())
            .total(propertiesPage.getTotalElements())
            .build())
        .data(propertiesPage.getContent())
        .build();
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
