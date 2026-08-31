package com.sunrisedental.web.controller;

import com.sunrisedental.web.model.DashboardSummaryViewModel;
import com.sunrisedental.web.pattern.facade.DashboardFacade;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final String DASHBOARD_PAGE =
            "/WEB-INF/views/dashboard.jsp";

    private final DashboardFacade dashboardFacade;

    /*
     * Production constructor used by Tomcat.
     */
    public DashboardServlet() {

        this.dashboardFacade =
                new DashboardFacade();
    }

    /*
     * ============================================================
     * GET - DASHBOARD
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
         * --------------------------------------------------------
         * Authentication check
         * --------------------------------------------------------
         */
        if (!isAuthenticated(session)) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/login"
            );

            return;
        }

        try {

            /*
             * ----------------------------------------------------
             * FACADE PATTERN
             * ----------------------------------------------------
             *
             * DashboardServlet does not communicate separately
             * with Patients, Dentists, Treatments, Appointments
             * and Billing APIs.
             *
             * It asks one Facade object for a complete dashboard
             * summary.
             */
            DashboardSummaryViewModel summary =
                    dashboardFacade
                            .getDashboardSummary();

            request.setAttribute(
                    "dashboardSummary",
                    summary
            );

            request.setAttribute(
                    "dashboardServiceAvailable",
                    true
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "Dashboard API request was interrupted.",
                    exception
            );

            /*
             * The authenticated user can still see the dashboard
             * even if one of the REST services is temporarily
             * unavailable.
             */
            request.setAttribute(
                    "dashboardSummary",
                    new DashboardSummaryViewModel()
            );

            request.setAttribute(
                    "dashboardServiceAvailable",
                    false
            );

            request.setAttribute(
                    "dashboardError",
                    "Live clinic statistics are temporarily unavailable."
            );

        } catch (IOException exception) {

            getServletContext().log(
                    "Unable to retrieve dashboard information "
                    + "through DashboardFacade.",
                    exception
            );

            request.setAttribute(
                    "dashboardSummary",
                    new DashboardSummaryViewModel()
            );

            request.setAttribute(
                    "dashboardServiceAvailable",
                    false
            );

            request.setAttribute(
                    "dashboardError",
                    "Live clinic statistics are temporarily unavailable."
            );
        }

        /*
         * --------------------------------------------------------
         * Presentation
         * --------------------------------------------------------
         */
        request.getRequestDispatcher(
                DASHBOARD_PAGE
        ).forward(
                request,
                response
        );
    }

    /*
     * ============================================================
     * SESSION AUTHENTICATION HELPER
     * ============================================================
     */
    private boolean isAuthenticated(
            HttpSession session) {

        return session != null
                && session.getAttribute(
                        "userId"
                ) != null
                && session.getAttribute(
                        "username"
                ) != null
                && session.getAttribute(
                        "role"
                ) != null;
    }
}