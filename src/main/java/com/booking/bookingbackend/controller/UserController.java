package com.booking.bookingbackend.controller;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.CommonConstant;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.ForgotPasswordRequest;
import com.booking.bookingbackend.data.dto.request.ResetPasswordRequest;
import com.booking.bookingbackend.data.dto.request.UserCreationRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.ProfileResponse;
import com.booking.bookingbackend.data.dto.response.UserProfileDto;
import com.booking.bookingbackend.data.dto.response.UserResponse;
import com.booking.bookingbackend.service.mail.MailService;
import com.booking.bookingbackend.service.profile.ProfileService;
import com.booking.bookingbackend.service.user.UserService;
import com.booking.bookingbackend.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.io.UnsupportedEncodingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_USER)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
public class UserController {

  UserService userService;
  MailService mailService;
  ProfileService profileService;


  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Create User",
      description = "Create a new User",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = CommonConstant.MESSAGE_CREATED,
              description = "User created",
              content =
              @Content(
                  examples =
                  @ExampleObject(
                      value =
                          """
                              {
                                "code": "M000",
                                "status": "201",
                                "message": "Success",
                                "data": {
                                  "id": 1073741824,
                                  "email": "host@gmail.com",
                                  "is_active": "true",
                                  "created_at": "2025-03-15T05:35:35.467Z",
                                  "updated_at": "2025-03-15T05:35:35.467Z",
                                  "roles": ["USER"]
                                }
                              }
                              """))),
      }
  )
  ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreationRequest request)
      throws MessagingException, UnsupportedEncodingException {
    UserResponse userResponse = userService.save(request);
    ProfileResponse userProfile = profileService.findByUserId(userResponse.getId());

    String firstName = userProfile.getFirstName();
    String lastName = userProfile.getLastName();
    String name = firstName != null && lastName != null ? firstName + " " + lastName : null;

    mailService.sendVerificationEmail(
        userResponse.getEmail(),
        name,
        SecurityUtil.generateVerificationCode()
    );

    return ApiResponse.<UserResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.CREATED.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(userResponse)
        .build();
  }

  @GetMapping("/my-profile")
  @Operation(
      summary = "Get My Profile",
      description = "Retrieve the profile of the currently authenticated user",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = CommonConstant.MESSAGE_SUCCESS,
              description = "User profile retrieved successfully",
              content =
              @Content(
                  examples =
                  @ExampleObject(
                      value =
                          """
                              {
                                "code": "M000",
                                "status": "200",
                                "message": "Success",
                                "data": {
                                  "id": "550e8400-e29b-41d4-a716-446655440000",
                                  "email": "host@gmail.com",
                                  "isActive": true,
                                  "profileId": "550e8400-e29b-41d4-a716-446655440001",
                                  "avatar": "https://example.com/avatar.jpg",
                                  "phone": "+1234567890",
                                  "dob": "1990-01-01",
                                  "gender": "MALE",
                                  "address": "123 Main St, City, Country",
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "countryCode": "+84"
                                }
                              }
                              """))),
      }
  )
  ApiResponse<UserProfileDto> getMyProfile() {

    return ApiResponse.<UserProfileDto>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(userService.getMyProfile())
        .build();
  }
  @PostMapping("/forgot-password")
  ApiResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request){
    String code = SecurityUtil.generateVerificationCode();
    try {
      mailService.sendVerificationEmail(
          request.email().trim(),
          null,
          code
      );
    } catch (MessagingException | UnsupportedEncodingException e) {
      log.error("Error sending verification email", e);
      return ApiResponse.<Void>builder()
          .code(ErrorCode.MESSAGE_INTERNAL_SERVER_ERROR.getErrorCode())
          .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
          .message(Translator.toLocale(ErrorCode.MESSAGE_INTERNAL_SERVER_ERROR.getErrorCode()))
          .build();
    }
    return ApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .build();
  }
  @PostMapping("/reset-password")
  ApiResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request){

    userService.changePassword(request);
    return ApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .build();
  }
}
