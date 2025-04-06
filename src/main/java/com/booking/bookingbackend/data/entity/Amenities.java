package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.constant.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_amenities")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Amenities {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    UUID id;
    String name;
    String icon;
    @Enumerated(EnumType.STRING)
    Gender type;

    @ManyToMany
    @JoinTable(
            name = "tbl_properties_amenities",
            joinColumns = @JoinColumn(name = "amenities_id"),
            inverseJoinColumns = @JoinColumn(name = "properties_id")
    )
    Set<Properties> properties;
}
