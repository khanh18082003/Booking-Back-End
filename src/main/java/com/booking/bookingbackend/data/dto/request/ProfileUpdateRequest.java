package com.booking.bookingbackend.data.dto.request;

import com.booking.bookingbackend.data.validator.GenderValid;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDate;

public record ProfileUpdateRequest(
  String avatar,
  @JsonProperty("country_code")
  String countryCode,
  @JsonProperty("phone_number")
  String phoneNumber,
  LocalDate dob,
  @GenderValid String gender,
  String address,
  @JsonProperty("first_name")
  String firstName,
  @JsonProperty("last_name")
  String lastName
) implements Serializable {

}
