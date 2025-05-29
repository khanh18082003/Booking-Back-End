package com.booking.bookingbackend.data.dto.response;

import com.booking.bookingbackend.constant.BookingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RevenueResponse {
    Timestamp date;
    String nameProperty;
    String nameAccommodation;
    int quantity;
    Date checkIn;
    Date checkOut;
    BigDecimal totalPrice;
    String status;
}
