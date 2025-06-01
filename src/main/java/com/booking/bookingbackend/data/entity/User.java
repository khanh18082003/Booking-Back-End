package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.data.base.UUIDJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serial;
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


// Adaptee
@Entity
@Table(name = "tbl_user")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends UUIDJpaEntity {

  @Serial
  private static final long serialVersionUID = -820978303502982886L;

  @Id
  @UuidGenerator(style = Style.TIME)
  UUID id;

  @Column(name = "email", nullable = false, unique = true)
  String email;

  @Column(name = "password", nullable = false)
  String password;

  @Column(name = "is_active", nullable = false)
  boolean active;

  @Column(name = "created_at")
  @CreationTimestamp
  Timestamp createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  Timestamp updatedAt;

  @ToString.Exclude
  @ManyToMany
  @JoinTable(
      name = "user_has_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  Set<Role> roles;

  @ToString.Exclude
  @OneToOne(mappedBy = "user")
  Profile profile;

  @ToString.Exclude
  @OneToMany(mappedBy = "host")
  Set<Properties> properties;

  @ToString.Exclude
  @OneToMany(mappedBy = "user")
  Set<Review> reviews;

  @ToString.Exclude
  @OneToMany(mappedBy = "user")
  Set<Booking> bookings;


}
