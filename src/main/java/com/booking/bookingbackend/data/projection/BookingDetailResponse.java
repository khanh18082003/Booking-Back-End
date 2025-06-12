package com.booking.bookingbackend.data.projection;

import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.constant.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookingDetailResponse implements Serializable {

  private UUID id;
  private LocalDate checkIn;
  private LocalDate checkOut;
  private Integer adultUnits;
  private Integer childUnits;
  private BigDecimal totalPrice;
  private BookingStatus status;
  private String propertiesName;
  private String fullName;
  private String email;
  private String phone;
  private Boolean paymentStatus;
  private PaymentMethod paymentMethod;
  private Set<AccommodationBaseBookingResponse> accommodations;
  private Timestamp createdAt;

}
