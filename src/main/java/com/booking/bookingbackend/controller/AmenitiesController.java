package com.booking.bookingbackend.controller;


import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.AmenitiesRequest;
import com.booking.bookingbackend.data.dto.response.AmenitiesResponse;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.service.amenities.AmenitiesService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_AMENITIES)
@FieldDefaults(makeFinal = true, level= AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AmenitiesController {
    AmenitiesService amenitiesService;

    @PostMapping
    ApiResponse<AmenitiesResponse> save(@Valid @RequestBody AmenitiesRequest request) {
        return ApiResponse.<AmenitiesResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(amenitiesService.save(request))
                .build();
    }
}
