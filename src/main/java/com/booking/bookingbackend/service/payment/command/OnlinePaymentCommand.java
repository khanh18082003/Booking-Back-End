package com.booking.bookingbackend.service.payment.command;
import com.booking.bookingbackend.constant.PaymentMethod;
import com.booking.bookingbackend.data.dto.request.PaymentRequest;
import com.booking.bookingbackend.data.entity.Payment;

public class OnlinePaymentCommand implements PaymentCommand {
    private static final String ACCOUNT_NUMBER = "0396441431";
    private static final String ACCOUNT_NAME = "NGUYEN THANH TAM";
    @Override
    public void execute(Payment payment, PaymentRequest request) {
        String qrImgSrc = "https://img.vietqr.io/image/970422-" + ACCOUNT_NUMBER
                + "-compact2.png?amount=" + request.amount()
                + "&addInfo=" + payment.getTransactionId()
                + "&accountName=" + ACCOUNT_NAME.replace(" ", "%20");
        payment.setUrlImage(qrImgSrc);
        payment.setStatus(false); // Chờ thanh toán
    }
}
