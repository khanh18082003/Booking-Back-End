package com.booking.bookingbackend.controller;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.AccommodationCreationRequest;
import com.booking.bookingbackend.data.dto.request.AccommodationUpdateRequest;
import com.booking.bookingbackend.data.dto.request.AvailableUpdatePriceRequest;
import com.booking.bookingbackend.data.dto.response.AccommodationResponse;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.AvailableResponse;
import com.booking.bookingbackend.data.dto.response.Meta;
import com.booking.bookingbackend.data.dto.response.PaginationResponse;
import com.booking.bookingbackend.service.accommodation.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_ACCOMMODATION)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "ACCOMMODATION-CONTROLLER")
public class AccommodationController {

  AccommodationService accommodationService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Create Property",
      description = "Create a new Property (`ADMIN` only)",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "201",
              description = "Property created",
              content = @Content(
                  examples = @ExampleObject(
                      value = """
                          {
                            "code": "M000",
                            "status": "201",
                            "message": "Success",
                            "data": {
                              "name": "Luxury Villa",
                              "description": "A beautiful villa with sea view",
                              "address": "123 Ocean Drive",
                              "city": "Miami",
                              "country": "USA",
                              "district": "South Beach",
                              "rating": 4.8,
                              "status": true,
                              "latitude": 25.7617,
                              "longitude": -80.1918,
                              "check_in_time": "14:00",
                              "check_out_time": "11:00",
                              "created_at": "2025-03-15T05:35:35.467Z",
                              "updated_at": "2025-03-15T05:35:35.467Z",
                              "host": {
                                "id": "1",
                                "name": "John Doe"
                              },
                              "property_type": {
                                "id": "2",
                                "name": "Villa"
                              },
                              "amenities": [
                                {
                                  "id": "1",
                                  "name": "WiFi"
                                },
                                {
                                  "id": "2",
                                  "name": "Pool"
                                }
                              ]
                            }
                          }
                          """
                  )
              )
          )
      }
  )
  ApiResponse<AccommodationResponse> create(
      @RequestPart("request") @Valid AccommodationCreationRequest request,
      @RequestPart(value = "extra_image", required = false) MultipartFile[] images) {
    List<String> imageUrls = new ArrayList<>();
    if (images != null) {
      for (MultipartFile i : images) {
        if (!i.isEmpty()) {
          try {
            String fileName = UUID.randomUUID() + "_" + i.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/accommodation/", fileName);
            Files.createDirectories(uploadPath.getParent());
            Files.write(uploadPath, i.getBytes());
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build()
                .toUriString();
            String imageUrl = baseUrl + "/uploads/accommodation/" + fileName;
            imageUrls.add(imageUrl); // URL tương đối
          } catch (IOException e) {
            log.error("Lỗi khi lưu ảnh", e);
            throw new RuntimeException("Lỗi khi upload ảnh", e);
          }
        }
      }
    }
    AccommodationCreationRequest result = request.withExtraImages(imageUrls);

    return ApiResponse.<AccommodationResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(accommodationService.save(result))
        .build();
  }

  @PutMapping("/{id}")
  ApiResponse<AccommodationResponse> update(@PathVariable UUID id,
      @Valid @RequestBody AccommodationUpdateRequest request) {
    return ApiResponse.<AccommodationResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(accommodationService.update(id, request))
        .build();
  }

  @GetMapping
  ApiResponse<PaginationResponse<AccommodationResponse>> findAll(
      @RequestParam(defaultValue = "1") int pageNo,
      @RequestParam(defaultValue = "20") int pageSize
  ) {
    Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name");
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(order));
    Page<AccommodationResponse> page = accommodationService.findAll(pageable);
    PaginationResponse<AccommodationResponse> result = PaginationResponse.<AccommodationResponse>builder()
        .meta(Meta.builder()
            .page(pageNo)
            .pageSize(pageSize)
            .pages(page.getTotalPages())
            .total(page.getTotalPages())
            .build())
        .data(page.getContent())
        .build();
    return ApiResponse.<PaginationResponse<AccommodationResponse>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(result)
        .build();
  }

  @GetMapping("/{id}")
  ApiResponse<AccommodationResponse> findById(@PathVariable UUID id) {
    return ApiResponse.<AccommodationResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(accommodationService.findById(id))
        .build();
  }

  @GetMapping("/{id}/available")
  ApiResponse<List<AvailableResponse>> findAllByAccommodationId(@PathVariable UUID id) {
    List<AvailableResponse> availableResponses = accommodationService.findAllByAccommodationId(id);
    return ApiResponse.<List<AvailableResponse>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(availableResponses)
        .build();
  }

  @PutMapping("/available")
  ApiResponse<List<AvailableResponse>> updatePriceAvailableByDate(
      @Valid @RequestBody AvailableUpdatePriceRequest request
  ) {
    List<AvailableResponse> updatedResponses = accommodationService.updatePriceAvailableByDate(request);
    return ApiResponse.<List<AvailableResponse>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(updatedResponses)
        .build();
  }
}

