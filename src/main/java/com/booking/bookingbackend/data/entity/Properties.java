package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.data.base.UUIDJpaEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_properties")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Properties extends UUIDJpaEntity {
    @Id
    UUID id;
    String name;
    String description;
    String address;
    String city;
    String country;
    String district;
    String rating;
    boolean status;
    String latitude;
    String longitude;
    @Column(name = "check_in_time")
    Time checkInTime;
    @Column(name = "check_out_time")
    Time checkOutTime;
    @Column(name = "created_at")
    @CreationTimestamp
    Timestamp createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    Timestamp updatedAt;

    @ManyToMany
    @JoinTable(
            name = "tbl_properties_amenities",
            joinColumns = @JoinColumn(name = "properties_id"),
            inverseJoinColumns = @JoinColumn(name = "amenities_id")
    )
    Set<Amenities> amenities;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    User host;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    PropertyType propertyType;
}
