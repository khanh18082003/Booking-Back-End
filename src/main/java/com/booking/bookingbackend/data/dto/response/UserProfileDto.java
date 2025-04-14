package com.booking.bookingbackend.data.dto.response;

import com.booking.bookingbackend.constant.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserProfileDto(
    UUID id,
    String email,
    Boolean isActive,
    UUID profileId,
    String avatar,
    String phone,
    LocalDate dob,
    Gender gender,
    String address,
    String firstName,
    String lastName,
    String countryCode
) {

}
