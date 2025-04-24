package com.booking.bookingbackend.service.payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

public class PaymentOnlineServiceImpl implements PaymentOnlineService {

    private static final String API_URL = "https://my.sepay.vn/userapi/transactions/list?";
    private static final String AUTHORIZATION_TOKEN = "Bearer CJPN4H68I7XSWHVPGNJ5CYU6H3UVZR24WS5NDT97EV0CXBFUXPFTLGACOM9IIQ1A";

    @Override
    public boolean processPayment(UUID id, BigDecimal amount, String transactionId) {
        long startTime = System.currentTimeMillis();
        long timeout = 5 * 60 * 1000; // 5 phút

        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                if (checkPaymentStatus(amount.intValue(), transactionId)) {
                    System.out.println("Thanh toán thành công cho giao dịch: " + transactionId);
                    return true;
                }
                Thread.sleep(1000); // kiểm tra mỗi 5 giây
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Thanh toán thất bại hoặc hết thời gian cho giao dịch: " + transactionId);
        return false;
    }

    private boolean checkPaymentStatus(int expectedAmount, String expectedTransactionId) throws IOException {
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

            if (transactionContent.equals(expectedTransactionId) && amountIn == expectedAmount) {
                return true;
            }
        }
        return false;
    }
}
