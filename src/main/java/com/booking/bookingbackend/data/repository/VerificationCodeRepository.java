package com.booking.bookingbackend.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.booking.bookingbackend.data.entity.RedisVerificationCode;

@Repository
public interface VerificationCodeRepository extends CrudRepository<RedisVerificationCode, String> {}
