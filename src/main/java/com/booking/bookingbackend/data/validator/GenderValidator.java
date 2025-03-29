package com.booking.bookingbackend.data.validator;

import com.booking.bookingbackend.constant.Gender;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class GenderValidator implements ConstraintValidator<GenderValid, String> {
  private List<Gender> genders;

  @Override
  public void initialize(GenderValid constraintAnnotation) {
    genders = Arrays.asList(constraintAnnotation.anyOf());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    Gender gender;
    try {
      gender = Gender.valueOf(value);
    } catch (IllegalArgumentException e) {
      return false;
    }
    return genders.contains(gender);
  }
}
