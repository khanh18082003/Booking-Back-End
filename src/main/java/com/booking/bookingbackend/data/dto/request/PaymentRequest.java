package com.booking.bookingbackend.data.dto.request;

import com.booking.bookingbackend.constant.PaymentMethod;
import com.booking.bookingbackend.constant.PaymentStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        String transactionId,
        String description,
        Timestamp paidAt,
        UUID BookingId
) implements java.io.Serializable{
}
