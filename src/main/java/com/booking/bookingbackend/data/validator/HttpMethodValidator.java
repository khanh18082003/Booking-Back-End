package com.booking.bookingbackend.data.validator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
@Constraint(validatedBy = HttpMethodSubsetValidator.class)
public @interface HttpMethodValidator {

  Method[] anyOf() default {Method.GET, Method.POST, Method.DELETE, Method.PUT, Method.PATCH};

  String message() default "must be any of {anyOf}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
