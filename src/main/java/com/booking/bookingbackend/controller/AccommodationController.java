package com.booking.bookingbackend.controller;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.AccommodationCreationRequest;
import com.booking.bookingbackend.data.dto.response.AccommodationResponse;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.PropertiesResponse;
import com.booking.bookingbackend.service.accommodation.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_ACCOMMODATION)
@FieldDefaults(makeFinal = true, level= AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AccommodationController {

  AccommodationService accommodationService;

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
  ApiResponse<AccommodationResponse> create(@Valid @RequestBody AccommodationCreationRequest request) {
    return ApiResponse.<AccommodationResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(accommodationService.save(request))
        .build();
  }
}
