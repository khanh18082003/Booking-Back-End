package com.booking.bookingbackend.service.payment;

import java.util.UUID;

import com.booking.bookingbackend.data.dto.request.PaymentRequest;
import com.booking.bookingbackend.data.dto.response.PaymentResponse;
import com.booking.bookingbackend.data.entity.Payment;
import com.booking.bookingbackend.data.repository.PaymentRepository;
import com.booking.bookingbackend.service.BaseEntityService;

public interface PaymentService extends BaseEntityService<UUID, Payment, PaymentRepository, PaymentResponse> {
    @Override
    default Class<?> getEntityClass() {
        return Payment.class;
    }

    PaymentResponse save(PaymentRequest request);

    PaymentResponse getPayment(UUID id);

    Boolean checkPaymentOnlineStatus(UUID id, int expectedAmount, String expectedTransactionId) throws Exception;

    void payComplete(UUID id);
}
