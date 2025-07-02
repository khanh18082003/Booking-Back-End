package com.booking.bookingbackend.data.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import com.booking.bookingbackend.constant.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse implements Serializable {
    @JsonProperty("id")
    UUID id;

    @JsonProperty("amount")
    BigDecimal amount;

    @JsonProperty("payment_method")
    PaymentMethod paymentMethod;

    @JsonProperty("status")
    boolean status;

    @JsonProperty("transaction_id")
    String transactionId;

    @JsonProperty("paid_at")
    Timestamp paidAt;

    @JsonProperty("url_image")
    String urlImage;
}
