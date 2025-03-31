package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.entity.RedisInvalidToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidTokenRepository extends CrudRepository<RedisInvalidToken, String> {

}
