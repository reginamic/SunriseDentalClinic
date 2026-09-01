package com.sunrisedental.web.controller;

import com.google.gson.Gson;

import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.AppointmentViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Web controller for Appointment Management.
 *
 * This servlet belongs to the Presentation Tier.
 *
 * It never accesses MySQL directly. All appointment data
 * is retrieved from the Sunrise Dental REST API using
 * HTTP/JSON through ApiClient.
 */
@WebServlet("/appointments")
public class AppointmentsServlet extends HttpServlet {

    private final ApiClient apiClient;
    private final Gson gson;

    public AppointmentsServlet() {

        this.apiClient =
                new ApiClient();

        this.gson =
                new Gson();
    }

    /*
     * =========================================================
     * GET - LIST / SEARCH APPOINTMENTS
     * =========================================================
     */

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String appointmentNumber =
                    trimToNull(
                            request.getParameter(
                                    "appointmentNumber"
                            )
                    );

            String appointmentDate =
                    trimToNull(
                            request.getParameter(
                                    "appointmentDate"
                            )
                    );

            String patientIdText =
                    trimToNull(
                            request.getParameter(
                                    "patientId"
                            )
                    );

            List<AppointmentViewModel> appointments;

            /*
             * =================================================
             * EXACT APPOINTMENT NUMBER SEARCH
             * =================================================
             *
             * Appointment number search has highest priority.
             */
            if (appointmentNumber != null) {

                appointments =
                        searchByAppointmentNumber(
                                appointmentNumber
                        );

            /*
             * =================================================
             * DATE SEARCH
             * =================================================
             */
            } else if (appointmentDate != null) {

                appointments =
                        searchByDate(
                                appointmentDate
                        );

            /*
             * =================================================
             * PATIENT SEARCH
             * =================================================
             */
            } else if (patientIdText != null) {

                appointments =
                        searchByPatient(
                                patientIdText
                        );

            /*
             * =================================================
             * ALL APPOINTMENTS
             * =================================================
             */
            } else {

                appointments =
                        getAllAppointments();
            }

            /*
             * Optional Web-side status filter.
             *
             * The REST API currently supplies appointment
             * search by number/date/patient. Status filtering
             * is safe to perform over the returned REST data.
             */
            String status =
                    trimToNull(
                            request.getParameter(
                                    "status"
                            )
                    );

            if (status != null) {

                appointments =
                        appointments.stream()
                                .filter(
                                        appointment ->
                                                appointment
                                                        .getStatus()
                                                        != null
                                                && appointment
                                                        .getStatus()
                                                        .equalsIgnoreCase(
                                                                status
                                                        )
                                )
                                .toList();
            }

            /*
             * =================================================
             * DASHBOARD STATISTICS
             * =================================================
             */

            long totalAppointments =
                    appointments.size();

            long scheduledAppointments =
                    appointments.stream()
                            .filter(
                                    AppointmentViewModel::isScheduled
                            )
                            .count();

            long completedAppointments =
                    appointments.stream()
                            .filter(
                                    AppointmentViewModel::isCompleted
                            )
                            .count();

            long cancelledAppointments =
                    appointments.stream()
                            .filter(
                                    AppointmentViewModel::isCancelled
                            )
                            .count();

            /*
             * =================================================
             * JSP ATTRIBUTES
             * =================================================
             */

            request.setAttribute(
                    "appointments",
                    appointments
            );

            request.setAttribute(
                    "totalAppointments",
                    totalAppointments
            );

            request.setAttribute(
                    "scheduledAppointments",
                    scheduledAppointments
            );

            request.setAttribute(
                    "completedAppointments",
                    completedAppointments
            );

            request.setAttribute(
                    "cancelledAppointments",
                    cancelledAppointments
            );

            /*
             * Keep filters visible in the form after searching.
             */
            request.setAttribute(
                    "selectedAppointmentNumber",
                    appointmentNumber
            );

            request.setAttribute(
                    "selectedAppointmentDate",
                    appointmentDate
            );

            request.setAttribute(
                    "selectedPatientId",
                    patientIdText
            );

