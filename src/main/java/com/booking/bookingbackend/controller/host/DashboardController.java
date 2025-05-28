package com.booking.bookingbackend.controller.host;

import com.booking.bookingbackend.constant.EndpointConstant;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_HOST_DASHBOARD)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "Dashboard-HOST-CONTROLLER")
public class DashboardController {

}
