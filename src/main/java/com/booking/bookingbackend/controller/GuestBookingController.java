package com.booking.bookingbackend.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.GuestBookingRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.GuestBookingResponse;
import com.booking.bookingbackend.service.guest.GuestBookingService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_GUEST_BOOKING)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "GUEST-BOOKING-CONTROLLER")
public class GuestBookingController {

    GuestBookingService guestBookingService;

    @PostMapping
    public ApiResponse<GuestBookingResponse> save(@Valid @RequestBody GuestBookingRequest guestBookingRequest) {
        return ApiResponse.<GuestBookingResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(guestBookingService.save(guestBookingRequest))
                .build();
    }
}
