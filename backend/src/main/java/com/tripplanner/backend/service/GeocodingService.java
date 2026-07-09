package com.tripplanner.backend.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tripplanner.backend.dto.geocoding.GeocodingResponse;
import com.tripplanner.backend.exception.ValidationException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class GeocodingService {

    private static final long MIN_REQUEST_INTERVAL_MILLIS = 1_000;

    private final RestClient restClient;
    private final String userAgent;
    private final Map<String, GeocodingResponse> cache = new ConcurrentHashMap<>();
    private final Object rateLimitLock = new Object();
    private long lastRequestAt;

    public GeocodingService(
            RestClient.Builder restClientBuilder,
            @Value("${app.geocoding.base-url:https://nominatim.openstreetmap.org}") String baseUrl,
            @Value("${app.geocoding.user-agent:TripPlanner-TBank/1.0}") String userAgent) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.userAgent = userAgent;
    }

    public GeocodingResponse geocode(String query) {
        if (query == null || query.isBlank()) {
            throw new ValidationException("Address is required for geocoding");
        }
        String normalizedQuery = query.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
        GeocodingResponse cached = cache.get(normalizedQuery);
        if (cached != null) {
            return cached;
        }

        NominatimResult[] results;
        try {
            awaitRateLimit();
            results = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", query.trim())
                            .queryParam("format", "jsonv2")
                            .queryParam("limit", 1)
                            .build())
                    .header(HttpHeaders.USER_AGENT, userAgent)
                    .retrieve()
                    .body(NominatimResult[].class);
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Geocoding service is unavailable", exception);
        }

        if (results == null || results.length == 0) {
            throw new ValidationException("Address could not be geocoded");
        }
        try {
            GeocodingResponse response = new GeocodingResponse(
                    results[0].displayName(),
                    Double.valueOf(results[0].latitude()),
                    Double.valueOf(results[0].longitude()));
            cache.put(normalizedQuery, response);
            return response;
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Geocoding service returned invalid coordinates", exception);
        }
    }

    private void awaitRateLimit() {
        synchronized (rateLimitLock) {
            long waitMillis = MIN_REQUEST_INTERVAL_MILLIS - (System.currentTimeMillis() - lastRequestAt);
            if (waitMillis > 0) {
                try {
                    Thread.sleep(waitMillis);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new ResponseStatusException(
                            HttpStatus.SERVICE_UNAVAILABLE, "Geocoding request was interrupted", exception);
                }
            }
            lastRequestAt = System.currentTimeMillis();
        }
    }

    private record NominatimResult(
            @JsonProperty("display_name") String displayName,
            @JsonProperty("lat") String latitude,
            @JsonProperty("lon") String longitude
    ) {
    }
}
