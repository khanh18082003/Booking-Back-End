package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.data.base.UUIDJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.math.BigDecimal;
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
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;

@Entity
@Table(name = "tbl_available")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Available extends UUIDJpaEntity {

  @Serial
  private static final long serialVersionUID = 1666931288627522644L;

  @Id
  @UuidGenerator(style = Style.TIME)
  UUID id;

  @Column(name = "date", nullable = false)
  LocalDate date;

  @Column(name = "available_units", nullable = false)
  int availableUnits;

  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  BigDecimal price;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "accommodation_id", nullable = false)
  Accommodation accommodation;

}
