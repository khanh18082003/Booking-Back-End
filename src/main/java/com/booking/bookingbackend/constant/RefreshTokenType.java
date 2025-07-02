package com.booking.bookingbackend.constant;

import lombok.Getter;

@Getter
public enum RefreshTokenType {
    HOST("refresh_token_host"),
    USER("refresh_token"),
    ADMIN("refresh_token_admin");

    private final String type;

    RefreshTokenType(final String type) {
        this.type = type;
    }
}
