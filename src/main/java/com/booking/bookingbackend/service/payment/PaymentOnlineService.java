package com.booking.bookingbackend.service.payment;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentOnlineService {
    boolean processPayment(UUID id, BigDecimal amount, String transactionId);
}
