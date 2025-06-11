package com.booking.bookingbackend.data.projection;

import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.constant.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public interface BookingDetailResponse extends Serializable {

  UUID getId();

  LocalDate getCheckIn();

  LocalDate getCheckOut();

  Integer getAdultUnits();

  Integer getChildUnits();

  BigDecimal getTotalPrice();

  BookingStatus getStatus();

  String getPropertiesName();

  String getFullName();

  String getEmail();

  String getPhone();

  Boolean getPaymentStatus();

  PaymentMethod getPaymentMethod();

  Set<AccommodationBaseBookingResponse> getAccommodations();

  Timestamp getCreatedAt();
}
