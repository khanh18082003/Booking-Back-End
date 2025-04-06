package com.booking.bookingbackend.data.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_property_type")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PropertyType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String name;
    String description;
    @Column(name = "created_at")
    Timestamp createdAt;
    @Column(name = "updated_at")
    Timestamp updatedAt;

}
