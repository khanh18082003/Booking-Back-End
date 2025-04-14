package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.constant.AmenityType;
import com.booking.bookingbackend.data.base.UUIDJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_amenities")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Amenities extends UUIDJpaEntity {

  @Serial
  private static final long serialVersionUID = 7278871004311142720L;

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  UUID id;

  String name;

  String icon;

  @Enumerated(EnumType.STRING)
  AmenityType type;
}
