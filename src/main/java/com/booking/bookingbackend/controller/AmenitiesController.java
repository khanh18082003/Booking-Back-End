package com.booking.bookingbackend.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.AmenitiesRequest;
import com.booking.bookingbackend.data.dto.response.AmenitiesResponse;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.Meta;
import com.booking.bookingbackend.data.dto.response.PaginationResponse;
import com.booking.bookingbackend.data.projection.AmenitiesPropertiesDTO;
import com.booking.bookingbackend.service.amenities.AmenitiesService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_AMENITIES)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class AmenitiesController {

    AmenitiesService amenitiesService;

    @PostMapping
    ApiResponse<AmenitiesResponse> save(@Valid @RequestBody AmenitiesRequest request) {
        return ApiResponse.<AmenitiesResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(amenitiesService.save(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<AmenitiesResponse> update(@PathVariable UUID id, @Valid @RequestBody AmenitiesRequest request) {
        return ApiResponse.<AmenitiesResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(amenitiesService.update(id, request))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<AmenitiesResponse> findById(@PathVariable UUID id) {
        return ApiResponse.<AmenitiesResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(amenitiesService.findById(id))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('HOST')")
    ApiResponse<PaginationResponse<AmenitiesResponse>> findAll(
            @RequestParam(defaultValue = "1") int pageNo, @RequestParam(defaultValue = "20") int pageSize) {
        Order order = new Order(Direction.ASC, "name");
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(order));
        Page<AmenitiesResponse> page = amenitiesService.findAll(pageable);
        PaginationResponse<AmenitiesResponse> result = PaginationResponse.<AmenitiesResponse>builder()
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

    @GetMapping("/properties")
    ApiResponse<List<AmenitiesPropertiesDTO>> getAmenitiesByPropertyIds(
            @RequestParam("property_ids") UUID[] propertyIds) {

        return ApiResponse.<List<AmenitiesPropertiesDTO>>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(amenitiesService.getAmenitiesByPropertyIds(List.of(propertyIds)))
                .build();
    }
}
