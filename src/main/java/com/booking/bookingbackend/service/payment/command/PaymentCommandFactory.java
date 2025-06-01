package com.booking.bookingbackend.service.payment.command;

import com.booking.bookingbackend.constant.PaymentMethod;

public class PaymentCommandFactory {

    public static PaymentCommand getCommand(PaymentMethod method) {
        return switch (method) {
            case ONLINE -> new OnlinePaymentCommand();
            case CASH -> new CashPaymentCommand();
            default -> throw new IllegalArgumentException("Unsupported payment method: " + method);
        };
    }
}
