package com.booking.bookingbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BookingBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingBackEndApplication.class, args);
    }
}
