package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.User;
import com.sunrisedental.api.service.UserManagementService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/api/users")
public class UserServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE =
            "application/json";

    private static final String CHARACTER_ENCODING =
            "UTF-8";

    private final UserManagementService userManagementService =
            new UserManagementService();

    /*
     * ============================================================
     * GET
     * ============================================================
     *
     * GET /api/users
     *     -> list all users
     *
     * GET /api/users?userId=1
     *     -> retrieve one user
     * ============================================================
     */

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {

            String userIdParameter =
                    getTrimmedParameter(
                            request,
                            "userId"
                    );

            if (userIdParameter == null
                    || userIdParameter.isBlank()) {

                List<User> users =
                        userManagementService
                                .getAllUsers();

                writeUserList(
                        response,
                        users
                );

                return;
            }

            int userId =
                    parsePositiveInteger(
                            userIdParameter,
                            "User ID"
                    );

            Optional<User> user =
                    userManagementService
                            .getUserById(
                                    userId
                            );

            if (user.isEmpty()) {

                sendErrorResponse(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "User account was not found."
                );

                return;
            }

            writeUser(
                    response,
                    user.get()
            );

        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (SQLException exception) {

            logError(
                    "Unable to retrieve users.",
                    exception
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve users."
            );
        }
    }

    /*
     * ============================================================
     * POST
     * ============================================================
     *
     * Creates a new ADMIN or RECEPTIONIST account.
     *
     * Required form fields:
     * username
     * password
     * fullName
     * role
     * ============================================================
     */

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

            String fullName =
                    getTrimmedParameter(
                            request,
                            "fullName"
                    );

            String role =
                    getTrimmedParameter(
                            request,
                            "role"
                    );

            User createdUser =
                    userManagementService
                            .createUser(
                                    username,
                                    password,
                                    fullName,
                                    role
                            );

            response.setStatus(
                    HttpServletResponse.SC_CREATED
            );

            writeMessageWithUser(
                    response,
                    "User account created successfully.",
                    createdUser
            );

        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (SQLException exception) {

            logError(
                    "Unable to create user account.",
                    exception
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to create user account."
            );
        }
    }

    /*
     * ============================================================
     * PUT
     * ============================================================
     *
     * action=details
     *     userId
     *     fullName
     *     role
     *
     * action=status
     *     userId
     *     active
     *
     * action=password
     *     userId
     *     password
     *
     * Physical DELETE is intentionally unsupported.
     * ============================================================
     */

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {

            Map<String, String> formData =
                    parsePutFormData(
                            request
                    );

            String action =
                    getPutValue(
                            formData,
                            "action"
                    );

            if (action == null
                    || action.isBlank()) {

                throw new IllegalArgumentException(
                        "User management action is required."
                );
            }

            int userId =
                    parseUserId(
                            formData
                    );

            switch (
                    action.trim()
                            .toLowerCase()
            ) {

                case "details" ->
                        updateDetails(
                                response,
                                formData,
                                userId
                        );

                case "status" ->
                        updateStatus(
                                response,
                                formData,
                                userId
                        );

                case "password" ->
                        resetPassword(
                                response,
                                formData,
                                userId
                        );

                default ->
                        throw new IllegalArgumentException(
                                "Unsupported user management action."
                        );
            }

        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (SQLException exception) {

            logError(
                    "Unable to update user account.",
                    exception
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update user account."
            );
        }
    }

    /*
     * ============================================================
     * UPDATE DETAILS
     * ============================================================
     */

    private void updateDetails(
            HttpServletResponse response,
            Map<String, String> formData,
            int userId)
            throws SQLException, IOException {

        String fullName =
                getPutValue(
                        formData,
                        "fullName"
                );

        String role =
                getPutValue(
                        formData,
                        "role"
                );

        User updatedUser =
                userManagementService
                        .updateUserDetails(
                                userId,
                                fullName,
                                role
                        );

        writeMessageWithUser(
                response,
                "User account updated successfully.",
                updatedUser
        );
    }

    /*
     * ============================================================
     * UPDATE ACTIVE STATUS
     * ============================================================
     */

    private void updateStatus(
            HttpServletResponse response,
            Map<String, String> formData,
            int userId)
            throws SQLException, IOException {

        String activeValue =
                getPutValue(
                        formData,
                        "active"
                );

        boolean active =
                parseBoolean(
                        activeValue,
                        "Active status"
                );

        User updatedUser =
                userManagementService
                        .updateActiveStatus(
                                userId,
                                active
                        );

        String message =
                active
                        ? "User account activated successfully."
                        : "User account deactivated successfully.";

        writeMessageWithUser(
                response,
                message,
                updatedUser
        );
    }

    /*
     * ============================================================
     * RESET PASSWORD
     * ============================================================
     */

    private void resetPassword(
            HttpServletResponse response,
            Map<String, String> formData,
            int userId)
            throws SQLException, IOException {

        String password =
                getPutValue(
                        formData,
                        "password"
                );

        userManagementService
                .resetPassword(
                        userId,
                        password
                );

        writeMessage(
                response,
                "User password reset successfully."
        );
    }

    /*
     * ============================================================
     * PUT FORM PARSING
     * ============================================================
     */

    private Map<String, String> parsePutFormData(
            HttpServletRequest request)
            throws IOException {

        byte[] bodyBytes =
                request.getInputStream()
                        .readAllBytes();

        String requestBody =
                new String(
                        bodyBytes,
                        StandardCharsets.UTF_8
                );

        Map<String, String> formData =
                new HashMap<>();

        if (requestBody.isBlank()) {
            return formData;
        }

        String[] parameters =
                requestBody.split("&");

        for (String parameter : parameters) {

            int equalsIndex =
                    parameter.indexOf('=');

            String encodedKey;
            String encodedValue;

            if (equalsIndex >= 0) {

                encodedKey =
                        parameter.substring(
                                0,
                                equalsIndex
                        );

                encodedValue =
                        parameter.substring(
                                equalsIndex + 1
                        );

            } else {

                encodedKey =
                        parameter;

                encodedValue =
                        "";
            }

            String key =
                    URLDecoder.decode(
                            encodedKey,
                            StandardCharsets.UTF_8
                    );

            String value =
                    URLDecoder.decode(
                            encodedValue,
                            StandardCharsets.UTF_8
                    );

            formData.put(
                    key,
                    value.trim()
            );
        }

        return formData;
    }

    /*
     * ============================================================
     * VALIDATION / PARSING HELPERS
     * ============================================================
     */

    private int parseUserId(
            Map<String, String> formData) {

        String value =
                getPutValue(
                        formData,
                        "userId"
                );

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    "User ID is required."
            );
        }

        return parsePositiveInteger(
                value,
                "User ID"
        );
    }

    private int parsePositiveInteger(
            String value,
            String fieldName) {

        try {

            int parsedValue =
                    Integer.parseInt(
                            value
                    );

            if (parsedValue <= 0) {

                throw new IllegalArgumentException(
                        fieldName
                                + " must be greater than zero."
                );
            }

            return parsedValue;

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    fieldName
                            + " must be a valid number."
            );
        }
    }

    private boolean parseBoolean(
            String value,
            String fieldName) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    fieldName
                            + " is required."
            );
        }

        if ("true".equalsIgnoreCase(value)) {
            return true;
        }

        if ("false".equalsIgnoreCase(value)) {
            return false;
        }

        throw new IllegalArgumentException(
                fieldName
                        + " must be true or false."
        );
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

    private String getPutValue(
            Map<String, String> formData,
            String parameterName) {

        String value =
                formData.get(
                        parameterName
                );

        return value == null
                ? null
                : value.trim();
    }

    /*
     * ============================================================
     * JSON RESPONSE HELPERS
     * ============================================================
     */

    private void writeUserList(
            HttpServletResponse response,
            List<User> users)
            throws IOException {

        PrintWriter writer =
                response.getWriter();

        writer.print("[");

        for (int index = 0;
             index < users.size();
             index++) {

            if (index > 0) {
                writer.print(",");
            }

            writer.print(
                    toJson(
                            users.get(index)
                    )
            );
        }

        writer.print("]");
    }

    private void writeUser(
            HttpServletResponse response,
            User user)
            throws IOException {

        response.getWriter()
                .print(
                        toJson(user)
                );
    }

    private void writeMessageWithUser(
            HttpServletResponse response,
            String message,
            User user)
            throws IOException {

        response.getWriter()
                .print(
                        "{"
                                + "\"message\":\""
                                + escapeJson(message)
                                + "\","
                                + "\"user\":"
                                + toJson(user)
                                + "}"
                );
    }

    private void writeMessage(
            HttpServletResponse response,
            String message)
            throws IOException {

        response.getWriter()
                .print(
                        "{"
                                + "\"message\":\""
                                + escapeJson(message)
                                + "\""
                                + "}"
                );
    }

    private String toJson(
            User user) {

        return "{"
                + "\"userId\":"
                + user.getUserId()
                + ","
                + "\"username\":\""
                + escapeJson(
                        user.getUsername()
                )
                + "\","
                + "\"fullName\":\""
                + escapeJson(
                        user.getFullName()
                )
                + "\","
                + "\"role\":\""
                + escapeJson(
                        user.getRole()
                )
                + "\","
                + "\"active\":"
                + user.isActive()
                + ","
                + "\"createdAt\":"
                + (
                    user.getCreatedAt() == null
                            ? "null"
                            : "\""
                            + escapeJson(
                                    user.getCreatedAt()
                                            .toString()
                            )
                            + "\""
                )
                + "}";
    }

    /*
     * ============================================================
     * COMMON RESPONSE HELPERS
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
                                + "\"error\":\""
                                + escapeJson(message)
                                + "\""
                                + "}"
                );
    }

    private void logError(
            String message,
            Exception exception) {

        getServletContext()
                .log(
                        message,
                        exception
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