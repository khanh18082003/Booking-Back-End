package com.booking.bookingbackend.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.BookingRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.BookingResponse;
import com.booking.bookingbackend.service.booking.BookingService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_BOOKING)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "BOOKING-CONTROLLER")
public class BookingController {

    BookingService bookingService;

    @PostMapping
    ApiResponse<BookingResponse> save(@Valid @RequestBody BookingRequest request)
            throws MessagingException, UnsupportedEncodingException {
        return ApiResponse.<BookingResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(bookingService.book(request))
                .build();
    }

    @PostMapping("/change-status")
    ApiResponse<BookingResponse> changeStatus(@Valid @RequestBody BookingStatus status, @Valid @RequestBody UUID id) {
        return ApiResponse.<BookingResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(bookingService.changeStatus(id, status))
                .build();
    }

    @GetMapping("/booking-history")
    ApiResponse<List<BookingResponse>> bookingHistory(@RequestParam("userId") UUID userId) {
        return ApiResponse.<List<BookingResponse>>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(bookingService.BookingHistory(userId))
                .build();
    }

    @PostMapping("/cancel-booking/{id}")
    ApiResponse<Void> cancelBooking(@PathVariable("id") UUID bookingId) {
        bookingService.cancelBooking(bookingId);
        return ApiResponse.<Void>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .build();
    }

    @PreAuthorize("hasRole('HOST')")
    @PatchMapping("/{id}/complete")
    ApiResponse<Void> bookingConfirmation(@PathVariable("id") UUID bookingId) {
        bookingService.bookingConfirmation(bookingId);
        return ApiResponse.<Void>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .build();
    }

    @PreAuthorize("hasRole('HOST')")
    @PatchMapping("/{id}/cancellation")
    ApiResponse<Void> bookingCancellation(@PathVariable("id") UUID bookingId) {
        bookingService.bookingCancellation(bookingId);
        return ApiResponse.<Void>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .build();
    }
}
