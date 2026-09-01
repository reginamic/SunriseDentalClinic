package com.sunrisedental.web.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.UserViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.lang.reflect.Type;

import java.util.Collections;
import java.util.List;

@WebServlet("/users")
public class UsersServlet extends HttpServlet {

    private static final String USERS_PAGE =
            "/WEB-INF/views/users.jsp";

    private final ApiClient apiClient =
            new ApiClient();

    private final Gson gson =
            new Gson();


    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Staff account administration is an
         * ADMIN-only function.
         */
        if (!isAdmin(request)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Administrator access is required."
            );

            return;
        }

        try {

            String jsonResponse =
                    apiClient.get(
                            "/api/users"
                    );

            Type userListType =
                    new TypeToken<
                            List<UserViewModel>>() {
                    }.getType();

            List<UserViewModel> users =
                    gson.fromJson(
                            jsonResponse,
                            userListType
                    );

            if (users == null) {
                users =
                        Collections.emptyList();
            }

            long activeUserCount =
                    users.stream()
                            .filter(
                                    UserViewModel::isActive
                            )
                            .count();

            long administratorCount =
                    users.stream()
                            .filter(
                                    UserViewModel::isAdmin
                            )
                            .count();

            long receptionistCount =
                    users.stream()
                            .filter(
                                    UserViewModel
                                            ::isReceptionist
                            )
                            .count();

            request.setAttribute(
                    "users",
                    users
            );

            request.setAttribute(
                    "userCount",
                    users.size()
            );

            request.setAttribute(
                    "activeUserCount",
                    activeUserCount
            );

            request.setAttribute(
                    "administratorCount",
                    administratorCount
            );

            request.setAttribute(
                    "receptionistCount",
                    receptionistCount
            );

            request.getRequestDispatcher(
                    USERS_PAGE
            ).forward(
                    request,
                    response
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "User API request was interrupted.",
                    exception
            );

            showErrorPage(
                    request,
                    response,
                    "User management service is temporarily unavailable."
            );

        } catch (IOException | RuntimeException exception) {

            getServletContext().log(
                    "Unable to retrieve staff users from REST API.",
                    exception
            );

            showErrorPage(
                    request,
                    response,
                    "Unable to load staff account information."
            );
        }
    }


    private void showErrorPage(
            HttpServletRequest request,
            HttpServletResponse response,
            String message)
            throws ServletException, IOException {

        request.setAttribute(
                "users",
                Collections.emptyList()
        );

        request.setAttribute(
                "userCount",
                0
        );

        request.setAttribute(
                "activeUserCount",
                0L
        );

        request.setAttribute(
                "administratorCount",
                0L
        );

        request.setAttribute(
                "receptionistCount",
                0L
        );

        request.setAttribute(
                "errorMessage",
                message
        );

        request.getRequestDispatcher(
                USERS_PAGE
        ).forward(
                request,
                response
        );
    }


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
}