package com.booking.bookingbackend.data.dto.request;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import com.booking.bookingbackend.constant.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record PaymentRequest(
        BigDecimal amount,
        @JsonProperty("payment_method") PaymentMethod paymentMethod,
        Boolean status,
        @JsonProperty("transaction_id") String transactionId,
        String description,
        @JsonProperty("paid_at") Timestamp paidAt,
        @JsonProperty("booking_id") UUID bookingId) {}
