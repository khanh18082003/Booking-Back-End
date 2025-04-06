package com.booking.bookingbackend.data.dto.response;

import com.booking.bookingbackend.data.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertiesResponse implements Serializable {

    String name;
    String description;
    String address;
    String city;
    String country;
    String district;
    String rating;
    boolean status;
    String latitude;
    String longitude;
    String checkInTime;
    String checkOutTime;
}
