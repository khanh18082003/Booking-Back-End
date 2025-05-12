package com.booking.bookingbackend.data.dto.response;

import com.booking.bookingbackend.constant.AmenityType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmenitiesResponse implements Serializable {

  UUID id;

  String name;

  String icon;

  AmenityType type;

  public AmenitiesResponse(UUID id, String name) {
    this.id = id;
    this.name = name;
  }
}
