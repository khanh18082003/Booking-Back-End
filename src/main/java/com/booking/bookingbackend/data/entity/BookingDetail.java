package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.data.base.AbstractIdentifiable;
import com.booking.bookingbackend.data.entity.ids.BookingDetailId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tbl_booking_detail")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDetail extends AbstractIdentifiable<BookingDetailId> {

  @Serial
  private static final long serialVersionUID = -5602276862764545365L;

  @EmbeddedId
  BookingDetailId id;

  @Column(name = "booked_units", nullable = false)
  Integer bookedUnits;

  @Column(name = "total_nights", nullable = false)
  Integer totalNights;

  @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
  BigDecimal pricePerNight;

  @Column(name = "tax", precision = 10, scale = 2)
  BigDecimal tax;

  @Column(name = "service_fee", precision = 10, scale = 2)
  BigDecimal serviceFee;

  @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
  BigDecimal totalPrice;
}
