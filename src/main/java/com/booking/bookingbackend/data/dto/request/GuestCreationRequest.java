package com.booking.bookingbackend.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GuestCreationRequest(
    @JsonProperty("first_name")
    String firstName,
    @JsonProperty("last_name")
    String lastName,
    @JsonProperty("email")
    String email,
    @JsonProperty("phone_number")
    String phoneNumber,
    @JsonProperty("country")
    String country
) {

}
