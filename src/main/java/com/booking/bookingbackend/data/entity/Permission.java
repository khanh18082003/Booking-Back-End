package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.constant.Method;
import com.booking.bookingbackend.data.base.AbstractIdentifiable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.sql.Timestamp;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "tbl_permission")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends AbstractIdentifiable<Integer> {

  @Serial
  private static final long serialVersionUID = -3328462773117569902L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @Column(name = "method", nullable = false)
  @Enumerated(EnumType.STRING)
  Method method;

  @Column(name = "url", nullable = false)
  String url;

  @Column(name = "description")
  String description;

  @Column(name = "created_at")
  @CreationTimestamp
  Timestamp createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  Timestamp updatedAt;

}
