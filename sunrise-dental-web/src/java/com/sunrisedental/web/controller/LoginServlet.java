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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final int SESSION_TIMEOUT_SECONDS =
            30 * 60;

   private static final String LOGIN_PAGE =
        "/WEB-INF/views/login.jsp";

    private final ApiClient apiClient =
            new ApiClient();

    /*
     * GET /login
     *
     * Displays the login page.
     * If the user is already logged in,
     * redirect directly to dashboard.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        if (isAuthenticated(session)) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/dashboard"
            );

            return;
        }

        request.getRequestDispatcher(
                LOGIN_PAGE
        ).forward(
                request,
                response
        );
    }

    /*
     * POST /login
     *
     * Sends username/password to the
     * Sunrise Dental API for authentication.
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String username =
                normalizeUsername(
                        request.getParameter(
                                "username"
                        )
                );

        String password =
                request.getParameter(
                        "password"
                );

        /*
         * Basic presentation-layer validation.
         */
        if (isBlank(username)
                || isBlank(password)) {

            showLoginError(
                    request,
                    response,
                    "Username and password are required.",
                    username
            );

            return;
        }

        try {

            Map<String, String> credentials =
                    createCredentials(
                            username,
                            password
                    );

            /*
             * IMPORTANT:
             * Web application communicates with
             * API through HTTP.
             *
             * It does NOT connect directly to MySQL.
             */
            String apiResponse =
                    apiClient.post(
                            "/api/auth",
                            credentials
                    );

            AuthenticatedUser authenticatedUser =
                    parseAuthenticatedUser(
                            apiResponse
                    );

            /*
             * Authentication succeeded.
             * Create a fresh session.
             */
            createAuthenticatedSession(
                    request,
                    authenticatedUser
            );

            response.sendRedirect(
                    request.getContextPath()
                    + "/dashboard"
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "Authentication API request was interrupted.",
                    exception
            );

            showLoginError(
                    request,
                    response,
                    "Login service is temporarily unavailable.",
                    username
            );

        } catch (IOException exception) {

            /*
             * ApiClient throws IOException when
             * the API returns a non-success HTTP status,
             * such as 401 Unauthorized.
             */
            showLoginError(
                    request,
                    response,
                    "Invalid username or password.",
                    username
            );

        } catch (IllegalArgumentException exception) {

            /*
             * Protects the Web application if the
             * API returns an unexpected response.
             */
            getServletContext().log(
                    "Invalid authentication API response.",
                    exception
            );

            showLoginError(
                    request,
                    response,
                    "Unable to process the login response.",
                    username
            );
        }
    }

    /*
     * Creates a new authenticated session.
     *
     * Previous session is invalidated first
     * to reduce session-fixation risk.
     */
    private void createAuthenticatedSession(
            HttpServletRequest request,
            AuthenticatedUser user) {

        HttpSession oldSession =
                request.getSession(false);

        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession session =
                request.getSession(true);

        session.setAttribute(
                "userId",
                user.userId()
        );

        session.setAttribute(
                "username",
                user.username()
        );

        session.setAttribute(
                "fullName",
                user.fullName()
        );

        session.setAttribute(
                "role",
                user.role()
        );

        /*
         * Automatically expire session after
         * 30 minutes of inactivity.
         */
        session.setMaxInactiveInterval(
                SESSION_TIMEOUT_SECONDS
        );
    }

    private Map<String, String> createCredentials(
            String username,
            String password) {

        Map<String, String> credentials =
                new HashMap<>();

        credentials.put(
                "username",
                username
        );

        credentials.put(
                "password",
                password
        );

        return credentials;
    }

    /*
     * Converts the controlled JSON response
     * returned by /api/auth into a Web user.
     */
    private AuthenticatedUser parseAuthenticatedUser(
            String json) {

        if (json == null
                || json.isBlank()) {

            throw new IllegalArgumentException(
                    "Authentication response is empty."
            );
        }

        int userId;

        try {

            userId =
                    Integer.parseInt(
                            extractJsonValue(
                                    json,
                                    "userId"
                            )
                    );

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "Authentication response contains "
                    + "an invalid user ID.",
                    exception
            );
        }

        String username =
                extractJsonValue(
                        json,
                        "username"
                );

        String fullName =
                extractJsonValue(
                        json,
                        "fullName"
                );

        String role =
                extractJsonValue(
                        json,
                        "role"
                );

        if (userId <= 0
                || isBlank(username)
                || isBlank(fullName)
                || isBlank(role)) {

            throw new IllegalArgumentException(
                    "Authentication response is incomplete."
            );
        }

        return new AuthenticatedUser(
                userId,
                username,
                fullName,
                role
        );
    }

    /*
     * Minimal JSON parser for our controlled
     * authentication response.
     */
    private String extractJsonValue(
            String json,
            String key) {

        String token =
                "\"" + key + "\":";

        int keyIndex =
                json.indexOf(token);

        if (keyIndex < 0) {

            throw new IllegalArgumentException(
                    "Missing authentication field: "
                    + key
            );
        }

        int valueStart =
                keyIndex + token.length();

        while (valueStart < json.length()
                && Character.isWhitespace(
                        json.charAt(valueStart)
                )) {

            valueStart++;
        }

        if (valueStart >= json.length()) {

            throw new IllegalArgumentException(
                    "Invalid authentication response."
            );
        }

        /*
         * String JSON value.
         */
        if (json.charAt(valueStart) == '"') {

            valueStart++;

            int valueEnd =
                    json.indexOf(
                            '"',
                            valueStart
                    );

            if (valueEnd < 0) {

                throw new IllegalArgumentException(
                        "Invalid authentication response."
                );
            }

            return json.substring(
                    valueStart,
                    valueEnd
            );
        }

        /*
         * Numeric JSON value.
         */
        int valueEnd =
                valueStart;

        while (valueEnd < json.length()
                && Character.isDigit(
                        json.charAt(valueEnd)
                )) {

            valueEnd++;
        }

        if (valueEnd == valueStart) {

            throw new IllegalArgumentException(
                    "Invalid authentication response."
            );
        }

        return json.substring(
                valueStart,
                valueEnd
        );
    }

    private void showLoginError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message,
            String username)
            throws ServletException, IOException {

        request.setAttribute(
                "errorMessage",
                message
        );

        /*
         * Preserve username only.
         * Never preserve or return the password.
         */
        request.setAttribute(
                "enteredUsername",
                username
        );

        request.getRequestDispatcher(
                LOGIN_PAGE
        ).forward(
                request,
                response
        );
    }

    private boolean isAuthenticated(
            HttpSession session) {

        return session != null
                && session.getAttribute(
                        "userId"
                ) != null;
    }

    private String normalizeUsername(
            String username) {

        return username == null
                ? null
                : username.trim();
    }

    private boolean isBlank(
            String value) {

        return value == null
                || value.isBlank();
    }

    /*
     * Internal immutable representation of
     * an authenticated API user.
     */
    private record AuthenticatedUser(
            int userId,
            String username,
            String fullName,
            String role) {
    }
}