package com.booking.bookingbackend.data.dto.request;

import com.booking.bookingbackend.constant.DeviceType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record OutboundAuthenticationAppRequest(
    String email,
    String firstName,
    String lastName,
    String avatarUrl,
    DeviceType deviceType
) {

}
