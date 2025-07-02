package com.booking.bookingbackend.controller;

import java.io.UnsupportedEncodingException;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.ResendVerificationRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.ProfileResponse;
import com.booking.bookingbackend.data.dto.response.UserResponse;
import com.booking.bookingbackend.service.notification.MailService;
import com.booking.bookingbackend.service.notification.VerificationCodeService;
import com.booking.bookingbackend.service.profile.ProfileService;
import com.booking.bookingbackend.service.user.UserService;
import com.booking.bookingbackend.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_MAIL)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "MAIl-CONTROLLER")
public class MailController {

    MailService mailService;
    UserService userService;
    ProfileService profileService;
    VerificationCodeService verificationCodeService;

    @PostMapping
    @Operation(
            summary = "Resend Verification Code",
            description = "Resend a new verification code to the user's email address",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Verification code resent successfully",
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
						""")))
            })
    ApiResponse<Void> resendVerificationCode(@Valid @RequestBody ResendVerificationRequest request)
            throws MessagingException, UnsupportedEncodingException {
        UserResponse user = userService.findByEmail(request.email().strip());
        ProfileResponse userProfile = profileService.findByUserId(user.getId());

        String firstName = userProfile.getFirstName();
        String lastName = userProfile.getLastName();
        String name = firstName != null && lastName != null ? firstName + " " + lastName : null;

        String newVerifyCode = SecurityUtils.generateVerificationCode();
        verificationCodeService.saveCode(newVerifyCode, user.getEmail());

        mailService.sendVerificationEmail(user.getEmail(), name, newVerifyCode);

        return ApiResponse.<Void>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .build();
    }
}
