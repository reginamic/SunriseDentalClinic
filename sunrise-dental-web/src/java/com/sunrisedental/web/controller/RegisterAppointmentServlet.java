package com.sunrisedental.web.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.DentistViewModel;
import com.sunrisedental.web.model.PatientViewModel;
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
 * Web controller for registering dental appointments.
 *
 * Presentation Tier responsibilities:
 * - load patients from REST API
 * - load ACTIVE dentists from REST API
 * - load ACTIVE treatments from REST API
 * - validate basic form input
 * - obtain createdBy from authenticated session
 * - send appointment registration to REST API
 *
 * This servlet never communicates directly with MySQL.
 */
@WebServlet("/appointments/register")
public class RegisterAppointmentServlet
        extends HttpServlet {

    private final ApiClient apiClient;
    private final Gson gson;


    public RegisterAppointmentServlet() {

        this.apiClient =
                new ApiClient();

        this.gson =
                new Gson();
    }


    // =========================================================
    // GET - DISPLAY REGISTRATION FORM
    // =========================================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * AuthenticationFilter should already protect
         * appointment routes.
         *
         * This check is additional defensive protection.
         */
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


        try {

            loadReferenceData(
                    request
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/appointment-form.jsp"
            ).forward(
                    request,
                    response
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            throw new ServletException(
                    "Appointment registration request was interrupted.",
                    exception
            );


        } catch (IOException exception) {

            /*
             * Allow the form page to show a professional
             * error instead of exposing a stack trace.
             */
            request.setAttribute(
                    "errorMessage",
                    createFriendlyApiError(
                            exception
                    )
            );


            setEmptyReferenceData(
                    request
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/appointment-form.jsp"
            ).forward(
                    request,
                    response
            );
        }
    }


    // =========================================================
    // POST - REGISTER APPOINTMENT
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


        /*
         * Read submitted values.
         */
        String patientIdText =
                trimToNull(
                        request.getParameter(
                                "patientId"
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
         * Preserve entered form data when validation fails.
         */
        preserveFormValues(
                request,
                patientIdText,
                dentistIdText,
                treatmentIdText,
                appointmentDateText,
                appointmentTimeText,
                notes
        );


        try {

            /*
             * =================================================
             * WEB-TIER BASIC VALIDATION
             * =================================================
             *
             * The REST API remains the authoritative validation
             * layer and will still execute the full
             * Chain of Responsibility.
             */

            int patientId =
                    parsePositiveInteger(
                            patientIdText,
                            "Patient"
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
             * =================================================
             * BUILD REST FORM DATA
             * =================================================
             *
             * IMPORTANT:
             *
             * createdBy comes from the authenticated session.
             * The user cannot type or manipulate creator ID
             * through the HTML form.
             */

            Map<String, String> formData =
                    new LinkedHashMap<>();


            formData.put(
                    "patientId",
                    String.valueOf(
                            patientId
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


            formData.put(
                    "createdBy",
                    String.valueOf(
                            sessionUserId
                    )
            );


            /*
             * =================================================
             * REST API CALL
             * =================================================
             */

            String jsonResponse =
                    apiClient.post(
                            "/api/appointments",
                            formData
                    );


            String appointmentNumber =
                    extractAppointmentNumber(
                            jsonResponse
                    );


            /*
             * Flash success message.
             */
            HttpSession session =
                    request.getSession();


            if (appointmentNumber != null
                    && !appointmentNumber.isBlank()) {

                session.setAttribute(
                        "successMessage",
                        "Appointment "
                        + appointmentNumber
                        + " registered successfully."
                );

            } else {

                session.setAttribute(
                        "successMessage",
                        "Appointment registered successfully."
                );
            }


            /*
             * Post/Redirect/Get prevents duplicate submission
             * when the browser refreshes.
             */
            response.sendRedirect(
                    request.getContextPath()
                    + "/appointments"
            );


        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );


            showRegistrationFormAgain(
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


            showRegistrationFormAgain(
                    request,
                    response
            );


        } catch (IOException exception) {

            /*
             * Includes API validation failures such as:
             *
             * - inactive dentist
             * - inactive treatment
             * - overlapping appointment
             * - missing patient
             * - past appointment
             */
            request.setAttribute(
                    "errorMessage",
                    createFriendlyApiError(
                            exception
                    )
            );


            showRegistrationFormAgain(
                    request,
                    response
            );
        }
    }


    // =========================================================
    // LOAD PATIENT / DENTIST / TREATMENT DATA
    // =========================================================

    private void loadReferenceData(
            HttpServletRequest request)
            throws IOException, InterruptedException {

        /*
         * =====================================================
         * PATIENTS
         * =====================================================
         */

        String patientJson =
                apiClient.get(
                        "/api/patients"
                );


        PatientViewModel[] patientArray =
                gson.fromJson(
                        patientJson,
                        PatientViewModel[].class
                );


        List<PatientViewModel> patients;


        if (patientArray == null) {

            patients =
                    Collections.emptyList();

        } else {

            patients =
                    Arrays.stream(
                            patientArray
                    )
                    .sorted(
                            Comparator.comparing(
                                    patient ->
                                            safeText(
                                                    patient.getFullName()
                                            ),
                                    String.CASE_INSENSITIVE_ORDER
                            )
                    )
                    .toList();
        }


        /*
         * =====================================================
         * DENTISTS
         * =====================================================
         *
         * Only ACTIVE dentists may be used for
         * new appointments.
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
         * TREATMENTS
         * =====================================================
         *
         * Only ACTIVE treatments may be used for
         * new appointments.
         *
         * Therefore an inactive treatment such as TRT-008
         * will not appear in the registration form.
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
                                                    treatment.getTreatmentName()
                                            ),
                                    String.CASE_INSENSITIVE_ORDER
                            )
                    )
                    .toList();
        }


        request.setAttribute(
                "patients",
                patients
        );


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
    // SHOW FORM AGAIN AFTER VALIDATION ERROR
    // =========================================================

    private void showRegistrationFormAgain(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            loadReferenceData(
                    request
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            throw new ServletException(
                    "Unable to reload appointment "
                    + "registration data.",
                    exception
            );


        } catch (IOException exception) {

            /*
             * Preserve the original validation error if one
             * already exists.
             */
            if (request.getAttribute(
                    "errorMessage"
            ) == null) {

                request.setAttribute(
                        "errorMessage",
                        createFriendlyApiError(
                                exception
                        )
                );
            }


            setEmptyReferenceData(
                    request
            );
        }


        request.getRequestDispatcher(
                "/WEB-INF/views/appointment-form.jsp"
        ).forward(
                request,
                response
        );
    }


    // =========================================================
    // EMPTY REFERENCE DATA
    // =========================================================

    private void setEmptyReferenceData(
            HttpServletRequest request) {

        request.setAttribute(
                "patients",
                Collections.emptyList()
        );


        request.setAttribute(
                "dentists",
                Collections.emptyList()
        );


        request.setAttribute(
                "treatments",
                Collections.emptyList()
        );
    }


    // =========================================================
    // PRESERVE FORM VALUES
    // =========================================================

    private void preserveFormValues(
            HttpServletRequest request,
            String patientId,
            String dentistId,
            String treatmentId,
            String appointmentDate,
            String appointmentTime,
            String notes) {

        request.setAttribute(
                "formPatientId",
                patientId
        );


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
    // GET SESSION USER ID
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


        Object userIdValue =
                session.getAttribute(
                        "userId"
                );


        if (userIdValue == null) {

            return null;
        }


        if (userIdValue instanceof Number number) {

            int userId =
                    number.intValue();


            return userId > 0
                    ? userId
                    : null;
        }


        try {

            int userId =
                    Integer.parseInt(
                            userIdValue
                                    .toString()
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
                        + " selection is invalid."
                );
            }


            return parsedValue;


        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    fieldName
                    + " selection is invalid."
            );
        }
    }


    // =========================================================
    // DATE VALIDATION
    // =========================================================

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


    // =========================================================
    // TIME VALIDATION
    // =========================================================

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


    // =========================================================
    // PAST DATE/TIME VALIDATION
    // =========================================================

    private void validateNotInPast(
            LocalDate appointmentDate,
            LocalTime appointmentTime) {

        LocalDateTime appointmentDateTime =
                LocalDateTime.of(
                        appointmentDate,
                        appointmentTime
                );


        if (appointmentDateTime.isBefore(
                LocalDateTime.now()
        )) {

            throw new IllegalArgumentException(
                    "Appointment date and time "
                    + "cannot be in the past."
            );
        }
    }


    // =========================================================
    // EXTRACT GENERATED APPOINTMENT NUMBER
    // =========================================================

    private String extractAppointmentNumber(
            String jsonResponse) {

        if (jsonResponse == null
                || jsonResponse.isBlank()) {

            return null;
        }


        try {

            JsonObject root =
                    JsonParser.parseString(
                            jsonResponse
                    )
                    .getAsJsonObject();


            if (!root.has(
                    "appointment"
            )
                    || root.get(
                            "appointment"
                    ).isJsonNull()) {

                return null;
            }


            JsonObject appointment =
                    root.getAsJsonObject(
                            "appointment"
                    );


            if (!appointment.has(
                    "appointmentNumber"
            )
                    || appointment.get(
                            "appointmentNumber"
                    ).isJsonNull()) {

                return null;
            }


            return appointment.get(
                    "appointmentNumber"
            ).getAsString();


        } catch (Exception exception) {

            /*
             * Registration may still be successful even
             * if the response format changes slightly.
             */
            return null;
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


        /*
         * ApiClient error format:
         *
         * API request failed. HTTP 400:
         * {"error":"Selected treatment is inactive."}
         *
         * Extract the useful API error for the user.
         */
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
                        JsonParser.parseString(
                                jsonPart
                        )
                        .getAsJsonObject();


                if (errorObject.has(
                        "error"
                )
                        && !errorObject.get(
                                "error"
                        ).isJsonNull()) {

                    return errorObject.get(
                            "error"
                    ).getAsString();
                }


            } catch (Exception ignored) {

                /*
                 * Fall back to original message.
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