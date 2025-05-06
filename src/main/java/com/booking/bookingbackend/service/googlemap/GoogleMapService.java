package com.booking.bookingbackend.service.googlemap;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "GOOGLE-MAP-SERVICE")
public class GoogleMapService {

  @NonFinal
  @Value("${google.map.api-key}")
  String GOOGLE_MAPS_API_KEY;

  public String getLocation(String q, String limit) {
    try {
      String input = URLEncoder.encode(q, StandardCharsets.UTF_8);

      String url = String.format(
          "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&types=%s&language=vi&key=%s&limit=%s&components=country:vn",
          input,
          "lodging%7Cpolitical",
          GOOGLE_MAPS_API_KEY,
          limit
      );

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .GET()
          .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return response.body();

    } catch (Exception e) {
      log.error("Error while calling Google Maps API: {}", e.getMessage());
      return null;
    }
  }

}
