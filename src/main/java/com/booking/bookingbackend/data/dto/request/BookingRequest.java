package com.booking.bookingbackend.data.dto.request;

import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.data.entity.GuestBooking;
import com.booking.bookingbackend.data.entity.Payment;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.entity.User;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

public record BookingRequest(
        LocalDate checkIn,
        LocalDate checkOut,
        Integer adultUnits,
        Integer childUnits,
        BigDecimal totalPrice,
        BookingStatus status,
        Timestamp createdAt,
        Timestamp updatedAt,
        UUID userId,
        UUID propertiesID,
        UUID guestBookingID
)implements java.io.Serializable {
}
