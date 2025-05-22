package com.booking.bookingbackend.controller;


import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.PaymentRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.PaymentResponse;
import com.booking.bookingbackend.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_PAYMENT)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "PAYMENT-CONTROLLER")
public class PaymentController {
    PaymentService paymentService;

    @PostMapping("/save")
    ApiResponse<PaymentResponse> save(
            @Valid @RequestBody PaymentRequest request
    ) {
        return ApiResponse.<PaymentResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(paymentService.save(request))
                .build();
    }

    @GetMapping("/get-payment")
    ApiResponse<PaymentResponse> getPayment(
            @RequestParam("id") UUID id
    ) {
        return ApiResponse.<PaymentResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(paymentService.getPayment(id))
                .build();
    }

    @PostMapping("/change-status")
    ApiResponse<PaymentResponse> changeStatus(
            @Valid @RequestBody UUID id,
            @Valid @RequestBody Boolean status
    ) {
        return ApiResponse.<PaymentResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(paymentService.changStatus(id, status))
                .build();
    }
    @GetMapping("/check-payment-status")
    ApiResponse<Boolean> checkPaymentStatus(
            @RequestParam("expectedAmount") int expectedAmount,
            @RequestParam("expectedTransactionId") String expectedTransactionId
    ) throws Exception {
        return ApiResponse.<Boolean>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(paymentService.checkPaymentStatus(expectedAmount, expectedTransactionId))
                .build();
    }
}
