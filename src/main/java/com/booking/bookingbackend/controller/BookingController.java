package com.booking.bookingbackend.controller;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.BookingRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.BookingResponse;
import com.booking.bookingbackend.data.dto.response.Meta;
import com.booking.bookingbackend.data.dto.response.PaginationResponse;
import com.booking.bookingbackend.service.booking.BookingService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort.Order;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_BOOKING)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "BOOKING-CONTROLLER")
public class BookingController {

  BookingService bookingService;

  @PostMapping
  ApiResponse<BookingResponse> save(
      @Valid @RequestBody BookingRequest request
  ) throws MessagingException, UnsupportedEncodingException {
    return ApiResponse.<BookingResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(bookingService.book(request))
        .build();
  }

  @PostMapping("/change-status")
  ApiResponse<BookingResponse> changeStatus(
      @Valid @RequestBody BookingStatus status,
      @Valid @RequestBody UUID id
  ) {
    return ApiResponse.<BookingResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(bookingService.changeStatus(id, status))
        .build();
  }

  @GetMapping("/booking-history")
  ApiResponse<List<BookingResponse>> bookingHistory(
      @RequestParam("userId") UUID userId
  ) {
    return ApiResponse.<List<BookingResponse>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(bookingService.BookingHistory(userId))
        .build();
  }

  @PostMapping("/cancel-booking/{id}")
  ApiResponse<Void> cancelBooking(
      @PathVariable("id") UUID bookingId
  ) {
    bookingService.cancelBooking(bookingId);
    return ApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .build();
  }


  @GetMapping
  ApiResponse<PaginationResponse<BookingResponse>> findAll(
          @RequestParam(defaultValue = "1") int pageNo,
          @RequestParam(defaultValue = "20") int pageSize)
  {
    Order order= new Order(Sort.Direction.ASC, "createdAt");
    Pageable pageable= PageRequest.of(pageNo - 1, pageSize, Sort.by(order));
    Page<BookingResponse> page= bookingService.findAll(pageable);
    PaginationResponse<BookingResponse> result= PaginationResponse.<BookingResponse>builder()
        .meta(Meta.builder()
            .page(pageNo)
            .pageSize(pageSize)
            .pages(page.getTotalPages())
            .total(page.getTotalPages())
            .build())
        .data(page.getContent())
        .build();
    return ApiResponse.<PaginationResponse<BookingResponse>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(result)
        .build();
  }
}
