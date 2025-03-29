package com.booking.bookingbackend.data.dto.request;

import com.booking.bookingbackend.constant.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public record UserCreationRequest(
    @Email(message = "MESSAGE_INVALID_EMAIL")
    @NotBlank(message = "MESSAGE_NOT_BLANK")
    String email,
    @NotBlank(message = "MESSAGE_NOT_BLANK")
    @Size(min = 8, max = 20, message = "MESSAGE_INVALID_SIZE")
    String password,
    Collection<String> roles
) implements Serializable {

  public UserCreationRequest {
    roles = roles == null ? Set.of(UserRole.USER.name()) : roles;
  }
}
