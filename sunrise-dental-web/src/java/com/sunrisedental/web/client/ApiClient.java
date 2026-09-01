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

    /*
     * ============================================================
     * API CONFIGURATION
     * ============================================================
     *
     * Deployment configuration can be supplied using:
     *
     * Environment variable:
     *   SUNRISE_API_BASE_URL
     *
     * or Java system property:
     *   sunrise.api.base.url
     *
     * Local development fallback preserves the existing
     * Tomcat configuration.
     * ============================================================
     */

    private static final String DEFAULT_BASE_URL =
            "http://localhost:8081/sunrise-dental-api";

    private final HttpClient httpClient;

    private final String baseUrl;


    public ApiClient() {

        this.httpClient =
                HttpClient.newBuilder()
                        .build();

        this.baseUrl =
                normalizeBaseUrl(
                        resolveConfiguration(
                                "sunrise.api.base.url",
                                "SUNRISE_API_BASE_URL",
                                DEFAULT_BASE_URL
                        )
                );
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
                                buildUri(endpoint)
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

        validateResponse(response);

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
                                buildUri(endpoint)
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

        validateResponse(response);

        return response.body();
    }


    /**
     * Sends an HTTP PUT request using
     * application/x-www-form-urlencoded.
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
                                buildUri(endpoint)
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

        validateResponse(response);

        return response.body();
    }


    /*
     * ============================================================
     * URI CONSTRUCTION
     * ============================================================
     */

    private URI buildUri(
            String endpoint) {

        if (endpoint == null
                || endpoint.isBlank()) {

            throw new IllegalArgumentException(
                    "API endpoint is required."
            );
        }

        String normalizedEndpoint =
                endpoint.startsWith("/")
                        ? endpoint
                        : "/" + endpoint;

        return URI.create(
                baseUrl
                + normalizedEndpoint
        );
    }


    private String normalizeBaseUrl(
            String value) {

        String normalized =
                value.trim();

        while (normalized.endsWith("/")) {

            normalized =
                    normalized.substring(
                            0,
                            normalized.length() - 1
                    );
        }

        return normalized;
    }


    /*
     * ============================================================
     * FORM ENCODING
     * ============================================================
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


    /*
     * ============================================================
     * RESPONSE VALIDATION
     * ============================================================
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


    /*
     * ============================================================
     * CONFIGURATION RESOLUTION
     * ============================================================
     *
     * Priority:
     *
     * 1. Java system property
     * 2. Operating-system environment variable
     * 3. Local development fallback
     * ============================================================
     */

    private String resolveConfiguration(
            String systemPropertyName,
            String environmentVariableName,
            String defaultValue) {

        String systemProperty =
                System.getProperty(
                        systemPropertyName
                );

        if (systemProperty != null
                && !systemProperty.isBlank()) {

            return systemProperty.trim();
        }


        String environmentValue =
                System.getenv(
                        environmentVariableName
                );

        if (environmentValue != null
                && !environmentValue.isBlank()) {

            return environmentValue.trim();
        }


        return defaultValue;
    }
}