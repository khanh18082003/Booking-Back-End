package com.booking.bookingbackend.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class UserCreationResponse implements Serializable {

  UUID id;
  String email;
  @JsonProperty("is_active")
  boolean isActive;
  @JsonProperty("created_at")
  Timestamp createdAt;
  @JsonProperty("updated_at")
  Timestamp updatedAt;
  Set<RoleResponse> roles;
}
