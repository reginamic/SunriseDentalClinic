package com.sunrisedental.web.client;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiClient {

    private static final String API_BASE_URL =
            "http://localhost:8081/sunrise-dental-api";

    private final HttpClient httpClient;

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    public String get(String endpoint)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildUri(endpoint))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        validateResponse(response);

        return response.body();
    }

    public String post(
            String endpoint,
            Map<String, String> formData)
            throws IOException, InterruptedException {

        String encodedBody =
                encodeFormData(formData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildUri(endpoint))
                .header(
                        "Content-Type",
                        "application/x-www-form-urlencoded"
                )
                .header(
                        "Accept",
                        "application/json"
                )
                .POST(
                        HttpRequest.BodyPublishers.ofString(
                                encodedBody
                        )
                )
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        validateResponse(response);

        return response.body();
    }

    private URI buildUri(String endpoint) {

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalArgumentException(
                    "API endpoint is required."
            );
        }

        String normalizedEndpoint =
                endpoint.startsWith("/")
                        ? endpoint
                        : "/" + endpoint;

        return URI.create(
                API_BASE_URL + normalizedEndpoint
        );
    }

    private String encodeFormData(
            Map<String, String> formData) {

        if (formData == null || formData.isEmpty()) {
            return "";
        }

        return formData.entrySet()
                .stream()
                .map(entry ->
                        encode(entry.getKey())
                        + "="
                        + encode(entry.getValue())
                )
                .collect(
                        Collectors.joining("&")
                );
    }

    private String encode(String value) {

        return URLEncoder.encode(
                value == null ? "" : value,
                StandardCharsets.UTF_8
        );
    }

    private void validateResponse(
            HttpResponse<String> response)
            throws IOException {

        int statusCode =
                response.statusCode();

        if (statusCode < 200 || statusCode >= 300) {

            throw new IOException(
                    "API request failed with HTTP "
                    + statusCode
                    + ": "
                    + response.body()
            );
        }
    }
}