package com.booking.bookingbackend.data.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReviewCreationRequest(
        String review,
        @Min(value = 1, message = "MESSAGE_RATING_MIN") @Max(value = 10, message = "MESSAGE_RATING_MAX") Integer rating,
        @JsonProperty("properties_id") UUID propertiesId) {}
