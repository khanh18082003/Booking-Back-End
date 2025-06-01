package com.booking.bookingbackend.service.payment.command;
import com.booking.bookingbackend.data.dto.request.PaymentRequest;
import com.booking.bookingbackend.data.entity.Payment;
public class CashPaymentCommand implements PaymentCommand{
    @Override
    public void execute(Payment payment, PaymentRequest request) {
        payment.setStatus(true);
        payment.setUrlImage(null);
    }
}
