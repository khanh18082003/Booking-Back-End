package com.booking.bookingbackend.service.payment.command;

import com.booking.bookingbackend.data.dto.request.PaymentRequest;
import com.booking.bookingbackend.data.entity.Payment;

public interface PaymentCommand {
    void execute(Payment payment, PaymentRequest request);
}
