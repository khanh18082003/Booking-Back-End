package com.booking.bookingbackend.controller;

import java.io.UnsupportedEncodingException;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.CommonConstant;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.AuthenticationRequest;
import com.booking.bookingbackend.data.dto.request.CheckExistEmailRequest;
import com.booking.bookingbackend.data.dto.request.LogoutRequest;
import com.booking.bookingbackend.data.dto.request.OutboundAuthenticationAppRequest;
import com.booking.bookingbackend.data.dto.request.RefreshTokenRequest;
import com.booking.bookingbackend.data.dto.request.VerificationEmailRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.AuthenticationResponse;
import com.booking.bookingbackend.service.authentication.AuthenticationService;
import com.booking.bookingbackend.service.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_AUTH)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
public class AuthenticationController {

    AuthenticationService authenticationService;
    UserService userService;

    public AuthenticationController(
            @Qualifier("authenticationServiceImpl") AuthenticationService authenticationService,
            UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticate user when user login",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_OK,
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
            })
    ApiResponse<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request, HttpServletResponse response)
            throws MessagingException, UnsupportedEncodingException {

        return ApiResponse.<AuthenticationResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(authenticationService.authenticate(request, response))
                .build();
    }

    @PostMapping("/outbound/authentication")
    ApiResponse<AuthenticationResponse> outboundAuthentication(
            @RequestParam("code") String code, HttpServletResponse res) {
        return ApiResponse.<AuthenticationResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(authenticationService.outboundAuthenticate(code, res))
                .build();
    }

    @PostMapping("/outbound/authentication-app")
    ApiResponse<AuthenticationResponse> outboundAuthenticationApp(
            @RequestBody OutboundAuthenticationAppRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(authenticationService.outboundAuthenticateApp(request))
                .build();
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Log out account",
            description = "Log out",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_OK,
                        description = "Log out",
                        content =
                                @Content(
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
							{
								"code": "M000",
								"status": "200",
								"message": "Success"
							}
							"""))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_UNAUTHORIZED,
                        description = "Unauthorized access",
                        content =
                                @Content(
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
							{
								"code": "E001",
								"status": "401",
								"message": "Unauthorized"
							}
							""")))
            })
    ApiResponse<Void> logout(
            @Valid @RequestBody LogoutRequest logoutRequest, HttpServletRequest req, HttpServletResponse res) {
        try {
            authenticationService.logout(logoutRequest, req, res);
            log.info("User logged out successfully");
            return ApiResponse.<Void>builder()
                    .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                    .status(HttpStatus.OK.value())
                    .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                    .build();
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            return ApiResponse.<Void>builder()
                    .code(ErrorCode.MESSAGE_UN_AUTHENTICATION.getErrorCode())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message(Translator.toLocale(ErrorCode.MESSAGE_UN_AUTHENTICATION.getErrorCode()))
                    .build();
        }
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Generate a new access token",
            description = "Generates a new access token using the refresh token provided by the user",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_OK,
                        description = "Successfully generated the new access token",
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
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_UNAUTHORIZED,
                        description = "Invalid or expired refresh token",
                        content =
                                @Content(
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
						{
							"code": "M0401",
							"status": "401",
							"message": "Unauthorized"
						}
						""")))
            })
    ApiResponse<AuthenticationResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest, HttpServletRequest req) {

        return ApiResponse.<AuthenticationResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(authenticationService.refreshToken(refreshTokenRequest, req))
                .build();
    }

    @PostMapping("/verify-email")
    @Operation(
            summary = "Verify Email",
            description = "Verifies the email using the provided verification code",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_OK,
                        description = "Email verified successfully",
                        content =
                                @Content(
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
							{
								"code": "M000",
								"status": "200",
								"message": "Success"
							}
							"""))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_BAD_REQUEST,
                        description = "Verification code is invalid or expired",
                        content =
                                @Content(
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
							{
								"code": "M0401",
								"status": "401",
								"message": "Unauthorized"
							}
							""")))
            })
    ApiResponse<Void> verifyMail(@Valid @RequestBody VerificationEmailRequest request) {
        log.info("Verifying email: {}", request.code());
        authenticationService.verifyTokenEmail(request);

        userService.activeUser(request.email());

        return ApiResponse.<Void>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .build();
    }

    @PostMapping("/check-exist-email")
    @Operation(
            summary = "Check if email exists",
            description = "Check if the email already exists in the system",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_OK,
                        description = "Email exists",
                        content =
                                @Content(
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
							{
								"code": "M000",
								"status": "200",
								"message": "Success"
							}
							"""))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_NOT_FOUND,
                        description = "Email does not exist",
                        content =
                                @Content(
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
							{
								"code": "M0404",
								"status": "404",
								"message": "Not Found"
							}
							""")))
            })
    ApiResponse<Void> checkExistEmail(@Valid @RequestBody CheckExistEmailRequest request) {
        String email = request.email();
        userService.findByEmail(email);
        return ApiResponse.<Void>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .build();
    }
}
