package com.booking.bookingbackend.data.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import com.booking.bookingbackend.data.dto.request.ExchangeTokenRequest;
import com.booking.bookingbackend.data.dto.response.ExchangeTokenResponse;

import feign.QueryMap;

@FeignClient(name = "outbound-identity-client", url = "${outbound.google.identity.url}")
public interface OutboundIdentityClient {

    @PostMapping(value = "/token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest request);
}
