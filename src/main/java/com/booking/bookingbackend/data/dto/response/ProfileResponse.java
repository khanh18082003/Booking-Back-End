package com.booking.bookingbackend.data.dto.response;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import com.booking.bookingbackend.constant.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@JsonInclude(Include.NON_NULL)
public class ProfileResponse implements Serializable {
    UUID id;
    String email;
    String avatar;

    @JsonProperty("country_code")
    String countryCode;

    @JsonProperty("phone_number")
    String phoneNumber;

    LocalDate dob;
    Gender gender;
    String address;

    @JsonProperty("first_name")
    String firstName;

    @JsonProperty("last_name")
    String lastName;

    String nationality;
}
