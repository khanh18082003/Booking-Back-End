package com.booking.bookingbackend.data.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.booking.bookingbackend.data.dto.response.OutboundUserResponse;

@FeignClient(name = "outbound-user-client", url = "${outbound.google.user.url}")
public interface OutboundUserClient {

    @GetMapping(value = "/oauth2/v1/userinfo")
    OutboundUserResponse getUserInfo(@RequestParam("alt") String alt, @RequestParam("access_token") String accessToken);
}
