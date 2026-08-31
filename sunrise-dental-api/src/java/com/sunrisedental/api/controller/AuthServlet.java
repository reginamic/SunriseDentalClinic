package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.User;

import com.sunrisedental.api.pattern.bridge.DatabaseAuditLogWriter;
import com.sunrisedental.api.pattern.bridge.SecurityAuditLogger;

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

    /*
     * ============================================================
     * BRIDGE PATTERN
     * ============================================================
     *
     * SecurityAuditLogger represents the abstraction.
     *
     * DatabaseAuditLogWriter represents the concrete
     * implementation responsible for storing audit records.
     *
     * The authentication controller depends on the high-level
     * security audit abstraction rather than JDBC persistence
     * details.
     */
    private final SecurityAuditLogger securityAuditLogger =
            new SecurityAuditLogger(
                    new DatabaseAuditLogWriter()
            );

    /*
     * ============================================================
     * POST /api/auth
     * ============================================================
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(
                response
        );

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

            /*
             * ----------------------------------------------------
             * AUTHENTICATION
             * ----------------------------------------------------
             */
            User authenticatedUser =
                    authService.authenticate(
                            username,
                            password
                    );

            /*
             * ----------------------------------------------------
             * SECURITY AUDIT - SUCCESS
             * ----------------------------------------------------
             *
             * Audit failure must NOT prevent an authenticated
             * member of staff from using the system.
             */
            recordSuccessfulLogin(
                    request,
                    authenticatedUser
            );

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            try (
                    PrintWriter out =
                            response.getWriter()
            ) {

                out.print("{");

                out.print(
                        "\"authenticated\":true,"
                );

                out.print(
                        "\"userId\":"
                        + authenticatedUser
                                .getUserId()
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
             * ----------------------------------------------------
             * SECURITY AUDIT - FAILED AUTHENTICATION
             * ----------------------------------------------------
             *
             * Never record the password.
             *
             * The username and request source address are enough
             * for the security audit trail.
             */
            recordFailedLogin(
                    request,
                    username
            );

            /*
             * Do not reveal whether the username or
             * password was incorrect.
             */
            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid username or password."
            );

        } catch (SQLException exception) {

            /*
             * A database failure is an infrastructure problem,
             * not an invalid-credentials event.
             */
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

    /*
     * ============================================================
     * GET /api/auth
     * ============================================================
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(
                response
        );

        sendErrorResponse(
                response,
                HttpServletResponse
                        .SC_METHOD_NOT_ALLOWED,
                "POST request required."
        );
    }

    /*
     * ============================================================
     * SECURITY AUDIT HELPERS
     * ============================================================
     */

    private void recordSuccessfulLogin(
            HttpServletRequest request,
            User authenticatedUser) {

        try {

            securityAuditLogger
                    .logLoginSuccess(
                            authenticatedUser
                                    .getUserId(),
                            authenticatedUser
                                    .getUsername(),
                            getRequestSourceAddress(
                                    request
                            )
                    );

        } catch (SQLException exception) {

            /*
             * IMPORTANT:
             *
             * Audit persistence is secondary to authentication.
             *
             * If audit_logs cannot be written temporarily,
             * successful authentication must continue.
             */
            getServletContext().log(
                    "Unable to persist successful-login audit record.",
                    exception
            );
        }
    }

    private void recordFailedLogin(
            HttpServletRequest request,
            String username) {

        try {

            securityAuditLogger
                    .logLoginFailure(
                            username,
                            getRequestSourceAddress(
                                    request
                            )
                    );

        } catch (SQLException exception) {

            /*
             * Invalid credentials must still return HTTP 401
             * even when the audit destination is unavailable.
             */
            getServletContext().log(
                    "Unable to persist failed-login audit record.",
                    exception
            );
        }
    }

    /*
     * Address of the HTTP connection reaching the API.
     *
     * In the local distributed deployment this may appear as
     * 127.0.0.1 or an IPv6 loopback address because the Web tier
     * communicates with the API on the same development machine.
     */
    private String getRequestSourceAddress(
            HttpServletRequest request) {

        if (request == null) {
            return null;
        }

        String address =
                request.getRemoteAddr();

        if (address == null
                || address.isBlank()) {

            return null;
        }

        return address.trim();
    }

    /*
     * ============================================================
     * RESPONSE CONFIGURATION
     * ============================================================
     */
    private void configureJsonResponse(
            HttpServletResponse response) {

        response.setContentType(
                JSON_CONTENT_TYPE
        );

        response.setCharacterEncoding(
                CHARACTER_ENCODING
        );
    }

    /*
     * ============================================================
     * JSON ERROR RESPONSE
     * ============================================================
     */
    private void sendErrorResponse(
            HttpServletResponse response,
            int status,
            String message)
            throws IOException {

        response.setStatus(
                status
        );

        try (
                PrintWriter out =
                        response.getWriter()
        ) {

            out.print(
                    "{\"error\":\""
                    + escapeJson(
                            message
                    )
                    + "\"}"
            );
        }
    }

    /*
     * ============================================================
     * INPUT NORMALIZATION
     * ============================================================
     */
    private String normalize(
            String value) {

        if (value == null) {
            return null;
        }

        return value.trim();
    }

    /*
     * ============================================================
     * JSON ESCAPING
     * ============================================================
     */
    private String escapeJson(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\r",
                        "\\r"
                )
                .replace(
                        "\t",
                        "\\t"
                );
    }
}