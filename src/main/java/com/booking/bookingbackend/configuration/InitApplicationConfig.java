package com.booking.bookingbackend.configuration;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.booking.bookingbackend.service.InitApplicationService;

@Configuration
public class InitApplicationConfig {
    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            name = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner runnerApplication(InitApplicationService service) {
        return args -> service.init();
    }
}
