package com.booking.bookingbackend.controller;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.BookingRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.BookingResponse;
import com.booking.bookingbackend.service.booking.BookingService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_PROPERTY)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "BOOKING-CONTROLLER")
public class BookingController {
    BookingService bookingService;

    @PostMapping()
    ApiResponse<BookingResponse> save(
            @Valid @RequestBody BookingRequest request
    ) {
        return ApiResponse.<BookingResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(bookingService.save(request))
                .build();
    }

    @PostMapping("/change-status")
    ApiResponse<BookingResponse> changeStatus(
            @Valid @RequestBody BookingRequest request
    ) {
        return ApiResponse.<BookingResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(bookingService.changeStatus(request.guestBookingID(), request))
                .build();
    }
}
