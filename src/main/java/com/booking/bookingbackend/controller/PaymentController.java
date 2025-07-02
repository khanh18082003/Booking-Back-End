package com.booking.bookingbackend.controller;

import java.util.UUID;

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
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.PaymentRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.PaymentResponse;
import com.booking.bookingbackend.service.payment.PaymentService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_PAYMENT)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "PAYMENT-CONTROLLER")
public class PaymentController {

    PaymentService paymentService;

    @PostMapping
    ApiResponse<PaymentResponse> save(@Valid @RequestBody PaymentRequest request) {
        return ApiResponse.<PaymentResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(paymentService.save(request))
                .build();
    }

    @GetMapping("/get-payment")
    ApiResponse<PaymentResponse> getPayment(@RequestParam("id") UUID id) {
        return ApiResponse.<PaymentResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(paymentService.getPayment(id))
                .build();
    }

    @GetMapping("/check-payment-status")
    ApiResponse<Boolean> checkPaymentStatus(
            @RequestParam("id") UUID id,
            @RequestParam("expectedAmount") int expectedAmount,
            @RequestParam("expectedTransactionId") String expectedTransactionId)
            throws Exception {
        return ApiResponse.<Boolean>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(paymentService.checkPaymentOnlineStatus(id, expectedAmount, expectedTransactionId))
                .build();
    }

    @PreAuthorize("hasRole('HOST')")
    @PatchMapping("/bookings/{id}")
    ApiResponse<Void> paymentComplete(@PathVariable UUID id) {
        paymentService.payComplete(id);
        return ApiResponse.<Void>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .build();
    }
}
