package com.booking.bookingbackend.data.projection;

import com.booking.bookingbackend.constant.BookingStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface UserBookingsHistoryDTO extends Serializable {

  @JsonProperty("booking_id")
  UUID getBookingId();

  @JsonProperty("check_in")
  LocalDate getCheckIn();

  @JsonProperty("check_out")
  LocalDate getCheckOut();

  @JsonProperty("adults")
  Integer getAdults();

  @JsonProperty("children")
  Integer getChildren();

  @JsonProperty("total_price")
  BigDecimal getTotalPrice();

  @JsonProperty("status")
  BookingStatus getStatus();

  @JsonProperty("user_id")
  UUID getUserId();

  @JsonProperty("first_name")
  String getFirstName();

  @JsonProperty("last_name")
  String getLastName();

  @JsonProperty("email")
  String getEmail();

  @JsonProperty("phone")
  String getPhone();

  @JsonProperty("country")
  String getCountry();

  @JsonProperty("note")
  String getNote();

  @JsonProperty("property_id")
  UUID getPropertyId();

  @JsonProperty("property_name")
  String getPropertyName();

  @JsonProperty("property_image")
  String getPropertyImage();

  @JsonProperty("property_address")
  String getPropertyAddress();

  @JsonProperty("property_province")
  String getPropertyProvince();

  @JsonProperty("payment_id")
  UUID getPaymentId();

  @JsonProperty("payment_status")
  boolean getPaymentStatus();

  @JsonProperty("payment_image")
  String getPaymentImage();

  @JsonProperty("payment_method")
  String getPaymentMethod();

  @JsonProperty("transaction_id")
  String getTransactionId();
}
