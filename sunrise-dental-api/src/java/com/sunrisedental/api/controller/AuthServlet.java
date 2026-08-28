package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.User;
import com.sunrisedental.api.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/auth")
public class AuthServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE =
            "application/json";

    private static final String CHARACTER_ENCODING =
            "UTF-8";

    private final AuthService authService =
            new AuthService();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {

            String username =
                    getTrimmedParameter(
                            request,
                            "username"
                    );

            String password =
                    request.getParameter(
                            "password"
                    );

            User authenticatedUser =
                    authService.authenticate(
                            username,
                            password
                    );

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            writeSuccessResponse(
                    response,
                    authenticatedUser
            );

        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    exception.getMessage()
            );

        } catch (SQLException exception) {

            getServletContext().log(
                    "Authentication database error.",
                    exception
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to process login request."
            );
        }
    }

    private String getTrimmedParameter(
            HttpServletRequest request,
            String parameterName) {

        String value =
                request.getParameter(
                        parameterName
                );

        return value == null
                ? null
                : value.trim();
    }

    private void writeSuccessResponse(
            HttpServletResponse response,
            User user)
            throws IOException {

        response.getWriter().print(
                "{"
                + "\"authenticated\":true,"
                + "\"user\":{"
                + "\"userId\":"
                + user.getUserId()
                + ","
                + "\"username\":\""
                + escapeJson(user.getUsername())
                + "\","
                + "\"fullName\":\""
                + escapeJson(user.getFullName())
                + "\","
                + "\"role\":\""
                + escapeJson(user.getRole())
                + "\""
                + "}"
                + "}"
        );
    }

    private void configureJsonResponse(
            HttpServletResponse response) {

        response.setContentType(
                JSON_CONTENT_TYPE
        );

        response.setCharacterEncoding(
                CHARACTER_ENCODING
        );
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            int statusCode,
            String message)
            throws IOException {

        response.setStatus(
                statusCode
        );

        response.getWriter().print(
                "{"
                + "\"authenticated\":false,"
                + "\"error\":\""
                + escapeJson(message)
                + "\""
                + "}"
        );
    }

    private String escapeJson(
            String value) {

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