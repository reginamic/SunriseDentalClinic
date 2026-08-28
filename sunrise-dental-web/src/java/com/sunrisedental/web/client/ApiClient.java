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

    private static final String BASE_URL =
            "http://localhost:8081/sunrise-dental-api";

    private final HttpClient httpClient;

    public ApiClient() {

        this.httpClient =
                HttpClient.newBuilder()
                        .build();
    }

    /**
     * Sends an HTTP GET request to the API.
     */
    public String get(
            String endpoint)
            throws IOException, InterruptedException {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL
                                        + endpoint
                                )
                        )
                        .GET()
                        .header(
                                "Accept",
                                "application/json"
                        )
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        validateResponse(
                response
        );

        return response.body();
    }

    /**
     * Sends an HTTP POST request using
     * application/x-www-form-urlencoded.
     */
    public String post(
            String endpoint,
            Map<String, String> formData)
            throws IOException, InterruptedException {

        String encodedForm =
                encodeFormData(
                        formData
                );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL
                                        + endpoint
                                )
                        )
                        .header(
                                "Content-Type",
                                "application/x-www-form-urlencoded"
                        )
                        .header(
                                "Accept",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(
                                                encodedForm
                                        )
                        )
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        validateResponse(
                response
        );

        return response.body();
    }

    /**
     * Sends an HTTP PUT request using
     * application/x-www-form-urlencoded.
     *
     * Used when existing resources such as
     * patients are updated.
     */
    public String put(
            String endpoint,
            Map<String, String> formData)
            throws IOException, InterruptedException {

        String encodedForm =
                encodeFormData(
                        formData
                );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL
                                        + endpoint
                                )
                        )
                        .header(
                                "Content-Type",
                                "application/x-www-form-urlencoded"
                        )
                        .header(
                                "Accept",
                                "application/json"
                        )
                        .PUT(
                                HttpRequest.BodyPublishers
                                        .ofString(
                                                encodedForm
                                        )
                        )
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        validateResponse(
                response
        );

        return response.body();
    }

    /**
     * Converts form values into standard
     * URL-encoded request data.
     */
    private String encodeFormData(
            Map<String, String> formData) {

        return formData.entrySet()
                .stream()
                .map(
                        entry ->
                                encode(
                                        entry.getKey()
                                )
                                + "="
                                + encode(
                                        entry.getValue()
                                )
                )
                .collect(
                        Collectors.joining("&")
                );
    }

    private String encode(
            String value) {

        if (value == null) {
            return "";
        }

        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }

    /**
     * Accept only successful 2xx HTTP responses.
     */
    private void validateResponse(
            HttpResponse<String> response)
            throws IOException {

        int statusCode =
                response.statusCode();

        if (statusCode < 200
                || statusCode >= 300) {

            throw new IOException(
                    "API request failed. HTTP "
                    + statusCode
                    + ": "
                    + response.body()
            );
        }
    }
}