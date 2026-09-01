package com.sunrisedental.web.controller;

import com.google.gson.Gson;

import com.sunrisedental.web.client.ApiClient;

import com.sunrisedental.web.model.AppointmentHistoryViewModel;
import com.sunrisedental.web.model.AppointmentViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Web controller for viewing and managing
 * one complete dental appointment.
 *
 * Responsibilities:
 * - load enriched appointment details
 * - load real Memento appointment history
 * - forward both datasets to the JSP
 *
 * The Web tier communicates only with REST APIs.
 */
@WebServlet("/appointments/details")
public class AppointmentDetailsServlet
        extends HttpServlet {

    private final ApiClient apiClient;

    private final Gson gson;


    public AppointmentDetailsServlet() {

        this.apiClient =
                new ApiClient();

        this.gson =
                new Gson();
    }


    // =========================================================
    // GET
    // =========================================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(
                        false
                );


        if (session == null
                || session.getAttribute(
                        "userId"
                ) == null) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/login"
            );

            return;
        }


        String appointmentIdText =
                trimToNull(
                        request.getParameter(
                                "id"
                        )
                );


        try {

            int appointmentId =
                    parsePositiveInteger(
                            appointmentIdText,
                            "Appointment ID"
                    );


            // =================================================
            // LOAD COMPLETE APPOINTMENT DETAILS
            // =================================================

            String appointmentJson =
                    apiClient.get(
                            "/api/appointments?id="
                            + appointmentId
                    );


            AppointmentViewModel appointment =
                    gson.fromJson(
                            appointmentJson,
                            AppointmentViewModel.class
                    );


            if (appointment == null
                    || appointment
                            .getAppointmentId() <= 0) {

                throw new IllegalArgumentException(
                        "Appointment not found."
                );
            }


            request.setAttribute(
                    "appointment",
                    appointment
            );


            // =================================================
            // LOAD REAL MEMENTO HISTORY
            // =================================================

            List<AppointmentHistoryViewModel> history =
                    loadAppointmentHistory(
                            appointmentId
                    );


            request.setAttribute(
                    "appointmentHistory",
                    history
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/appointment-details.jsp"
            ).forward(
                    request,
                    response
            );


        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );


            request.setAttribute(
                    "appointmentHistory",
                    Collections.emptyList()
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/appointment-details.jsp"
            ).forward(
                    request,
                    response
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            request.setAttribute(
                    "errorMessage",
                    "The appointment service request "
                    + "was interrupted."
            );


            request.setAttribute(
                    "appointmentHistory",
                    Collections.emptyList()
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/appointment-details.jsp"
            ).forward(
                    request,
                    response
            );


        } catch (IOException exception) {

            request.setAttribute(
                    "errorMessage",
                    createFriendlyApiError(
                            exception
                    )
            );


            request.setAttribute(
                    "appointmentHistory",
                    Collections.emptyList()
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/appointment-details.jsp"
            ).forward(
                    request,
                    response
            );
        }
    }


    // =========================================================
    // LOAD APPOINTMENT HISTORY
    // =========================================================

    private List<AppointmentHistoryViewModel>
            loadAppointmentHistory(
                    int appointmentId)
            throws IOException, InterruptedException {

        String historyJson =
                apiClient.get(
                        "/api/appointment-history?appointmentId="
                        + appointmentId
                );


        AppointmentHistoryViewModel[] historyArray =
                gson.fromJson(
                        historyJson,
                        AppointmentHistoryViewModel[].class
                );


        if (historyArray == null) {

            return Collections.emptyList();
        }


        return Arrays.asList(
                historyArray
        );
    }


    // =========================================================
    // POSITIVE INTEGER VALIDATION
    // =========================================================

    private int parsePositiveInteger(
            String value,
            String fieldName) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    fieldName
                    + " is required."
            );
        }


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


    // =========================================================
    // FRIENDLY API ERROR
    // =========================================================

    private String createFriendlyApiError(
            IOException exception) {

        String message =
                exception.getMessage();


        if (message == null
                || message.isBlank()) {

            return "Unable to retrieve appointment details.";
        }


        if (message.contains(
                "Appointment not found"
        )) {

            return "Appointment not found.";
        }


        return message;
    }


    // =========================================================
    // STRING HELPER
    // =========================================================

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