package com.booking.bookingbackend.data.dto.request;

import java.io.Serializable;

public record VerificationEmailRequest(String code, String email) implements Serializable {}
