package com.booking.bookingbackend.data.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "invalid_token")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisInvalidToken implements Serializable {

  String id;
  String token;
  Date expireTime;
}