            request.setAttribute(
                    "selectedStatus",
                    status
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/appointments.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            showEmptyPage(
                    request,
                    response
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            request.setAttribute(
                    "errorMessage",
                    "The appointment service request was interrupted."
            );

            showEmptyPage(
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

            showEmptyPage(
                    request,
                    response
            );
        }
    }

    /*
     * =========================================================
     * GET ALL APPOINTMENTS
     * =========================================================
     */

    private List<AppointmentViewModel>
            getAllAppointments()
            throws IOException, InterruptedException {

        String json =
                apiClient.get(
                        "/api/appointments"
                );

        AppointmentViewModel[] result =
                gson.fromJson(
                        json,
                        AppointmentViewModel[].class
                );

        if (result == null) {

            return Collections.emptyList();
        }

        return Arrays.asList(
                result
        );
    }

    /*
     * =========================================================
     * EXACT APPOINTMENT NUMBER SEARCH
     * =========================================================
     */

    private List<AppointmentViewModel>
            searchByAppointmentNumber(
                    String appointmentNumber)
            throws IOException, InterruptedException {

        String encodedAppointmentNumber =
                URLEncoder.encode(
                        appointmentNumber.trim(),
                        StandardCharsets.UTF_8
                );

        String json =
                apiClient.get(
                        "/api/appointments"
                        + "?appointmentNumber="
                        + encodedAppointmentNumber
                );

        /*
         * Exact appointment-number search returns one
         * appointment object rather than an array.
         */
        AppointmentViewModel appointment =
                gson.fromJson(
                        json,
                        AppointmentViewModel.class
                );

        if (appointment == null
                || appointment.getAppointmentId() <= 0) {

            return Collections.emptyList();
        }

        return List.of(
                appointment
        );
    }

    /*
     * =========================================================
     * DATE SEARCH
     * =========================================================
     */

    private List<AppointmentViewModel>
            searchByDate(
                    String appointmentDate)
            throws IOException, InterruptedException {

        String encodedDate =
                URLEncoder.encode(
                        appointmentDate,
                        StandardCharsets.UTF_8
                );

        String json =
                apiClient.get(
                        "/api/appointments"
                        + "?date="
                        + encodedDate
                );

        AppointmentViewModel[] result =
                gson.fromJson(
                        json,
                        AppointmentViewModel[].class
                );

        if (result == null) {

            return Collections.emptyList();
        }

        return Arrays.asList(
                result
        );
    }

    /*
     * =========================================================
     * PATIENT SEARCH
     * =========================================================
     */

    private List<AppointmentViewModel>
            searchByPatient(
                    String patientIdText)
            throws IOException, InterruptedException {

        int patientId;

        try {

            patientId =
                    Integer.parseInt(
                            patientIdText
                    );

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "Patient ID must be a valid number."
            );
        }

        if (patientId <= 0) {

            throw new IllegalArgumentException(
                    "Patient ID must be greater than zero."
            );
        }

        String json =
                apiClient.get(
                        "/api/appointments"
                        + "?patientId="
                        + patientId
                );

        AppointmentViewModel[] result =
                gson.fromJson(
                        json,
                        AppointmentViewModel[].class
                );

        if (result == null) {

            return Collections.emptyList();
        }

        return Arrays.asList(
                result
        );
    }

    /*
     * =========================================================
     * ERROR PAGE PREPARATION
     * =========================================================
     */

    private void showEmptyPage(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute(
                "appointments",
                Collections.emptyList()
        );

        request.setAttribute(
                "totalAppointments",
                0L
        );

        request.setAttribute(
                "scheduledAppointments",
                0L
        );

        request.setAttribute(
                "completedAppointments",
                0L
        );

        request.setAttribute(
                "cancelledAppointments",
                0L
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/appointments.jsp"
        ).forward(
                request,
                response
        );
    }

    /*
     * =========================================================
     * FRIENDLY API ERROR
     * =========================================================
     */

    private String createFriendlyApiError(
            IOException exception) {

        String message =
                exception.getMessage();

        if (message == null
                || message.isBlank()) {

            return "Unable to communicate with the appointment service.";
        }

        /*
         * Preserve useful API validation messages while
         * preventing the JSP from receiving a large stack trace.
         */
        return message;
    }

    /*
     * =========================================================
     * GENERAL HELPER
     * =========================================================
     */

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