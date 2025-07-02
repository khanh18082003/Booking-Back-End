package com.booking.bookingbackend.data.entity;

import java.io.Serial;
import java.sql.Timestamp;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;

import com.booking.bookingbackend.data.base.UUIDJpaEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tbl_images")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image extends UUIDJpaEntity {
    @Serial
    private static final long serialVersionUID = 4126747677358520923L;

    @Id
    @UuidGenerator(style = Style.TIME)
    UUID id;

    @Column(name = "reference_id", nullable = false)
    String referenceId;

    @Column(name = "reference_type", nullable = false)
    String referenceType;

    @Column(name = "url", nullable = false)
    String url;

    @Column(name = "created_at")
    @CreationTimestamp
    Timestamp createdAt;
}
