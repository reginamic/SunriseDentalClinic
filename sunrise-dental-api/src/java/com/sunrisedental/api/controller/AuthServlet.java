package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.User;
import com.sunrisedental.api.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(
        name = "AuthServlet",
        urlPatterns = {"/api/auth"}
)
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

        String username =
                normalize(
                        request.getParameter(
                                "username"
                        )
                );

        String password =
                request.getParameter(
                        "password"
                );

        try {

            User authenticatedUser =
                    authService.authenticate(
                            username,
                            password
                    );

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            try (PrintWriter out =
                    response.getWriter()) {

                out.print("{");

                out.print(
                        "\"authenticated\":true,"
                );

                out.print(
                        "\"userId\":"
                        + authenticatedUser.getUserId()
                        + ","
                );

                out.print(
                        "\"username\":\""
                        + escapeJson(
                                authenticatedUser
                                        .getUsername()
                        )
                        + "\","
                );

                out.print(
                        "\"fullName\":\""
                        + escapeJson(
                                authenticatedUser
                                        .getFullName()
                        )
                        + "\","
                );

                out.print(
                        "\"role\":\""
                        + escapeJson(
                                authenticatedUser
                                        .getRole()
                        )
                        + "\""
                );

                out.print("}");
            }

        } catch (IllegalArgumentException exception) {

            /*
             * Do not reveal whether the username
             * or password was incorrect.
             */
            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid username or password."
            );

        } catch (SQLException exception) {

            getServletContext().log(
                    "Authentication database error.",
                    exception
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR,
                    "Authentication service unavailable."
            );
        }
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        sendErrorResponse(
                response,
                HttpServletResponse
                        .SC_METHOD_NOT_ALLOWED,
                "POST request required."
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
            int status,
            String message)
            throws IOException {

        response.setStatus(status);

        try (PrintWriter out =
                response.getWriter()) {

            out.print(
                    "{\"error\":\""
                    + escapeJson(message)
                    + "\"}"
            );
        }
    }

    private String normalize(
            String value) {

        if (value == null) {
            return null;
        }

        return value.trim();
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