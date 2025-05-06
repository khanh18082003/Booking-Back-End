package com.booking.bookingbackend.controller;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.BookingStatus;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_BOOKING)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "BOOKING-CONTROLLER")
public class BookingController {
    BookingService bookingService;

    @PostMapping("/save")
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
            @Valid @RequestBody BookingStatus status,
            @Valid @RequestBody UUID id
    ) {
        return ApiResponse.<BookingResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(bookingService.changeStatus(id, status))
                .build();
    }
    @GetMapping("/booing-history")
    ApiResponse<List<BookingResponse>> bookingHistory(
            @RequestParam("userId") UUID userId
    ) {
        return ApiResponse.<List<BookingResponse>>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(bookingService.BookingHistory(userId))
                .build();
    }
}
