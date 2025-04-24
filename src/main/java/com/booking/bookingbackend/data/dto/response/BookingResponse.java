package com.booking.bookingbackend.data.dto.response;

import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.data.entity.GuestBooking;
import com.booking.bookingbackend.data.entity.Payment;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponse implements Serializable {
    @JsonProperty("check_in")
    LocalDate checkIn;
    @JsonProperty("check_out")
    LocalDate checkOut;
    @JsonProperty("adult_units")
    Integer adultUnits;
    @JsonProperty("child_units")
    Integer childUnits;
    @JsonProperty("total_price")
    BigDecimal totalPrice;
    BookingStatus status;
    @JsonProperty("created_at")
    Timestamp createdAt;
    @JsonProperty("updated_at")
    Timestamp updatedAt;
    User user;
    Properties properties;
    @JsonProperty("guest_booking")
    GuestBooking guestBooking;
    Payment payment;
}
