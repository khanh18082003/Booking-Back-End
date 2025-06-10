package com.booking.bookingbackend.controller.host;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.RevenueResponse;
import com.booking.bookingbackend.data.entity.CustomUserDetails;
import com.booking.bookingbackend.service.user.UserService;
import com.booking.bookingbackend.util.SecurityUtils;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_HOST_DASHBOARD)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "Dashboard-HOST-CONTROLLER")
public class DashboardController {

  UserService userService;

  @GetMapping
  ApiResponse<RevenueResponse> getRevenueByHostId() {
    CustomUserDetails userDetails = SecurityUtils.getCurrentUser();
    UUID hostId = userDetails.user().getId();
    RevenueResponse revenueResponses = userService.getRevenueByHostId(hostId);
    return ApiResponse.<RevenueResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(revenueResponses)
        .build();
  }

  @GetMapping("/revenue")
  ApiResponse<RevenueResponse> getRevenueByHostIdWithMonthAndYear(
      @RequestParam(value = "month") int month,
      @RequestParam("year") int year
  ) {
    CustomUserDetails userDetails = SecurityUtils.getCurrentUser();
    UUID hostId = userDetails.user().getId();
    RevenueResponse revenueResponses = userService.getRevenueByHostIdWithMonthAndYear(
        hostId,
        month,
        year);
    return ApiResponse.<RevenueResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(revenueResponses)
        .build();
  }

  @GetMapping("/revenue-by-year")
  ApiResponse<List<RevenueResponse>> getRevenueByHostIdWithMonthAndYear(
      @RequestParam("year") int year
  ) {
    CustomUserDetails userDetails = SecurityUtils.getCurrentUser();
    UUID hostId = userDetails.user().getId();
    List<RevenueResponse> revenueResponses = userService.getRevenueByHostIdWithYear(
        hostId,
        year);
    return ApiResponse.<List<RevenueResponse>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(revenueResponses)
        .build();
  }

}
