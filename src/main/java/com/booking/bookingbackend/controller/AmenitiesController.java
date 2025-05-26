package com.booking.bookingbackend.controller;


import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.AmenitiesRequest;
import com.booking.bookingbackend.data.dto.response.AmenitiesResponse;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.Meta;
import com.booking.bookingbackend.data.dto.response.PaginationResponse;
import com.booking.bookingbackend.service.amenities.AmenitiesService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    @PutMapping("/{id}")
    ApiResponse<AmenitiesResponse> update(@PathVariable UUID id, @Valid @RequestBody AmenitiesRequest request) {
        return ApiResponse.<AmenitiesResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(amenitiesService.update(id, request))
                .build();
    }
    @GetMapping("/{id}")
    ApiResponse<AmenitiesResponse> findById(@PathVariable UUID id) {
        return ApiResponse.<AmenitiesResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(amenitiesService.findById(id))
                .build();
    }
    @GetMapping
    @PreAuthorize("hasRole('HOST')")
    ApiResponse<PaginationResponse<AmenitiesResponse>> findAll(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize)
    {
        Order order=new Order(Direction.ASC, "name");
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(order));
        Page<AmenitiesResponse> page= amenitiesService.findAll(pageable);
        PaginationResponse<AmenitiesResponse> result=PaginationResponse.<AmenitiesResponse>builder()
                .meta(Meta.builder()
                        .page(pageNo)
                        .pageSize(pageSize)
                        .pages(page.getTotalPages())
                        .total(page.getTotalPages())
                        .build())
                .data(page.getContent())
                .build();
        return ApiResponse.<PaginationResponse<AmenitiesResponse>>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(result)
                .build();
    }
}
