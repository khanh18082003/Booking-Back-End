package com.booking.bookingbackend.service.notification;

import com.booking.bookingbackend.data.dto.response.BookingResponse;
import com.booking.bookingbackend.util.DateUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;
public class BookingConfirmationEmail extends AbstractEmailTemplate{

    private final BookingResponse bookingResponse;
    private final String supportEmail;


    public BookingConfirmationEmail(JavaMailSender mailSender, SpringTemplateEngine templateEngine, String from, BookingResponse bookingResponse, String supportEmail) {
        super(mailSender, templateEngine, from);
        this.bookingResponse = bookingResponse;
        this.supportEmail = supportEmail;
    }

    @Override
    protected String getRecipient() {
        return bookingResponse.getUserBooking().getEmail();
    }

    @Override
    protected String getSubject() {
        return "Gửi thông tin đặt phòng thành công";
    }

    @Override
    protected String buildHtmlContent() {
        Context context = new Context();
        context.setVariable("booking", bookingResponse);
        context.setVariable("supportEmail", supportEmail);
        context.setVariable("nights", DateUtils.daysBetween(
                bookingResponse.getCheckIn(),
                bookingResponse.getCheckOut()
        ));
        return templateEngine.process("booking-template.html", context);
    }
}
