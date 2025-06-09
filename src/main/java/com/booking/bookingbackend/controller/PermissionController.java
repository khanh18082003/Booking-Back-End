package com.booking.bookingbackend.controller;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.CommonConstant;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.PermissionRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.Meta;
import com.booking.bookingbackend.data.dto.response.PaginationResponse;
import com.booking.bookingbackend.data.dto.response.PermissionResponse;
import com.booking.bookingbackend.service.permission.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_PERMISSION)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "PERMISSION-CONTROLLER")
public class PermissionController {

  PermissionService permissionService;

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Create Permission",
      description = "Create a new permission (`ADMIN` only)",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = CommonConstant.MESSAGE_CREATED,
              description = "Permission created",
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
                                  "method": "GET",
                                  "url": "string",
                                  "description": "string",
                                  "created_at": "2025-03-15T05:35:35.467Z",
                                  "updated_at": "2025-03-15T05:35:35.467Z"
                                }
                              }
                              """))),
      }
  )
  @PreAuthorize(value = "hasRole('ADMIN') and hasAuthority('POST/permissions')")
  ApiResponse<PermissionResponse> createPermission(@Valid @RequestBody PermissionRequest request) {

    return ApiResponse.<PermissionResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.CREATED.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(permissionService.save(request))
        .build();
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Update Permission",
      description = "Update a exist permission (`ADMIN` only)",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = CommonConstant.MESSAGE_OK,
              description = "Permission updated",
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
      }
  )
  @PreAuthorize(value = "hasRole('ADMIN') and hasAuthority('PUT/permissions')")
  ApiResponse<PermissionResponse> updatePermission(@PathVariable int id,
      @Valid @RequestBody PermissionRequest request) {
    permissionService.update(id, request);
    return ApiResponse.<PermissionResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .build();
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get Permission By ID",
      description = "Get a exist permission by id (`ADMIN` only)",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = CommonConstant.MESSAGE_OK,
              description = "Get Permission",
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
                                  "id": 1073741824,
                                  "method": "GET",
                                  "url": "string",
                                  "description": "string",
                                  "created_at": "2025-03-15T05:35:35.467Z",
                                  "updated_at": "2025-03-15T05:35:35.467Z"
                                }
                              }
                              """))),
      }
  )
  @PreAuthorize(value = "hasRole('ADMIN') and hasAuthority('GET/permissions/id')")
  ApiResponse<PermissionResponse> getById(@PathVariable int id) {
    return ApiResponse.<PermissionResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(permissionService.findById(id))
        .build();
  }

  @GetMapping
  @Operation(
      summary = "Get all Permission",
      description = "Get all Permission (`ADMIN` only)",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = CommonConstant.MESSAGE_CREATED,
              description = "Get all Permission",
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
                                  "page_no": "1",
                                  "page_size": "20",
                                  "pages": "10",
                                  "total": "100",
                                  "id": 1073741824,
                                  "method": "GET",
                                  "url": "string",
                                  "description": "string",
                                  "created_at": "2025-03-15T05:35:35.467Z",
                                  "updated_at": "2025-03-15T05:35:35.467Z"
                                }
                              }
                              """))),
      }
  )
  @PreAuthorize(value = "hasRole('ADMIN') and hasAuthority('GET/permissions')")
  ApiResponse<PaginationResponse<PermissionResponse>> findAll(
      @RequestParam(defaultValue = "1") int pageNo,
      @RequestParam(defaultValue = "20") int pageSize
  ) {
    Order order = new Order(Direction.ASC, "createdAt");
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(order));
    Page<PermissionResponse> page = permissionService.findAll(pageable);
    PaginationResponse<PermissionResponse> result = PaginationResponse.<PermissionResponse>builder()
        .meta(Meta.builder()
            .page(page.getNumber())
            .pageSize(page.getSize())
            .pages(page.getTotalPages())
            .total(page.getTotalElements())
            .build())
        .data(page.getContent())
        .build();
    return ApiResponse.<PaginationResponse<PermissionResponse>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
        .data(result)
        .build();
  }
}
