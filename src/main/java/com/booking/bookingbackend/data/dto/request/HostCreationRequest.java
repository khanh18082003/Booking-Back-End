package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HostCreationRequest(
        String email,
        String password,
        @JsonProperty("confirm_password") String confirmPassword,
        Collection<String> roles)
        implements Serializable {}
