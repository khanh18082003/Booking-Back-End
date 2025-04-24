package com.booking.bookingbackend.controller;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.PropertiesRequest;
import com.booking.bookingbackend.data.dto.request.RoleRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.PropertiesResponse;
import com.booking.bookingbackend.service.properties.PropertiesService;
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

    @PostMapping()
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
