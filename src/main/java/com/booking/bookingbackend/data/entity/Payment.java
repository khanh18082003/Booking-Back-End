package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.constant.PaymentMethod;
import com.booking.bookingbackend.data.base.UUIDJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;

@Entity
@Table(name = "tbl_payment")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends UUIDJpaEntity {

  @Serial
  private static final long serialVersionUID = -6383790542466985068L;

  @Id
  @UuidGenerator(style = Style.TIME)
  UUID id;

  @Column(name = "amount", nullable = false, precision = 15, scale = 2)
  BigDecimal amount;

  @Column(name = "payment_method", nullable = false)
  @Enumerated(EnumType.STRING)
  PaymentMethod paymentMethod;

  @Column(name = "status", columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
  Boolean status;

  @Column(name = "transaction_id", nullable = false, unique = true)
  String transactionId;

  @Column(name = "paid_at", nullable = false)
  Timestamp paidAt;

  @Column(name = "url_image", nullable = false)
    String urlImage;

  @ToString.Exclude
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "booking_id", referencedColumnName = "id", nullable = false)
  Booking booking;
}
