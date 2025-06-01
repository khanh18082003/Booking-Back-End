package com.booking.bookingbackend.service.notification;

import com.booking.bookingbackend.data.entity.RedisVerificationCode;
import com.booking.bookingbackend.data.repository.VerificationCodeRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

public class VerificationEmail extends AbstractEmailTemplate{
    private final String to;
    private final String name;
    private final String code;
    private final String supportEmail;
    private final VerificationCodeRepository codeRepository;

    public VerificationEmail(JavaMailSender mailSender, SpringTemplateEngine templateEngine, String from,
                             String to, String name, String code,
                             String supportEmail,
                             VerificationCodeRepository codeRepository) {
        super(mailSender, templateEngine, from);
        this.to = to;
        this.name = name;
        this.code = code;
        this.supportEmail = supportEmail;
        this.codeRepository = codeRepository;
    }
    @Override
    protected String getRecipient() {
        return to;
    }

    @Override
    protected String getSubject() {
        return "Xác định email và thông tin đăng nhập";
    }

    @Override
    protected String buildHtmlContent() {
        // store code into redis
        RedisVerificationCode verificationCode = RedisVerificationCode.builder()
                .id(to)
                .code(code)
                .build();
        codeRepository.save(verificationCode);

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("code", code);
        context.setVariable("supportEmail", supportEmail);

        return templateEngine.process("email-template.html", context);
    }
}
