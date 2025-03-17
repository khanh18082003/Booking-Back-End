package com.booking.bookingbackend.data.dto.response;

import com.booking.bookingbackend.constant.Method;
import java.io.Serializable;
import java.sql.Timestamp;
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
public class PermissionResponse implements Serializable {
  int id;
  Method method;
  String url;
  String description;
  Timestamp createdAt;
  Timestamp updatedAt;
}
