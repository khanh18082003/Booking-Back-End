package com.booking.bookingbackend.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;

public record ReviewCreationRequest(
    String review,
    @Min(value = 1, message = "MESSAGE_RATING_MIN")
    @Max(value = 10, message = "MESSAGE_RATING_MAX")
    Integer rating,
    @JsonProperty("properties_id")
    UUID propertiesId
) {

}
