package com.booking.bookingbackend.data.dto.request;

public record GuestBookingRequest(
        String email, String firstName, String lastName, String phoneNumber, String country, String note) {}
