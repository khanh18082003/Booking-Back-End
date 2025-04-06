package com.booking.bookingbackend.data.repository;

import com.booking.bookingbackend.data.entity.RedisVerificationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends CrudRepository<RedisVerificationCode, String> {

}
