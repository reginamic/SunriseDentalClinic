package com.sunrisedental.web.controller;

import com.sunrisedental.web.client.ApiClient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

@WebServlet("/users/manage")
public class ManageUsersServlet extends HttpServlet {

    private final ApiClient apiClient =
            new ApiClient();


    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * User administration must remain ADMIN-only.
         * This check is server-side and therefore cannot
         * be bypassed by manually entering the URL.
         */
        if (!isAdmin(request)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Administrator access is required."
            );

            return;
        }

        request.setCharacterEncoding(
                "UTF-8"
        );

        String action =
                getTrimmedParameter(
                        request,
                        "action"
                );

        if (action == null
                || action.isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "User management action is required."
            );

            return;
        }

        try {

            switch (
                    action.toLowerCase()
            ) {

                case "create" ->
                        createUser(
                                request,
                                response
                        );

                case "details" ->
                        updateDetails(
                                request,
                                response
                        );

                case "status" ->
                        updateStatus(
                                request,
                                response
                        );

                case "password" ->
                        resetPassword(
                                request,
                                response
                        );

                default ->
                        response.sendError(
                                HttpServletResponse.SC_BAD_REQUEST,
                                "Unsupported user management action."
                        );
            }

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "User management API request was interrupted.",
                    exception
            );

            response.sendRedirect(
                    request.getContextPath()
                    + "/users"
            );

        } catch (IOException | RuntimeException exception) {

            getServletContext().log(
                    "Unable to complete user management operation.",
                    exception
            );

            response.sendRedirect(
                    request.getContextPath()
                    + "/users"
            );
        }
    }


    /*
     * ============================================================
     * CREATE STAFF ACCOUNT
     * ============================================================
     */

    private void createUser(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, InterruptedException {

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


        Map<String, String> formData =
                new HashMap<>();

        formData.put(
                "username",
                username
        );

        formData.put(
                "password",
                password
        );

        formData.put(
                "fullName",
                fullName
        );

        formData.put(
                "role",
                role
        );


        apiClient.post(
                "/api/users",
                formData
        );


        response.sendRedirect(
                request.getContextPath()
                + "/users?created=true"
        );
    }


    /*
     * ============================================================
     * UPDATE NAME / ROLE
     * ============================================================
     */

    private void updateDetails(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, InterruptedException {

        int userId =
                parseUserId(
                        request
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


        Map<String, String> formData =
                new HashMap<>();

        formData.put(
                "action",
                "details"
        );

        formData.put(
                "userId",
                String.valueOf(
                        userId
                )
        );

        formData.put(
                "fullName",
                fullName
        );

        formData.put(
                "role",
                role
        );


        apiClient.put(
                "/api/users",
                formData
        );


        response.sendRedirect(
                request.getContextPath()
                + "/users?updated=true"
        );
    }


    /*
     * ============================================================
     * ACTIVATE / DEACTIVATE
     * ============================================================
     */

    private void updateStatus(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, InterruptedException {

        int userId =
                parseUserId(
                        request
                );

        String active =
                getTrimmedParameter(
                        request,
                        "active"
                );


        if (!"true".equalsIgnoreCase(active)
                && !"false".equalsIgnoreCase(active)) {

            throw new IllegalArgumentException(
                    "Valid account status is required."
            );
        }


        /*
         * Prevent an administrator from deactivating
         * the account currently being used.
         */
        if ("false".equalsIgnoreCase(active)
                && userId
                == getCurrentUserId(request)) {

            throw new IllegalArgumentException(
                    "The currently signed-in account cannot be deactivated."
            );
        }


        Map<String, String> formData =
                new HashMap<>();

        formData.put(
                "action",
                "status"
        );

        formData.put(
                "userId",
                String.valueOf(
                        userId
                )
        );

        formData.put(
                "active",
                active
        );


        apiClient.put(
                "/api/users",
                formData
        );


        response.sendRedirect(
                request.getContextPath()
                + "/users?statusChanged=true"
        );
    }


    /*
     * ============================================================
     * PASSWORD RESET
     * ============================================================
     */

    private void resetPassword(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, InterruptedException {

        int userId =
                parseUserId(
                        request
                );

        String password =
                request.getParameter(
                        "password"
                );


        Map<String, String> formData =
                new HashMap<>();

        formData.put(
                "action",
                "password"
        );

        formData.put(
                "userId",
                String.valueOf(
                        userId
                )
        );

        formData.put(
                "password",
                password
        );


        apiClient.put(
                "/api/users",
                formData
        );


        response.sendRedirect(
                request.getContextPath()
                + "/users?passwordReset=true"
        );
    }


    /*
     * ============================================================
     * VALIDATION HELPERS
     * ============================================================
     */

    private int parseUserId(
            HttpServletRequest request) {

        String value =
                getTrimmedParameter(
                        request,
                        "userId"
                );

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    "User ID is required."
            );
        }

        try {

            int userId =
                    Integer.parseInt(
                            value
                    );

            if (userId <= 0) {

                throw new IllegalArgumentException(
                        "User ID must be greater than zero."
                );
            }

            return userId;

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "User ID must be a valid number."
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


    /*
     * ============================================================
     * SESSION / RBAC
     * ============================================================
     */

    private boolean isAdmin(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(false);

        if (session == null) {
            return false;
        }

        Object role =
                session.getAttribute(
                        "role"
                );

        return role != null
                && "ADMIN".equalsIgnoreCase(
                        role.toString()
                );
    }


    private int getCurrentUserId(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(false);

        if (session == null) {
            return -1;
        }

        Object userId =
                session.getAttribute(
                        "userId"
                );

        if (userId == null) {
            return -1;
        }

        if (userId instanceof Number number) {

            return number.intValue();
        }

        try {

            return Integer.parseInt(
                    userId.toString()
            );

        } catch (NumberFormatException exception) {

            return -1;
        }
    }
}