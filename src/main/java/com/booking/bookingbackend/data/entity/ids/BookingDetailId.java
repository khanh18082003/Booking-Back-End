package com.booking.bookingbackend.data.entity.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import lombok.experimental.FieldDefaults;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@With
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDetailId implements Serializable {

  @Serial
  private static final long serialVersionUID = -5602276862764545365L;

  @Column(name = "booking_id", nullable = false)
  UUID bookingId;

  @Column(name = "accommodation_id", nullable = false)
  UUID accommodationId;
}
