package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.data.base.UUIDJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.io.Serial;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalTime;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_properties")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Properties extends UUIDJpaEntity {

  @Serial
  private static final long serialVersionUID = -7289484180496429846L;

  @Id
  @UuidGenerator(style = Style.TIME)
  UUID id;

  @Column(name = "name", nullable = false)
  String name;

  @Column(name = "image")
  String image;

  @Column(name = "description")
  String description;

  @Column(name = "address")
  String address;

  @Column(name = "ward")
  String ward;

  @Column(name = "district")
  String district;

  @Column(name = "city")
  String city;

  @Column(name = "province")
  String province;

  @Column(name = "country")
  String country;

  @Column(name = "rating", precision = 3, scale = 1, nullable = false)
  BigDecimal rating;

  @Column(name = "total_rating", nullable = false)
  Integer totalRating;

  @Column(name = "status")
  boolean status;

  @Column(name = "latitude")
  Double latitude;

  @Column(name = "longitude")
  Double longitude;

  @Column(name = "geom")
  Point geom;

  @Column(name = "check_in_time")
  LocalTime checkInTime;

  @Column(name = "check_out_time")
  LocalTime checkOutTime;

  @Column(name = "created_at")
  @CreationTimestamp
  Timestamp createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  Timestamp updatedAt;

  @ToString.Exclude
  @ManyToMany
  @JoinTable(
      name = "tbl_properties_amenities",
      joinColumns = @JoinColumn(name = "properties_id"),
      inverseJoinColumns = @JoinColumn(name = "amenities_id")
  )
  Set<Amenities> amenities;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "host_id",
      referencedColumnName = "id",
      nullable = false)
  User host;

  @ToString.Exclude
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "type_id",
      referencedColumnName = "id",
      nullable = false
  )
  PropertyType propertyType;

  @ToString.Exclude
  @OneToMany(mappedBy = "properties")
  Set<Accommodation> accommodations;

  @ToString.Exclude
  @OneToMany(mappedBy = "properties")
  Set<Review> reviews;

  @ToString.Exclude
  @OneToMany(mappedBy = "properties")
  Set<Booking> bookings;

  @PrePersist
  public void onPrePersist() {
    this.rating = rating == null ? BigDecimal.ZERO : this.rating;
    this.totalRating = totalRating == null ? 0 : this.totalRating;
  }
}
