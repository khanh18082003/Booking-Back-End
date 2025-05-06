package com.booking.bookingbackend.data.dto.response;

import com.booking.bookingbackend.constant.PaymentMethod;
import com.booking.bookingbackend.constant.PaymentStatus;
import com.booking.bookingbackend.data.entity.Booking;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse implements Serializable {
    @JsonProperty("amount")
    BigDecimal amount;
    @JsonProperty("payment_method")
    PaymentMethod paymentMethod;
    @JsonProperty("status")
    PaymentStatus status;
    @JsonProperty("transaction_id")
    String transactionId;
    @JsonProperty("paid_at")
    Timestamp paidAt;
    @JsonProperty("booking")
    Booking booking;
}
