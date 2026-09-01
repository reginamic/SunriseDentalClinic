package com.sunrisedental.web.controller;

import com.sunrisedental.web.client.ApiClient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/system-check")
public class WebApiHealthServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE =
            "application/json";

    private static final String CHARACTER_ENCODING =
            "UTF-8";

    private final ApiClient apiClient =
            new ApiClient();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType(
                JSON_CONTENT_TYPE
        );

        response.setCharacterEncoding(
                CHARACTER_ENCODING
        );

        try {

            String apiResponse =
                    apiClient.get(
                            "/api/health"
                    );

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            response.getWriter()
                    .print(apiResponse);

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "API health request was interrupted.",
                    exception
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "API communication was interrupted."
            );

        } catch (IOException exception) {

            getServletContext().log(
                    "Unable to communicate with Sunrise Dental API.",
                    exception
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Sunrise Dental API is currently unavailable."
            );
        }
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            int statusCode,
            String message)
            throws IOException {

        response.setStatus(
                statusCode
        );

        response.getWriter()
                .print(
                        "{"
                        + "\"status\":\"DOWN\","
                        + "\"message\":\""
                        + escapeJson(message)
                        + "\""
                        + "}"
                );
    }

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}