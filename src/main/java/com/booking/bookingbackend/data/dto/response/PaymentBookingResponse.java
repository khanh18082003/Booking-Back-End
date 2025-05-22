package com.booking.bookingbackend.data.dto.response;

import com.booking.bookingbackend.constant.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentBookingResponse implements Serializable {
  @JsonProperty("payment_method")
  PaymentMethod paymentMethod;
  boolean status;
  BigDecimal amount;
  @JsonProperty("transaction_id")
  String transactionId;
}
