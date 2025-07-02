package com.booking.bookingbackend.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.booking.bookingbackend.configuration.Translator;
import com.booking.bookingbackend.constant.CommonConstant;
import com.booking.bookingbackend.constant.EndpointConstant;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.RoleRequest;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.booking.bookingbackend.data.dto.response.Meta;
import com.booking.bookingbackend.data.dto.response.PaginationResponse;
import com.booking.bookingbackend.data.dto.response.RoleResponse;
import com.booking.bookingbackend.service.role.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_ROLE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j(topic = "ROLE-CONTROLLER")
public class RoleController {

    RoleService roleService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create Role",
            description = "Create a new Role (`ADMIN` only)",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_CREATED,
                        description = "Role created",
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
								"name": "ADMIN",
								"description": "All permission",
								"created_at": "2025-03-15T05:35:35.467Z",
								"updated_at": "2025-03-15T05:35:35.467Z"
								}
							}
							"""))),
            })
    ApiResponse<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {

        return ApiResponse.<RoleResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.CREATED.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(roleService.save(request))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Role",
            description = "Update an existing Role (`ADMIN` only)",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "202",
                        description = "Role updated successfully",
                        content =
                                @Content(
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
								{
									"code": "M000",
									"status": "202",
									"message": "Success"
								}
							"""))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "Role not found",
                        content = @Content)
            })
    ApiResponse<Void> updateRole(@PathVariable int id, @Valid @RequestBody RoleRequest request) {
        roleService.update(id, request);
        return ApiResponse.<Void>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.ACCEPTED.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Role",
            description = "Get a Role (`ADMIN` only)",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_CREATED,
                        description = "Get Role",
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
								"name": "ADMIN",
								"description": "All permission",
								"created_at": "2025-03-15T05:35:35.467Z",
								"updated_at": "2025-03-15T05:35:35.467Z"
								}
							}
							"""))),
            })
    ApiResponse<RoleResponse> findById(@PathVariable int id) {
        return ApiResponse.<RoleResponse>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(roleService.findById(id))
                .build();
    }

    @GetMapping
    @Operation(
            summary = "Get all Role",
            description = "Get all Role (`ADMIN` only)",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = CommonConstant.MESSAGE_CREATED,
                        description = "Get all Role",
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
								"name": "ADMIN",
								"description": "All permission",
								"created_at": "2025-03-15T05:35:35.467Z",
								"updated_at": "2025-03-15T05:35:35.467Z"
								}
							}
							"""))),
            })
    ApiResponse<PaginationResponse<RoleResponse>> findAll(
            @RequestParam(defaultValue = "1") int pageNo, @RequestParam(defaultValue = "20") int pageSize) {
        Order order = new Order(Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(order));
        Page<RoleResponse> page = roleService.findAll(pageable);
        PaginationResponse<RoleResponse> result = PaginationResponse.<RoleResponse>builder()
                .meta(Meta.builder()
                        .page(page.getNumber())
                        .pageSize(page.getSize())
                        .pages(page.getTotalPages())
                        .total(page.getTotalElements())
                        .build())
                .data(page.getContent())
                .build();
        return ApiResponse.<PaginationResponse<RoleResponse>>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getErrorCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getErrorCode()))
                .data(result)
                .build();
    }
}
