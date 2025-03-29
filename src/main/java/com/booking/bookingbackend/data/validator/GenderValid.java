package com.booking.bookingbackend.data.validator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.booking.bookingbackend.constant.Gender;
import com.booking.bookingbackend.constant.Method;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = GenderValidator.class)
public @interface GenderValid {
  Gender[] anyOf() default {Gender.Male, Gender.Female, Gender.Other};

  String message() default "must be any of {anyOf}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
