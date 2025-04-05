package com.booking.bookingbackend.service.mail;

import com.booking.bookingbackend.data.entity.RedisVerificationCode;
import com.booking.bookingbackend.data.repository.VerificationCodeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
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

  public String sendMail(
      String recipients,
      String subject,
      String content,
      MultipartFile[] files
  ) throws MessagingException, UnsupportedEncodingException {
    log.info("Sending...");
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(
        message,
        MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
        StandardCharsets.UTF_8.name()
    );
    helper.setFrom(from, "Booking.com");

    if (recipients.contains(",")) {
      helper.setTo(InternetAddress.parse(recipients));
    } else {
      helper.setTo(recipients);
    }

    if (files != null) {
      for (MultipartFile file : files) {
        helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
      }
    }

    helper.setSubject(subject);
    helper.setText(content, true);

    mailSender.send(message);

    log.info("Email has been send successfully: recipients={}", recipients);

    return "sent";
  }

  @Transactional
  public void sendVerificationEmail(String to, String userId, String name, String code)
      throws MessagingException, UnsupportedEncodingException {
    // Tạo MimeMessage
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

    // Thiết lập thông tin email
    helper.setTo(to);
    helper.setSubject("Xác định email và thông tin đăng nhập");
    helper.setFrom(from);

    // Tạo context để truyền dữ liệu vào template
    Context context = new Context();
    context.setVariable("name", name);
    context.setVariable("code", code);
    context.setVariable("supportEmail", supportEmail);

    // Render template HTML
    String htmlContent = springTemplateEngine.process("email-template.html", context);
    helper.setText(htmlContent, true);

    // store code into redis
    log.info("Store key {} with value {}", userId, code);
    RedisVerificationCode verificationCode = RedisVerificationCode.builder()
        .id(userId)
        .code(code)
        .build();
    verificationCodeRepository.save(verificationCode);

    // Gửi email
    mailSender.send(message);
    log.info("Email sent");
  }

}
