package com.booking.bookingbackend.controller;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.CommonConstant;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.PropertiesRequest;
import com.booking.bookingbackend.data.dto.request.RoleRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.PropertiesResponse;
import com.booking.bookingbackend.service.properties.PropertiesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_PROPERTY)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "PROPERTIES-CONTROLLER")
public class PropertiesController {

  PropertiesService propertiesService;

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
    return ApiResponse.<PropertiesResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(propertiesService.save(request))
        .build();
  }

  @GetMapping("/search")
  ApiResponse<List<PropertiesResponse>> search(
      @RequestParam(name = "location") String location,
      @RequestParam(name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam(name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
      @RequestParam(name = "pageSize", defaultValue = "20") int pageSize
  ) {
    return ApiResponse.<List<PropertiesResponse>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(propertiesService.search(location, startDate, endDate, pageNo, pageSize))
        .build();
  }

  @PatchMapping("/id")
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

  @PutMapping("/id")
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
}
