package com.booking.bookingbackend.service.googlemap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  public JsonNode getLocation(String q, String limit) {
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
      return response.body() != null ? new ObjectMapper().readTree(response.body()) : null;

    } catch (Exception e) {
      log.error("Error while calling Google Maps API: {}", e.getMessage());
      return null;
    }
  }

  public double[] getLatLng(String location) {
    try {
      String input = URLEncoder.encode(location, StandardCharsets.UTF_8);

      String url = String.format(
          "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
          input,
          GOOGLE_MAPS_API_KEY
      );

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .GET()
          .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      var responseBody = response.body();

      if (responseBody != null) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);
        String status = rootNode.path("status").asText();
        if (status.equals("OK")) {
          JsonNode locationNode = rootNode.path("results").get(0).path("geometry").path("location");
          double lat = locationNode.path("lat").asDouble();
          double lng = locationNode.path("lng").asDouble();
          log.info("Latitude: {}, Longitude: {}", lat, lng);
          return new double[]{lat, lng};
        } else if (status.equals("ZERO_RESULTS")) {
          log.error("No results found for the given location: {}", location);
          return null;
        } else {
          log.error("Error from Google Maps API: {}", status);
          return null;
        }
      } else {
        log.error("Response body is null");
        return null;
      }
    } catch (Exception e) {
      log.error("Error while calling Google Maps API: {}", e.getMessage());
      return null;
    }
  }

}
