package com.booking.bookingbackend.data.validator;

import com.booking.bookingbackend.constant.Method;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class HttpMethodSubsetValidator implements ConstraintValidator<HttpMethodValidator, String> {

  private List<Method> httpMethods;

  @Override
  public void initialize(HttpMethodValidator constraintAnnotation) {
    httpMethods = Arrays.asList(constraintAnnotation.anyOf());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    Method methodValue;
    try {
      methodValue = Method.valueOf(value);
    } catch (IllegalArgumentException e) {
      return false;
    }
    return httpMethods.contains(methodValue);
  }
}
