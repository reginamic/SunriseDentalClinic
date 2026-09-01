package com.sunrisedental.web.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Displays the Sunrise Dental Clinic help and user guidance page.
 *
 * The Help function is available to authenticated ADMIN and
 * RECEPTIONIST users.
 */
@WebServlet("/help")
public class HelpServlet extends HttpServlet {

    private static final String HELP_PAGE =
            "/WEB-INF/views/help.jsp";

    /*
     * ============================================================
     * GET /help
     * ============================================================
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        /*
         * Defense-in-depth.
         *
         * The application already uses authentication protection,
         * but the servlet also verifies the session before exposing
         * authenticated system guidance.
         */
        if (!isAuthenticated(session)) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/login"
            );

            return;
        }

        /*
         * Help is intentionally available to both supported roles:
         *
         * ADMIN
         * RECEPTIONIST
         *
         * Individual guidance can later be displayed conditionally
         * inside the JSP according to the role stored in the session.
         */
        request.setAttribute(
                "pageTitle",
                "Help & User Guide"
        );

        request.getRequestDispatcher(
                HELP_PAGE
        ).forward(
                request,
                response
        );
    }

    /*
     * ============================================================
     * SESSION VALIDATION
     * ============================================================
     */
    private boolean isAuthenticated(
            HttpSession session) {

        return session != null
                && session.getAttribute(
                        "userId"
                ) != null
                && session.getAttribute(
                        "role"
                ) != null;
    }
}