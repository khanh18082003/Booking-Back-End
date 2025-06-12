package com.booking.bookingbackend.service.payment;

import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.PaymentRequest;
import com.booking.bookingbackend.data.dto.response.PaymentResponse;
import com.booking.bookingbackend.data.entity.Booking;
import com.booking.bookingbackend.data.entity.Payment;
import com.booking.bookingbackend.data.mapper.PaymentMapper;
import com.booking.bookingbackend.data.repository.BookingRepository;
import com.booking.bookingbackend.data.repository.PaymentRepository;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.service.payment.command.PaymentCommand;
import com.booking.bookingbackend.service.payment.command.PaymentCommandFactory;
import jakarta.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "PAYMENT-SERVICE")
public class PaymentServiceImpl implements PaymentService {

  PaymentRepository repository;
  BookingRepository bookingRepository;
  PaymentMapper mapper;
  static String API_URL = "https://my.sepay.vn/userapi/transactions/list?";
  static String AUTHORIZATION_TOKEN = "Bearer CJPN4H68I7XSWHVPGNJ5CYU6H3UVZR24WS5NDT97EV0CXBFUXPFTLGACOM9IIQ1A";

  @Override
  public PaymentResponse save(PaymentRequest request) {
    Payment payment = mapper.toEntity(request);
    payment.setBooking(bookingRepository.findById(request.bookingId())
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            Booking.class.getSimpleName())));

    String transactionId = createTransactionId();
    payment.setTransactionId(transactionId);

    // Command pattern to handle different payment methods
    PaymentCommand command = PaymentCommandFactory.getCommand(request.paymentMethod());
    command.execute(payment, request);

    Payment savedPayment = repository.save(payment);
    return mapper.toDtoResponse(savedPayment);
  }

  private String createTransactionId() {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder transactionId = new StringBuilder();
    for (int i = 0; i < 7; i++) {
      int randomIndex = (int) (Math.random() * chars.length());
      transactionId.append(chars.charAt(randomIndex));
    }
    return transactionId.toString();
  }

  @Override
  public PaymentResponse getPayment(UUID id) {
    Payment payment = repository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName()));
    return mapper.toDtoResponse(payment);
  }

  public void changStatus(UUID id, boolean status) {
    Payment payment = repository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName()));
    payment.setStatus(status);
    Payment updatedPayment = repository.save(payment);
  }

  public Boolean checkPaymentOnlineStatus(UUID id, int expectedAmount, String expectedTransactionId)
      throws IOException {
    LocalDate localDate = LocalDate.now();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedDate = localDate.format(formatter);
    String apiUrl = API_URL + "transaction_date_min=" + formattedDate;

    URL url = new URL(apiUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Authorization", AUTHORIZATION_TOKEN);
    connection.setRequestProperty("Content-Type", "application/json");

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String inputLine;
    StringBuilder response = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    JSONObject jsonObject = new JSONObject(response.toString());
    JSONArray transactions = jsonObject.getJSONArray("transactions");

    for (int i = 0; i < transactions.length(); i++) {
      JSONObject transaction = transactions.getJSONObject(i);
      String transactionContent = transaction.getString("transaction_content").trim();
      int amountIn = (int) Float.parseFloat(transaction.getString("amount_in"));
      System.out.println("Transaction ID: " + transactionContent);
      System.out.println("Amount: " + amountIn);
      if (transactionContent.contains(expectedTransactionId) && amountIn == expectedAmount) {
        Payment payment = repository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
                getEntityClass().getSimpleName()));
        payment.setStatus(true);
        payment.setPaidAt(new Timestamp(System.currentTimeMillis()));
        repository.save(payment);

        return true;
      }
    }
    return false;
  }

  @Transactional
  @Override
  public void payComplete(UUID id) {
    Payment payment = repository.findByBookingId(id)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName())
        );
    payment.setStatus(true);
    payment.setPaidAt(new Timestamp(System.currentTimeMillis()));

    repository.save(payment);
  }
}