package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.data.base.AbstractIdentifiable;
import com.booking.bookingbackend.data.base.UUIDJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "tbl_user")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends UUIDJpaEntity {
  @Serial
  private static final long serialVersionUID = -820978303502982886L;

  @Id
  UUID id;

  @Column(name = "email", nullable = false, unique = true)
  String email;

  @Column(name = "password", nullable = false)
  String password;

  @Column(name = "is_active", nullable = false)
  boolean isActive;

  @Column(name = "created_at")
  @CreationTimestamp
  Timestamp createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  Timestamp updatedAt;
}
