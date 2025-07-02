package com.booking.bookingbackend.configuration;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.MESSAGE_UN_AUTHENTICATION;

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                .timeStamp(LocalDateTime.now())
                .code(errorCode.getErrorCode())
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().getReasonPhrase())
                .path(request.getRequestURI())
                .message(Translator.toLocale(ErrorCode.MESSAGE_UN_AUTHORIZATION.getErrorCode()))
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.flushBuffer();
    }
}
