package com.booking.bookingbackend.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

public record RoleRequest(
  @NotBlank(message = "MESSAGE_NOT_BLANK") String name,
  String description,
  List<Integer> permissions
) implements Serializable {
}
