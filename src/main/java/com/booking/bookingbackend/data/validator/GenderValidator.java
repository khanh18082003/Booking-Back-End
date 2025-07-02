package com.booking.bookingbackend.data.validator;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.booking.bookingbackend.constant.Gender;

public class GenderValidator implements ConstraintValidator<GenderValid, String> {
    private List<Gender> genders;

    @Override
    public void initialize(GenderValid constraintAnnotation) {
        genders = Arrays.asList(constraintAnnotation.anyOf());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // Cho phép bỏ qua nếu không có dữ liệu

        try {
            Gender gender = Gender.valueOf(value.toUpperCase());
            return genders.contains(gender);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
