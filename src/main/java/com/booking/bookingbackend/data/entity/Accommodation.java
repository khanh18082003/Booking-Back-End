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
import jakarta.persistence.Table;
import java.io.Serial;
import java.math.BigDecimal;
import java.sql.Timestamp;
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

@Entity
@Table(name = "tbl_accommodation")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Accommodation extends UUIDJpaEntity {

  @Serial
  private static final long serialVersionUID = 2380452352465751964L;

  @Id
  @UuidGenerator(style = Style.TIME)
  UUID id;

  @Column(name = "name", nullable = false)
  String name;

  @Column(name = "base_price", precision = 10, scale = 2, nullable = false)
  BigDecimal basePrice;

  @Column(name = "capacity_adult", nullable = false)
  int capacityAdult;

  @Column(name = "capacity_children", nullable = false)
  int capacityChildren;

  @Column(name = "total_units")
  int totalUnits;

  String description;

  float size;

  String unit;

  @Column(name = "created_at")
  @CreationTimestamp
  Timestamp createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  Timestamp updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "properties_id",
      referencedColumnName = "id",
      nullable = false
  )
  Properties properties;

  @ManyToMany
  @JoinTable(name = "tbl_accommodation_amenities",
      joinColumns = @JoinColumn(name = "accommodation_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "amenities_id", referencedColumnName = "id")
  )
  Set<Amenities> amenities;

  @ToString.Exclude
  @OneToMany(mappedBy = "accommodation")
  Set<Available> available;
}
