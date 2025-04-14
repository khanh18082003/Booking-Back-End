package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.data.base.UUIDJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.util.Set;
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
@Table(name = "tbl_guest_booking")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestBooking extends UUIDJpaEntity {

  @Serial
  private static final long serialVersionUID = 5519473221049358896L;

  @Id
  @UuidGenerator(style = Style.TIME)
  UUID id;

  @Column(name = "email", nullable = false)
  String email;

  @Column(name = "first_name", nullable = false)
  String firstName;

  @Column(name = "last_name", nullable = false)
  String lastName;

  @Column(name = "phone_number", nullable = false)
  String phoneNumber;

  @Column(name = "country", nullable = false)
  String country;

  @Column(name = "note")
  String note;

  @ToString.Exclude
  @OneToMany(mappedBy = "guestBooking")
  Set<Booking> bookings;
}
