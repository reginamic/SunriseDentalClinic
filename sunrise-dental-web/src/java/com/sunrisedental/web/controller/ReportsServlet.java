package com.sunrisedental.web.controller;

import com.google.gson.Gson;

import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.ReportViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/reports")
public class ReportsServlet extends HttpServlet {

    private final ApiClient apiClient;
    private final Gson gson;

    public ReportsServlet() {

        this.apiClient =
                new ApiClient();

        this.gson =
                new Gson();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Reports are administrative decision-support
         * functionality, therefore only ADMIN users
         * are permitted to access this controller.
         */
        HttpSession session =
                request.getSession(false);

        if (session == null
                || session.getAttribute("userId") == null) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/login"
            );

            return;
        }

        String role =
                String.valueOf(
                        session.getAttribute("role")
                );

        if (!"ADMIN".equalsIgnoreCase(role)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "You do not have permission to access reports."
            );

            return;
        }

        LocalDate today =
                LocalDate.now();

        /*
         * Default to the current calendar year.
         *
         * The user can choose any other valid date range
         * using the Reports page.
         */
        LocalDate defaultFromDate =
                LocalDate.of(
                        today.getYear(),
                        1,
                        1
                );

        LocalDate defaultToDate =
                LocalDate.of(
                        today.getYear(),
                        12,
                        31
                );

        String fromValue =
                trimToNull(
                        request.getParameter("from")
                );

        String toValue =
                trimToNull(
                        request.getParameter("to")
                );

        if (fromValue == null) {
            fromValue =
                    defaultFromDate.toString();
        }

        if (toValue == null) {
            toValue =
                    defaultToDate.toString();
        }

        try {

            LocalDate fromDate =
                    LocalDate.parse(fromValue);

            LocalDate toDate =
                    LocalDate.parse(toValue);

            if (fromDate.isAfter(toDate)) {

                request.setAttribute(
                        "error",
                        "From date cannot be after to date."
                );

                request.setAttribute(
                        "fromDate",
                        fromValue
                );

                request.setAttribute(
                        "toDate",
                        toValue
                );

                forwardToReportPage(
                        request,
                        response
                );

                return;
            }

            String endpoint =
                    "/api/reports"
                    + "?from="
                    + encode(fromValue)
                    + "&to="
                    + encode(toValue);

            String json =
                    apiClient.get(endpoint);

            ReportViewModel report =
                    gson.fromJson(
                            json,
                            ReportViewModel.class
                    );

            request.setAttribute(
                    "report",
                    report
            );

            request.setAttribute(
                    "fromDate",
                    fromValue
            );

            request.setAttribute(
                    "toDate",
                    toValue
            );

            forwardToReportPage(
                    request,
                    response
            );

        } catch (DateTimeParseException exception) {

            request.setAttribute(
                    "error",
                    "Please select a valid reporting date range."
            );

            request.setAttribute(
                    "fromDate",
                    fromValue
            );

            request.setAttribute(
                    "toDate",
                    toValue
            );

            forwardToReportPage(
                    request,
                    response
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext()
                    .log(
                            "Reports API request was interrupted.",
                            exception
                    );

            request.setAttribute(
                    "error",
                    "The reporting service was interrupted. Please try again."
            );

            request.setAttribute(
                    "fromDate",
                    fromValue
            );

            request.setAttribute(
                    "toDate",
                    toValue
            );

            forwardToReportPage(
                    request,
                    response
            );

        } catch (Exception exception) {

            getServletContext()
                    .log(
                            "Unable to load clinic reports.",
                            exception
                    );

            request.setAttribute(
                    "error",
                    "Unable to load the clinic report at this time."
            );

            request.setAttribute(
                    "fromDate",
                    fromValue
            );

            request.setAttribute(
                    "toDate",
                    toValue
            );

            forwardToReportPage(
                    request,
                    response
            );
        }
    }

    private void forwardToReportPage(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/reports.jsp"
        ).forward(
                request,
                response
        );
    }

    private String encode(
            String value) {

        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }

    private String trimToNull(
            String value) {

        if (value == null) {
            return null;
        }

        String trimmed =
                value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }
}