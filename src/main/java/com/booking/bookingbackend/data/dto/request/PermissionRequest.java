package com.booking.bookingbackend.data.dto.request;

import com.booking.bookingbackend.data.validator.HttpMethodValidator;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;


public record PermissionRequest(
    @HttpMethodValidator(message = "MESSAGE_INVALID_HTTP_METHOD")
    @NotBlank(message = "MESSAGE_NOT_BLANK")
    String method,
    @NotBlank(message = "MESSAGE_NOT_BLANK") String url,
    String description
) implements Serializable {

}
