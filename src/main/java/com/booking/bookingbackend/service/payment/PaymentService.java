package com.booking.bookingbackend.service.payment;

import com.booking.bookingbackend.data.dto.request.PaymentRequest;
import com.booking.bookingbackend.data.dto.response.PaymentResponse;
import com.booking.bookingbackend.data.entity.Payment;
import com.booking.bookingbackend.data.repository.PaymentRepository;
import com.booking.bookingbackend.service.BaseEntityService;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService extends BaseEntityService<UUID, Payment, PaymentRepository, PaymentResponse> {
    @Override
    default Class<?> getEntityClass() {
        return Payment.class;
    }

    PaymentResponse save(PaymentRequest request);

    PaymentResponse getPayment(UUID id);


    Boolean checkPaymentOnlineStatus(UUID id ,int expectedAmount, String expectedTransactionId) throws Exception;

    void payComplete(UUID id);
}
