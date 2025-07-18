package com.booking.bookingbackend.data.entity;

import java.io.Serial;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;

import com.booking.bookingbackend.data.base.UUIDJpaEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

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

    @Column(name = "capacity")
    int capacity;

    @Column(name = "total_rooms", nullable = false)
    int totalRooms;

    @Column(name = "total_units")
    int totalUnits;

    @Column(name = "description")
    String description;

    @Column(name = "size")
    float size;

    @Column(name = "unit")
    String unit;

    @Column(name = "created_at")
    @CreationTimestamp
    Timestamp createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    Timestamp updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "properties_id", referencedColumnName = "id", nullable = false)
    Properties properties;

    @ManyToMany
    @JoinTable(
            name = "tbl_accommodation_amenities",
            joinColumns = @JoinColumn(name = "accommodation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "amenities_id", referencedColumnName = "id"))
    Set<Amenities> amenities;

    @ToString.Exclude
    @OneToMany(mappedBy = "accommodation")
    Set<Available> available;

    @ToString.Exclude
    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<AccommodationHasRoom> accommodationHasRooms;
}
