package com.booking.bookingbackend.data.dto.response;

import com.booking.bookingbackend.constant.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserProfileDto(
    UUID id,
    String email,
    @JsonProperty("is_active") Boolean isActive,
    @JsonProperty("profile_id") UUID profileId,
    String avatar,
    String phone,
    LocalDate dob,
    Gender gender,
    String address,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    String name,
    @JsonProperty("country_code") String countryCode,
    String nationality
) {

}
