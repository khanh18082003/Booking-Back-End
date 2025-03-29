package com.booking.bookingbackend.configuration;

import com.booking.bookingbackend.service.InitApplicationService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitApplicationConfig {
  @Bean
  ApplicationRunner runnerApplication(InitApplicationService service) {
    return args -> service.init();
  }
}
