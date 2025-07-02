package com.booking.bookingbackend.constant;

import lombok.Getter;

@Getter
public enum DeviceType {
    WEB("web"),
    ANDROID("android"),
    IOS("ios");

    private final String type;

    DeviceType(String type) {
        this.type = type;
    }
}
