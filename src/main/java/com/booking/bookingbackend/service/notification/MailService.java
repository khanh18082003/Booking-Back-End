package com.booking.bookingbackend.service.notification;

import com.booking.bookingbackend.data.dto.response.BookingResponse;
import com.booking.bookingbackend.data.repository.VerificationCodeRepository;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class MailService {

  JavaMailSender mailSender;
  SpringTemplateEngine springTemplateEngine;
  VerificationCodeRepository verificationCodeRepository;

  @Value("${spring.mail.from}")
  @NonFinal
  String from;

  @Value("${admin-user.email}")
  @NonFinal
  String supportEmail;

  public void sendMailBookingConfirmation(BookingResponse response)
      throws MessagingException, UnsupportedEncodingException {
    new BookingConfirmationEmail(mailSender, springTemplateEngine, from, response,
        supportEmail).send();
  }


  public void sendVerificationEmail(String to, String name, String code)
      throws MessagingException, UnsupportedEncodingException {
    new VerificationEmail(mailSender, springTemplateEngine, from, to, name, code, supportEmail,
        verificationCodeRepository)
        .send();
  }
}
