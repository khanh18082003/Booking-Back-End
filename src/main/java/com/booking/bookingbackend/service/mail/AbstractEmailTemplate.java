package com.booking.bookingbackend.service.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public abstract class AbstractEmailTemplate {
    protected final JavaMailSender mailSender;
    protected final SpringTemplateEngine templateEngine;
    protected final String from;

    public AbstractEmailTemplate(JavaMailSender mailSender, SpringTemplateEngine templateEngine, String from) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.from = from;
    }

    // Template method - khung xử lý gửi mail
    public final void send() throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        helper.setTo(getRecipient());
        helper.setSubject(getSubject());
        helper.setFrom(from, "Booking.com");

        String content = buildHtmlContent();
        helper.setText(content, true);

        customize(helper); // hook để thêm file đính kèm hoặc thông tin thêm nếu cần

        mailSender.send(message);
    }

    protected abstract String getRecipient();

    protected abstract String getSubject();

    protected abstract String buildHtmlContent();

    // hook method (có thể override hoặc bỏ qua)
    protected void customize(MimeMessageHelper helper) throws MessagingException {}
}
