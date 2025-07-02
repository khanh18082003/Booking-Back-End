package com.booking.bookingbackend.service.notification;

import org.springframework.stereotype.Service;

import com.booking.bookingbackend.data.entity.RedisVerificationCode;
import com.booking.bookingbackend.data.repository.VerificationCodeRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class VerificationCodeService {
    VerificationCodeRepository repository;

    public void saveCode(String code, String email) {
        RedisVerificationCode verificationCode =
                RedisVerificationCode.builder().id(email).code(code).build();
        repository.save(verificationCode);
    }
}
