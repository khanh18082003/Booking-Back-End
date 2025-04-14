package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.data.base.UUIDJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;

@Entity
@Table(name = "tbl_booking")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends UUIDJpaEntity {

  @Serial
  private static final long serialVersionUID = -3731957008644996712L;

  @Id
  @UuidGenerator(style = Style.TIME)
  UUID id;

  @Column(name = "check_in", nullable = false)
  LocalDate checkIn;

  @Column(name = "check_out", nullable = false)
  LocalDate checkOut;

  @Column(name = "adult_units", nullable = false)
  Integer adultUnits;

  @Column(name = "children_units", nullable = false)
  Integer childUnits;

  @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
  BigDecimal totalPrice;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  BookingStatus status;

  @Column(name = "created_at")
  @CreationTimestamp
  Timestamp createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  Timestamp updatedAt;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  User user;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "properties_id", nullable = false)
  Properties properties;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "guest_id")
  GuestBooking guestBooking;

  @ToString.Exclude
  @OneToOne(mappedBy = "booking")
  Payment payment;
}
