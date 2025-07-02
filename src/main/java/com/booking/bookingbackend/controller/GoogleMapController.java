package com.booking.bookingbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.service.googlemap.GoogleMapService;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_LOCATION)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "GOOGLE-MAP-CONTROLLER")
public class GoogleMapController {

    GoogleMapService googleMapService;

    @GetMapping
    ApiResponse<JsonNode> getLocation(
            @RequestParam("location") String location,
            @RequestParam(value = "limit", defaultValue = "5") String limit) {
        log.info("Get location: {}", location);
        JsonNode result = googleMapService.getLocation(location, limit);
        return ApiResponse.<JsonNode>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(result)
                .build();
    }
}
