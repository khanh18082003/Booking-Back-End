package com.booking.bookingbackend.controller;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.PropertiesRequest;
import com.booking.bookingbackend.data.dto.request.PropertiesSearchRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.PaginationResponse;
import com.booking.bookingbackend.data.dto.response.PropertiesResponse;
import com.booking.bookingbackend.data.projection.PropertiesDTO;
import com.booking.bookingbackend.data.projection.PropertiesDetailDTO;
import com.booking.bookingbackend.service.googlemap.GoogleMapService;
import com.booking.bookingbackend.service.properties.PropertiesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_PROPERTY)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "PROPERTIES-CONTROLLER")
public class PropertiesController {

  PropertiesService propertiesService;
  GoogleMapService googleMapService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Create Property",
      description = "Create a new Property (`ADMIN` only)",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "201",
              description = "Property created",
              content = @Content(
                  examples = @ExampleObject(
                      value = """
                          {
                            "code": "M000",
                            "status": "201",
                            "message": "Success",
                            "data": {
                              "name": "Luxury Villa",
                              "description": "A beautiful villa with sea view",
                              "address": "123 Ocean Drive",
                              "city": "Miami",
                              "country": "USA",
                              "district": "South Beach",
                              "rating": 4.8,
                              "status": true,
                              "latitude": 25.7617,
                              "longitude": -80.1918,
                              "check_in_time": "14:00",
                              "check_out_time": "11:00",
                              "created_at": "2025-03-15T05:35:35.467Z",
                              "updated_at": "2025-03-15T05:35:35.467Z",
                              "host": {
                                "id": "1",
                                "name": "John Doe"
                              },
                              "property_type": {
                                "id": "2",
                                "name": "Villa"
                              },
                              "amenities": [
                                {
                                  "id": "1",
                                  "name": "WiFi"
                                },
                                {
                                  "id": "2",
                                  "name": "Pool"
                                }
                              ]
                            }
                          }
                          """
                  )
              )
          )
      }
  )
  ApiResponse<PropertiesResponse> save(
      @Valid @RequestBody PropertiesRequest request
  ) {
    StringBuilder address = new StringBuilder();
    address.append(request.name()).append(", ");
    if (StringUtils.hasLength(request.ward())) {
      address.append(request.ward()).append(", ");
    }
    if (StringUtils.hasLength(request.district())) {
      address.append(request.district()).append(", ");
    }
    if (StringUtils.hasLength(request.city())) {
      address.append(request.city()).append(", ");
    }
    if (StringUtils.hasLength(request.province())) {
      address.append(request.province()).append(", ");
    }
    if (StringUtils.hasLength(request.country())) {
      address.append(request.country());
    }
    log.info("Address: {}", address);
    var location = googleMapService.getLatLng(address.toString());
    PropertiesRequest latLngRequest = request.withLatitude(location[0]).withLongitude(location[1]);

    return ApiResponse.<PropertiesResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(propertiesService.save(latLngRequest))
        .build();
  }

  @GetMapping("/search")
  ApiResponse<PaginationResponse<PropertiesDTO>> search(
      @RequestParam(name = "location") String location,
      @RequestParam(name = "radius", defaultValue = "10000") Double radius,
      @RequestParam(name = "start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam(name = "end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam(name = "adults") Integer adults,
      @RequestParam(name = "children") Integer children,
      @RequestParam(name = "rooms") Integer rooms,
      @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
      @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
      @RequestParam(name = "filter", required = false) String[] filter,
      @RequestParam(name = "sort", required = false) String... sort) {
    var request = PropertiesSearchRequest.builder()
        .location(location)
        .radius(radius)
        .startDate(startDate)
        .endDate(endDate)
        .adults(adults)
        .children(children)
        .rooms(rooms)
        .build();

    return ApiResponse.<PaginationResponse<PropertiesDTO>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(propertiesService.searchProperties(request, pageNo, pageSize, filter, sort))
        .build();
  }

  @PatchMapping("/{id}")
  ApiResponse<Void> changeStatus(
      @PathVariable UUID id
  ) {
    propertiesService.changeStatus(id);
    return ApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .build();
  }

  @PutMapping("/{id}")
  ApiResponse<PropertiesResponse> update(
      @PathVariable UUID id,
      @Valid @RequestBody PropertiesRequest request
  ) {
    return ApiResponse.<PropertiesResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(propertiesService.update(id, request))
        .build();
  }

  @GetMapping("/{id}")
  ApiResponse<PropertiesDetailDTO> getPropertiesDetail(
      @PathVariable UUID id
  ) {
    return ApiResponse.<PropertiesDetailDTO>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(propertiesService.getPropertiesDetail(id))
        .build();
  }

  @GetMapping("/{id}/reviews")
  ApiResponse<PaginationResponse<PropertiesDTO>> getPropertiesReviews(
      @PathVariable UUID id
  ) {
    return ApiResponse.<PaginationResponse<PropertiesDTO>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(propertiesService.getPropertiesReviews(id))
        .build();
  }

}
