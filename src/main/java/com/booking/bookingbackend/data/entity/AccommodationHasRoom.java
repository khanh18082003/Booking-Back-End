package com.booking.bookingbackend.data.entity;

import com.booking.bookingbackend.data.base.AbstractIdentifiable;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "accommodation_has_room")
public class AccommodationHasRoom extends AbstractIdentifiable<Integer> {

  @Serial
  private static final long serialVersionUID = -8738236214493027390L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  Integer id;

  @ToString.Exclude
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "accommodation_id", nullable = false)
  Accommodation accommodation;

  @ToString.Exclude
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "room_type_id", nullable = false)
  RoomType roomType;

  @Column(name = "room_name", nullable = false)
  String roomName;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_at")
  @CreationTimestamp
  Timestamp createdAt;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "updated_at")
  @UpdateTimestamp
  Timestamp updatedAt;

  @OneToMany(mappedBy = "accommodationRoom",cascade = CascadeType.ALL, orphanRemoval = true)
  Set<RoomHasBed> roomHasBedList;
}