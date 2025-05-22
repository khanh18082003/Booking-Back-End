package com.booking.bookingbackend.data.dto.response;

import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.data.entity.Payment;
import com.booking.bookingbackend.data.entity.Properties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
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
public class BookingResponse implements Serializable {

  @JsonProperty("booking_id")
  UUID id;
  @JsonProperty("check_in")
  LocalDate checkIn;
  @JsonProperty("check_out")
  LocalDate checkOut;
  @JsonProperty("adult_units")
  Integer adultUnits;
  @JsonProperty("children_units")
  Integer childUnits;
  @JsonProperty("total_price")
  BigDecimal totalPrice;
  BookingStatus status;
  @JsonProperty("user_booking")
  UserBookingResponse userBooking;
  @JsonProperty("properties_id")
  UUID propertiesId;
  List<AccommodationBookingResponse> accommodations;
  PaymentBookingResponse payment;
  @JsonProperty("created_at")
  Timestamp createdAt;
  @JsonProperty("updated_at")
  Timestamp updatedAt;
}
