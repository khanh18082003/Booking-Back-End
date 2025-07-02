package com.booking.bookingbackend.controller;

import java.io.IOException;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.CommonConstant;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.ProfileUpdateRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.ProfileResponse;
import com.booking.bookingbackend.service.profile.ProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_PROFILE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "PROFILE-CONTROLLER")
public class ProfileController {

    ProfileService profileService;

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Profile",
            description = "Update profile through id",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_OK,
                        description = "Profile update",
                        content =
                                @Content(
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
							{
								"code": "M000",
								"status": "202",
								"message": "Success",
							}
							"""))),
            })
    ApiResponse<Void> updateProfile(@PathVariable UUID id, @Valid @RequestBody ProfileUpdateRequest request) {
        profileService.update(id, request);

        return ApiResponse.<Void>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.ACCEPTED.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .build();
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "Update Profile",
            description = "Update profile through id",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_OK,
                        description = "Profile update",
                        content =
                                @Content(
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
							{
								"code": "M000",
								"status": "202",
								"message": "Success",
							}
							"""))),
            })
    ApiResponse<Void> updateMyProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        profileService.updateMyProfile(request);

        return ApiResponse.<Void>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.ACCEPTED.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Update Profile",
            description = "Update profile through id",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_OK,
                        description = "Profile update",
                        content =
                                @Content(
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
							{
								"code": "M000",
								"status": "202",
								"message": "Success",
								"data": {
								"id": 1073741824,
								"first_name": "A",
								"last_name": "Nguyen Van",
								"avatar": "https://....",
								"country_code": "84",
								"phone_number":  "+84378277559",
								"dob": "2003-08-18",
								"gender": "MALE",
								"address": "74, Quang Trung..."
								}
							}
							"""))),
            })
    ApiResponse<ProfileResponse> findById(@PathVariable UUID id) {
        return ApiResponse.<ProfileResponse>builder().build();
    }

    @PatchMapping("/avatar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    ApiResponse<ProfileResponse> updateAvatar(@RequestPart("avatar") MultipartFile avatarFile) throws IOException {

        return ApiResponse.<ProfileResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.ACCEPTED.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(profileService.updateAvatar(avatarFile))
                .build();
    }

    @DeleteMapping("/avatar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    ApiResponse<Void> deleteAvatar() {
        profileService.deleteAvatar();
        return ApiResponse.<Void>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.ACCEPTED.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .build();
    }
}
