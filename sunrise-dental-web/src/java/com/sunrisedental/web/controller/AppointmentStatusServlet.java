package com.sunrisedental.web.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.AppointmentViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Handles appointment lifecycle actions from the Web tier.
 *
 * Supported actions:
 * - COMPLETE
 * - CANCEL
 *
 * Security:
 * changedBy is always obtained from the authenticated
 * HTTP session and is never accepted from an editable form.
 *
 * Architecture:
 * Web -> REST API -> Service -> Repository -> MySQL
 */
@WebServlet("/appointments/status")
public class AppointmentStatusServlet
        extends HttpServlet {

    private final ApiClient apiClient;

    private final Gson gson;


    public AppointmentStatusServlet() {

        this.apiClient =
                new ApiClient();

        this.gson =
                new Gson();
    }


    // =========================================================
    // GET
    // SHOW CONFIRMATION PAGE
    // =========================================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Integer sessionUserId =
                getSessionUserId(
                        request
                );


        if (sessionUserId == null) {

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


        String action =
                normalizeAction(
                        request.getParameter(
                                "action"
                        )
                );


        try {

            int appointmentId =
                    parsePositiveInteger(
                            appointmentIdText,
                            "Appointment ID"
                    );


            validateAction(
                    action
            );


            AppointmentViewModel appointment =
                    loadAppointment(
                            appointmentId
                    );


            /*
             * Only scheduled appointments can enter
             * either lifecycle workflow.
             */
            if (!appointment.isScheduled()) {

                throw new IllegalArgumentException(
                        "Only scheduled appointments "
                        + "can be completed or cancelled."
                );
            }


            request.setAttribute(
                    "appointment",
                    appointment
            );


            request.setAttribute(
                    "statusAction",
                    action
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/appointment-status.jsp"
            ).forward(
                    request,
                    response
            );


        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/appointment-status.jsp"
            ).forward(
                    request,
                    response
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            throw new ServletException(
                    "Appointment status request was interrupted.",
                    exception
            );


        } catch (IOException exception) {

            request.setAttribute(
                    "errorMessage",
                    createFriendlyApiError(
                            exception
                    )
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/appointment-status.jsp"
            ).forward(
                    request,
                    response
            );
        }
    }


    // =========================================================
    // POST
    // COMPLETE OR CANCEL APPOINTMENT
    // =========================================================

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Integer sessionUserId =
                getSessionUserId(
                        request
                );


        if (sessionUserId == null) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/login"
            );

            return;
        }


        String appointmentIdText =
                trimToNull(
                        request.getParameter(
                                "appointmentId"
                        )
                );


        String action =
                normalizeAction(
                        request.getParameter(
                                "action"
                        )
                );


        String cancellationReason =
                trimToNull(
                        request.getParameter(
                                "cancellationReason"
                        )
                );


        try {

            int appointmentId =
                    parsePositiveInteger(
                            appointmentIdText,
                            "Appointment ID"
                    );


            validateAction(
                    action
            );


            /*
             * Load the trusted persisted appointment.
             *
             * We do NOT trust hidden form fields for patient,
             * dentist, treatment, date or time.
             */
            AppointmentViewModel appointment =
                    loadAppointment(
                            appointmentId
                    );


            if (!appointment.isScheduled()) {

                throw new IllegalArgumentException(
                        "Only scheduled appointments "
                        + "can be completed or cancelled."
                );
            }


            // =================================================
            // CANCELLATION VALIDATION
            // =================================================

            if ("cancel".equals(action)) {

                if (cancellationReason == null
                        || cancellationReason.length() < 3) {

                    throw new IllegalArgumentException(
                            "Please provide a cancellation reason."
                    );
                }


                if (cancellationReason.length() > 500) {

                    throw new IllegalArgumentException(
                            "Cancellation reason cannot exceed "
                            + "500 characters."
                    );
                }
            }


            // =================================================
            // BUILD NOTES
            // =================================================

            String updatedNotes =
                    appointment.getNotes();


            if ("cancel".equals(action)) {

                String cancellationEntry =
                        "Cancellation reason: "
                        + cancellationReason;


                if (updatedNotes == null
                        || updatedNotes.isBlank()) {

                    updatedNotes =
                            cancellationEntry;

                } else {

                    updatedNotes =
                            updatedNotes.trim()
                            + "\n"
                            + cancellationEntry;
                }
            }


            if (updatedNotes != null
                    && updatedNotes.length() > 1000) {

                throw new IllegalArgumentException(
                        "Appointment notes cannot exceed "
                        + "1000 characters."
                );
            }


            // =================================================
            // BUILD TRUSTED REST PUT REQUEST
            // =================================================

            Map<String, String> formData =
                    new LinkedHashMap<>();


            formData.put(
                    "appointmentId",
                    String.valueOf(
                            appointmentId
                    )
            );


            formData.put(
                    "patientId",
                    String.valueOf(
                            appointment.getPatientId()
                    )
            );


            formData.put(
                    "dentistId",
                    String.valueOf(
                            appointment.getDentistId()
                    )
            );


            formData.put(
                    "treatmentId",
                    String.valueOf(
                            appointment.getTreatmentId()
                    )
            );


            formData.put(
                    "appointmentDate",
                    appointment.getAppointmentDate()
            );


            formData.put(
                    "appointmentTime",
                    appointment.getAppointmentTime()
            );


            formData.put(
                    "notes",
                    updatedNotes == null
                            ? ""
                            : updatedNotes
            );


            /*
             * IMPORTANT SECURITY RULE:
             *
             * changedBy comes from the authenticated
             * session, never from an editable field.
             */
            formData.put(
                    "changedBy",
                    String.valueOf(
                            sessionUserId
                    )
            );


            if ("complete".equals(action)) {

                formData.put(
                        "status",
                        "COMPLETED"
                );

            } else {

                formData.put(
                        "status",
                        "CANCELLED"
                );
            }


            // =================================================
            // CALL REST API
            // =================================================

            apiClient.put(
                    "/api/appointments",
                    formData
            );


            // =================================================
            // SUCCESS MESSAGE
            // =================================================

            HttpSession session =
                    request.getSession();


            if ("complete".equals(action)) {

                session.setAttribute(
                        "successMessage",
                        "Appointment "
                        + appointment
                                .getAppointmentNumber()
                        + " completed successfully."
                );

            } else {

                session.setAttribute(
                        "successMessage",
                        "Appointment "
                        + appointment
                                .getAppointmentNumber()
                        + " cancelled successfully."
                );
            }


            response.sendRedirect(
                    request.getContextPath()
                    + "/appointments/details?id="
                    + appointmentId
            );


        } catch (IllegalArgumentException exception) {

            showConfirmationAgain(
                    request,
                    response,
                    appointmentIdText,
                    action,
                    exception.getMessage()
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            showConfirmationAgain(
                    request,
                    response,
                    appointmentIdText,
                    action,
                    "The appointment service request "
                    + "was interrupted."
            );


        } catch (IOException exception) {

            showConfirmationAgain(
                    request,
                    response,
                    appointmentIdText,
                    action,
                    createFriendlyApiError(
                            exception
                    )
            );
        }
    }


    // =========================================================
    // LOAD ONE APPOINTMENT
    // =========================================================

    private AppointmentViewModel loadAppointment(
            int appointmentId)
            throws IOException, InterruptedException {

        String json =
                apiClient.get(
                        "/api/appointments?id="
                        + appointmentId
                );


        AppointmentViewModel appointment =
                gson.fromJson(
                        json,
                        AppointmentViewModel.class
                );


        if (appointment == null
                || appointment.getAppointmentId() <= 0) {

            throw new IllegalArgumentException(
                    "Appointment not found."
            );
        }


        return appointment;
    }


    // =========================================================
    // REDISPLAY CONFIRMATION PAGE AFTER ERROR
    // =========================================================

    private void showConfirmationAgain(
            HttpServletRequest request,
            HttpServletResponse response,
            String appointmentIdText,
            String action,
            String errorMessage)
            throws ServletException, IOException {

        request.setAttribute(
                "errorMessage",
                errorMessage
        );


        request.setAttribute(
                "statusAction",
                action
        );


        try {

            int appointmentId =
                    parsePositiveInteger(
                            appointmentIdText,
                            "Appointment ID"
                    );


            AppointmentViewModel appointment =
                    loadAppointment(
                            appointmentId
                    );


            request.setAttribute(
                    "appointment",
                    appointment
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

        } catch (Exception ignored) {

            /*
             * Preserve original error message.
             */
        }


        request.getRequestDispatcher(
                "/WEB-INF/views/appointment-status.jsp"
        ).forward(
                request,
                response
        );
    }


    // =========================================================
    // SESSION USER
    // =========================================================

    private Integer getSessionUserId(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(
                        false
                );


        if (session == null) {

            return null;
        }


        Object value =
                session.getAttribute(
                        "userId"
                );


        if (value == null) {

            return null;
        }


        if (value instanceof Number number) {

            int userId =
                    number.intValue();


            return userId > 0
                    ? userId
                    : null;
        }


        try {

            int userId =
                    Integer.parseInt(
                            value.toString()
                                    .trim()
                    );


            return userId > 0
                    ? userId
                    : null;


        } catch (NumberFormatException exception) {

            return null;
        }
    }


    // =========================================================
    // ACTION VALIDATION
    // =========================================================

    private String normalizeAction(
            String action) {

        if (action == null) {

            return null;
        }


        String normalized =
                action
                        .trim()
                        .toLowerCase();


        return normalized.isEmpty()
                ? null
                : normalized;
    }


    private void validateAction(
            String action) {

        if (!"complete".equals(action)
                && !"cancel".equals(action)) {

            throw new IllegalArgumentException(
                    "Invalid appointment action."
            );
        }
    }


    // =========================================================
    // ID VALIDATION
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

            int parsed =
                    Integer.parseInt(
                            value.trim()
                    );


            if (parsed <= 0) {

                throw new IllegalArgumentException(
                        fieldName
                        + " must be greater than zero."
                );
            }


            return parsed;


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

            return "Unable to communicate with "
                    + "the appointment service.";
        }


        int jsonStart =
                message.indexOf(
                        "{"
                );


        if (jsonStart >= 0) {

            try {

                String jsonPart =
                        message.substring(
                                jsonStart
                        );


                JsonObject errorObject =
                        JsonParser
                                .parseString(
                                        jsonPart
                                )
                                .getAsJsonObject();


                if (errorObject.has(
                        "error"
                )
                        && !errorObject
                                .get(
                                        "error"
                                )
                                .isJsonNull()) {

                    return errorObject
                            .get(
                                    "error"
                            )
                            .getAsString();
                }


            } catch (Exception ignored) {

                /*
                 * Use original message below.
                 */
            }
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