package com.booking.bookingbackend.data.validator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.booking.bookingbackend.constant.Gender;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = GenderValidator.class)
public @interface GenderValid {

    Gender[] anyOf() default {Gender.MALE, Gender.FEMALE, Gender.OTHER};

    String message() default "must be any of {anyOf}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
