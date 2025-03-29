package com.booking.bookingbackend.controller;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.CommonConstant;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.AuthenticationRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.AuthenticationResponse;
import com.booking.bookingbackend.service.authentication.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_AUTH)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
public class AuthenticationController {
  AuthenticationService authenticationService;

  @PostMapping("/login")
  @Operation(
      summary = "Authenticate user",
      description = "Authenticate user when user login",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = CommonConstant.MESSAGE_CREATED,
              description = "Authentication",
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
                                  "access_token": "7SQerx9SoaVXIgymVZCEA3I6kloqfaZt+WJYWPUJ0Qk=",
                                  "refresh_token": "7SQerx9SoaVXIgymVZCEA3I6kloqfaZt+WJYWPUJ0Qk="
                                }
                              }
                              """))),
      }
  )
  ApiResponse<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
    return ApiResponse.<AuthenticationResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(authenticationService.authenticate(request))
        .build();
  }
}
