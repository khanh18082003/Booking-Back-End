package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.data.base.AbstractIdentifiable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.sql.Timestamp;
import java.util.List;
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

@Entity
@Table(name = "tbl_role")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends AbstractIdentifiable<Integer> {

  @Serial
  private static final long serialVersionUID = 1666931288627522644L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @Column(name = "name", nullable = false)
  String name;

  @Column(name = "description")
  String description;

  @Column(name = "created_at")
  @CreationTimestamp
  Timestamp createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  Timestamp updatedAt;

  @ToString.Exclude
  @ManyToMany
  @JoinTable(
      name = "role_has_permission",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id")
  )
  List<Permission> permissions;
}
