package com.booking.bookingbackend.data.entity;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@RedisHash(value = "verification_code", timeToLive = 60 * 10)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisVerificationCode implements Serializable {
    String id;
    String code;
}
