package com.sunrisedental.web.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.sunrisedental.web.client.ApiClient;

import com.sunrisedental.web.model.AppointmentViewModel;
import com.sunrisedental.web.model.DentistViewModel;
import com.sunrisedental.web.model.TreatmentViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.time.format.DateTimeParseException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Web controller for rescheduling an existing appointment.
 *
 * Responsibilities:
 * - load complete appointment data through REST
 * - load active dentists
 * - load active treatments
 * - preserve the original patient
 * - obtain changedBy from authenticated session
 * - send appointment changes to REST API using PUT
 *
 * The Web project never accesses MySQL directly.
 */
@WebServlet("/appointments/reschedule")
public class RescheduleAppointmentServlet
        extends HttpServlet {

    private final ApiClient apiClient;

    private final Gson gson;


    public RescheduleAppointmentServlet() {

        this.apiClient =
                new ApiClient();

        this.gson =
                new Gson();
    }


    // =========================================================
    // GET - SHOW RESCHEDULE FORM
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


            /*
             * Only a scheduled appointment may be
             * rescheduled.
             */
            if (!appointment.isScheduled()) {

                throw new IllegalArgumentException(
                        "Only scheduled appointments can be rescheduled."
                );
            }


            request.setAttribute(
                    "appointment",
                    appointment
            );


            /*
             * Pre-fill the form using the current state.
             */
            preserveFormValues(
                    request,
                    String.valueOf(
                            appointment.getDentistId()
                    ),
                    String.valueOf(
                            appointment.getTreatmentId()
                    ),
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime(),
                    appointment.getNotes()
            );


            loadReferenceData(
                    request
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/appointment-reschedule.jsp"
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
                    "/WEB-INF/views/appointment-reschedule.jsp"
            ).forward(
                    request,
                    response
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            throw new ServletException(
                    "Appointment reschedule request was interrupted.",
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
                    "/WEB-INF/views/appointment-reschedule.jsp"
            ).forward(
                    request,
                    response
            );
        }
    }


    // =========================================================
    // POST - SAVE RESCHEDULED APPOINTMENT
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


        String dentistIdText =
                trimToNull(
                        request.getParameter(
                                "dentistId"
                        )
                );


        String treatmentIdText =
                trimToNull(
                        request.getParameter(
                                "treatmentId"
                        )
                );


        String appointmentDateText =
                trimToNull(
                        request.getParameter(
                                "appointmentDate"
                        )
                );


        String appointmentTimeText =
                trimToNull(
                        request.getParameter(
                                "appointmentTime"
                        )
                );


        String notes =
                trimToNull(
                        request.getParameter(
                                "notes"
                        )
                );


        /*
         * Preserve entered values if validation fails.
         */
        preserveFormValues(
                request,
                dentistIdText,
                treatmentIdText,
                appointmentDateText,
                appointmentTimeText,
                notes
        );


        try {

            int appointmentId =
                    parsePositiveInteger(
                            appointmentIdText,
                            "Appointment ID"
                    );


            int dentistId =
                    parsePositiveInteger(
                            dentistIdText,
                            "Dentist"
                    );


            int treatmentId =
                    parsePositiveInteger(
                            treatmentIdText,
                            "Treatment"
                    );


            LocalDate appointmentDate =
                    parseAppointmentDate(
                            appointmentDateText
                    );


            LocalTime appointmentTime =
                    parseAppointmentTime(
                            appointmentTimeText
                    );


            validateNotInPast(
                    appointmentDate,
                    appointmentTime
            );


            if (notes != null
                    && notes.length() > 1000) {

                throw new IllegalArgumentException(
                        "Appointment notes cannot exceed "
                        + "1000 characters."
                );
            }


            /*
             * Reload the trusted current appointment.
             *
             * Patient ID comes from the API record,
             * NOT from an editable form field.
             */
            AppointmentViewModel existingAppointment =
                    loadAppointment(
                            appointmentId
                    );


            if (!existingAppointment.isScheduled()) {

                throw new IllegalArgumentException(
                        "Only scheduled appointments can be rescheduled."
                );
            }


            /*
             * =================================================
             * BUILD REST PUT DATA
             * =================================================
             */

            Map<String, String> formData =
                    new LinkedHashMap<>();


            formData.put(
                    "appointmentId",
                    String.valueOf(
                            appointmentId
                    )
            );


            /*
             * Patient remains linked to the same appointment.
             */
            formData.put(
                    "patientId",
                    String.valueOf(
                            existingAppointment
                                    .getPatientId()
                    )
            );


            formData.put(
                    "dentistId",
                    String.valueOf(
                            dentistId
                    )
            );


            formData.put(
                    "treatmentId",
                    String.valueOf(
                            treatmentId
                    )
            );


            formData.put(
                    "appointmentDate",
                    appointmentDate.toString()
            );


            formData.put(
                    "appointmentTime",
                    appointmentTime.toString()
            );


            formData.put(
                    "notes",
                    notes == null
                            ? ""
                            : notes
            );


            /*
             * IMPORTANT:
             *
             * changedBy is obtained from the authenticated
             * session and is never editable by the user.
             */
            formData.put(
                    "changedBy",
                    String.valueOf(
                            sessionUserId
                    )
            );


            /*
             * Status is deliberately omitted.
             *
             * AppointmentService preserves SCHEDULED.
             */
            apiClient.put(
                    "/api/appointments",
                    formData
            );


            /*
             * =================================================
             * SUCCESS
             * =================================================
             */

            HttpSession session =
                    request.getSession();


            session.setAttribute(
                    "successMessage",
                    "Appointment "
                    + existingAppointment
                            .getAppointmentNumber()
                    + " rescheduled successfully."
            );


            /*
             * Redirect to complete appointment details.
             */
            response.sendRedirect(
                    request.getContextPath()
                    + "/appointments/details?id="
                    + appointmentId
            );


        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );


            showFormAgain(
                    request,
                    response,
                    appointmentIdText
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            request.setAttribute(
                    "errorMessage",
                    "The appointment service request "
                    + "was interrupted."
            );


            showFormAgain(
                    request,
                    response,
                    appointmentIdText
            );


        } catch (IOException exception) {

            /*
             * API validation errors such as:
             *
             * - dentist conflict
             * - inactive dentist
             * - inactive treatment
             * - past schedule
             */
            request.setAttribute(
                    "errorMessage",
                    createFriendlyApiError(
                            exception
                    )
            );


            showFormAgain(
                    request,
                    response,
                    appointmentIdText
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
                || appointment
                        .getAppointmentId() <= 0) {

            throw new IllegalArgumentException(
                    "Appointment not found."
            );
        }


        return appointment;
    }


    // =========================================================
    // LOAD ACTIVE DENTISTS / TREATMENTS
    // =========================================================

    private void loadReferenceData(
            HttpServletRequest request)
            throws IOException, InterruptedException {

        /*
         * =====================================================
         * ACTIVE DENTISTS
         * =====================================================
         */

        String dentistJson =
                apiClient.get(
                        "/api/dentists"
                );


        DentistViewModel[] dentistArray =
                gson.fromJson(
                        dentistJson,
                        DentistViewModel[].class
                );


        List<DentistViewModel> dentists;


        if (dentistArray == null) {

            dentists =
                    Collections.emptyList();

        } else {

            dentists =
                    Arrays.stream(
                            dentistArray
                    )
                    .filter(
                            DentistViewModel::isActive
                    )
                    .sorted(
                            Comparator.comparing(
                                    dentist ->
                                            safeText(
                                                    dentist.getFullName()
                                            ),
                                    String.CASE_INSENSITIVE_ORDER
                            )
                    )
                    .toList();
        }


        /*
         * =====================================================
         * ACTIVE TREATMENTS
         * =====================================================
         */

        String treatmentJson =
                apiClient.get(
                        "/api/treatments"
                );


        TreatmentViewModel[] treatmentArray =
                gson.fromJson(
                        treatmentJson,
                        TreatmentViewModel[].class
                );


        List<TreatmentViewModel> treatments;


        if (treatmentArray == null) {

            treatments =
                    Collections.emptyList();

        } else {

            treatments =
                    Arrays.stream(
                            treatmentArray
                    )
                    .filter(
                            TreatmentViewModel::isActive
                    )
                    .sorted(
                            Comparator.comparing(
                                    treatment ->
                                            safeText(
                                                    treatment
                                                            .getTreatmentName()
                                            ),
                                    String.CASE_INSENSITIVE_ORDER
                            )
                    )
                    .toList();
        }


        request.setAttribute(
                "dentists",
                dentists
        );


        request.setAttribute(
                "treatments",
                treatments
        );
    }


    // =========================================================
    // SHOW FORM AGAIN
    // =========================================================

    private void showFormAgain(
            HttpServletRequest request,
            HttpServletResponse response,
            String appointmentIdText)
            throws ServletException, IOException {

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


            loadReferenceData(
                    request
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            throw new ServletException(
                    "Unable to reload appointment reschedule data.",
                    exception
            );


        } catch (Exception exception) {

            /*
             * Keep original error if already available.
             */
            if (request.getAttribute(
                    "errorMessage"
            ) == null) {

                request.setAttribute(
                        "errorMessage",
                        "Unable to reload appointment information."
                );
            }


            request.setAttribute(
                    "dentists",
                    Collections.emptyList()
            );


            request.setAttribute(
                    "treatments",
                    Collections.emptyList()
            );
        }


        request.getRequestDispatcher(
                "/WEB-INF/views/appointment-reschedule.jsp"
        ).forward(
                request,
                response
        );
    }


    // =========================================================
    // PRESERVE FORM VALUES
    // =========================================================

    private void preserveFormValues(
            HttpServletRequest request,
            String dentistId,
            String treatmentId,
            String appointmentDate,
            String appointmentTime,
            String notes) {

        request.setAttribute(
                "formDentistId",
                dentistId
        );


        request.setAttribute(
                "formTreatmentId",
                treatmentId
        );


        request.setAttribute(
                "formAppointmentDate",
                appointmentDate
        );


        request.setAttribute(
                "formAppointmentTime",
                appointmentTime
        );


        request.setAttribute(
                "formNotes",
                notes
        );
    }


    // =========================================================
    // SESSION USER ID
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
    // VALIDATION
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
                            value
                    );


            if (parsed <= 0) {

                throw new IllegalArgumentException(
                        fieldName
                        + " selection is invalid."
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


    private LocalDate parseAppointmentDate(
            String value) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }


        try {

            return LocalDate.parse(
                    value
            );


        } catch (DateTimeParseException exception) {

            throw new IllegalArgumentException(
                    "Appointment date is invalid."
            );
        }
    }


    private LocalTime parseAppointmentTime(
            String value) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    "Appointment time is required."
            );
        }


        try {

            return LocalTime.parse(
                    value
            );


        } catch (DateTimeParseException exception) {

            throw new IllegalArgumentException(
                    "Appointment time is invalid."
            );
        }
    }


    private void validateNotInPast(
            LocalDate date,
            LocalTime time) {

        LocalDateTime dateTime =
                LocalDateTime.of(
                        date,
                        time
                );


        if (dateTime.isBefore(
                LocalDateTime.now()
        )) {

            throw new IllegalArgumentException(
                    "Appointment date and time cannot be in the past."
            );
        }
    }


    // =========================================================
    // FRIENDLY REST ERROR
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
                 * Fall through to original message.
                 */
            }
        }


        return message;
    }


    // =========================================================
    // STRING HELPERS
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


    private String safeText(
            String value) {

        return value == null
                ? ""
                : value.trim();
    }
}