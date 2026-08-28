package com.sunrisedental.web.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

  // DashboardServlet
private static final String DASHBOARD_PAGE =
        "/WEB-INF/views/dashboard.jsp";

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        /*
         * Protect the dashboard.
         * Users without an authenticated session
         * must return to the login page.
         */
        if (!isAuthenticated(session)) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/login"
            );

            return;
        }

        request.getRequestDispatcher(
                DASHBOARD_PAGE
        ).forward(
                request,
                response
        );
    }

    private boolean isAuthenticated(
            HttpSession session) {

        return session != null
                && session.getAttribute("userId") != null
                && session.getAttribute("username") != null
                && session.getAttribute("role") != null;
    }
}