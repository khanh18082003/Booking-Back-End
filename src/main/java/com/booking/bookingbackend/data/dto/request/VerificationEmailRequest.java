package com.booking.bookingbackend.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public record VerificationEmailRequest(
    String code,
    String email
) implements Serializable {

}
