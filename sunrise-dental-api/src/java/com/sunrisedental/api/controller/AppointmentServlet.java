package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.AppointmentStatus;
import com.sunrisedental.api.pattern.builder.AppointmentBuilder;
import com.sunrisedental.api.service.AppointmentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/appointments")
public class AppointmentServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE =
            "application/json";

    private static final String CHARACTER_ENCODING =
            "UTF-8";

    private final AppointmentService appointmentService =
            new AppointmentService();

    /*
     * =========================================================
     * GET
     * =========================================================
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {

            String appointmentNumber =
                    getTrimmedParameter(
                            request,
                            "appointmentNumber"
                    );

            String dateParameter =
                    getTrimmedParameter(
                            request,
                            "date"
                    );

            String patientIdParameter =
                    getTrimmedParameter(
                            request,
                            "patientId"
                    );

            /*
             * Search by appointment number.
             */
            if (appointmentNumber != null
                    && !appointmentNumber.isBlank()) {

                Appointment appointment =
                        appointmentService
                                .getAppointmentByNumber(
                                        appointmentNumber
                                )
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Appointment not found."
                                        )
                                );

                writeAppointmentJson(
                        response,
                        appointment
                );

                return;
            }

            /*
             * Filter by appointment date.
             */
            if (dateParameter != null
                    && !dateParameter.isBlank()) {

                LocalDate appointmentDate =
                        parseDate(
                                dateParameter
                        );

                List<Appointment> appointments =
                        appointmentService
                                .getAppointmentsByDate(
                                        appointmentDate
                                );

                writeAppointmentList(
                        response,
                        appointments
                );

                return;
            }

            /*
             * Filter by patient.
             */
            if (patientIdParameter != null
                    && !patientIdParameter.isBlank()) {

                int patientId =
                        parsePositiveInteger(
                                patientIdParameter,
                                "Patient ID"
                        );

                List<Appointment> appointments =
                        appointmentService
                                .getAppointmentsByPatient(
                                        patientId
                                );

                writeAppointmentList(
                        response,
                        appointments
                );

                return;
            }

            /*
             * Return all appointments.
             */
            List<Appointment> appointments =
                    appointmentService
                            .getAllAppointments();

            writeAppointmentList(
                    response,
                    appointments
            );

        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (SQLException exception) {

            logError(
                    "Unable to retrieve appointments.",
                    exception
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve appointments."
            );
        }
    }

    /*
     * =========================================================
     * POST
     * =========================================================
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {

            Appointment appointment =
                    buildAppointmentFromRequest(
                            request,
                            true
                    );

            Appointment savedAppointment =
                    appointmentService
                            .createAppointment(
                                    appointment
                            );

            response.setStatus(
                    HttpServletResponse.SC_CREATED
            );

            writeMessageWithAppointment(
                    response,
                    "Appointment created successfully.",
                    savedAppointment
            );

        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (SQLException exception) {

            logError(
                    "Unable to create appointment.",
                    exception
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to create appointment."
            );
        }
    }

    /*
     * =========================================================
     * PUT
     * =========================================================
     */
    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {

            /*
             * Tomcat does not reliably expose form-urlencoded
             * PUT parameters through request.getParameter().
             *
             * Therefore the PUT body is manually parsed.
             */
            Map<String, String> parameters =
                    parseFormEncodedBody(
                            request
                    );

            int appointmentId =
                    parsePositiveInteger(
                            getTrimmedParameter(
                                    parameters,
                                    "appointmentId"
                            ),
                            "Appointment ID"
                    );

            /*
             * User performing this change.
             *
             * changedBy is intentionally separate from createdBy.
             */
            int changedBy =
                    parsePositiveInteger(
                            getTrimmedParameter(
                                    parameters,
                                    "changedBy"
                            ),
                            "Changed By User ID"
                    );

            Appointment appointment =
                    buildAppointmentFromParameters(
                            parameters
                    );

            appointment.setAppointmentId(
                    appointmentId
            );

            /*
             * If status is missing, AppointmentService
             * preserves the existing status.
             */
            String statusParameter =
                    getTrimmedParameter(
                            parameters,
                            "status"
                    );

            if (statusParameter == null
                    || statusParameter.isBlank()) {

                appointment.setStatus(null);
            }

            /*
             * Memento-aware update.
             */
            boolean updated =
                    appointmentService
                            .updateAppointment(
                                    appointment,
                                    changedBy
                            );

            if (!updated) {

                sendErrorResponse(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Appointment not found."
                );

                return;
            }

            writeMessageWithAppointment(
                    response,
                    "Appointment updated successfully.",
                    appointment
            );

        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (SQLException exception) {

            logError(
                    "Unable to update appointment.",
                    exception
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update appointment."
            );
        }
    }

    /*
     * =========================================================
     * BUILD APPOINTMENT FROM POST REQUEST
     * =========================================================
     */
    private Appointment buildAppointmentFromRequest(
            HttpServletRequest request,
            boolean requireCreatedBy) {

        int patientId =
                parsePositiveInteger(
                        getTrimmedParameter(
                                request,
                                "patientId"
                        ),
                        "Patient ID"
                );

        int dentistId =
                parsePositiveInteger(
                        getTrimmedParameter(
                                request,
                                "dentistId"
                        ),
                        "Dentist ID"
                );

        int treatmentId =
                parsePositiveInteger(
                        getTrimmedParameter(
                                request,
                                "treatmentId"
                        ),
                        "Treatment ID"
                );

        LocalDate appointmentDate =
                parseDate(
                        getTrimmedParameter(
                                request,
                                "appointmentDate"
                        )
                );

        LocalTime appointmentTime =
                parseTime(
                        getTrimmedParameter(
                                request,
                                "appointmentTime"
                        )
                );

        String notes =
                getTrimmedParameter(
                        request,
                        "notes"
                );

        String statusParameter =
                getTrimmedParameter(
                        request,
                        "status"
                );

        AppointmentBuilder builder =
                new AppointmentBuilder()
                        .patientId(patientId)
                        .dentistId(dentistId)
                        .treatmentId(treatmentId)
                        .appointmentDate(
                                appointmentDate
                        )
                        .appointmentTime(
                                appointmentTime
                        )
                        .notes(notes);

        if (statusParameter != null
                && !statusParameter.isBlank()) {

            builder.status(
                    AppointmentStatus.fromString(
                            statusParameter
                    )
            );
        }

        if (requireCreatedBy) {

            int createdBy =
                    parsePositiveInteger(
                            getTrimmedParameter(
                                    request,
                                    "createdBy"
                            ),
                            "Creator user ID"
                    );

            builder.createdBy(
                    createdBy
            );
        }

        return builder.build();
    }

    /*
     * =========================================================
     * BUILD APPOINTMENT FROM PUT PARAMETERS
     * =========================================================
     */
    private Appointment buildAppointmentFromParameters(
            Map<String, String> parameters) {

        int patientId =
                parsePositiveInteger(
                        getTrimmedParameter(
                                parameters,
                                "patientId"
                        ),
                        "Patient ID"
                );

        int dentistId =
                parsePositiveInteger(
                        getTrimmedParameter(
                                parameters,
                                "dentistId"
                        ),
                        "Dentist ID"
                );

        int treatmentId =
                parsePositiveInteger(
                        getTrimmedParameter(
                                parameters,
                                "treatmentId"
                        ),
                        "Treatment ID"
                );

        LocalDate appointmentDate =
                parseDate(
                        getTrimmedParameter(
                                parameters,
                                "appointmentDate"
                        )
                );

        LocalTime appointmentTime =
                parseTime(
                        getTrimmedParameter(
                                parameters,
                                "appointmentTime"
                        )
                );

        String notes =
                getTrimmedParameter(
                        parameters,
                        "notes"
                );

        String statusParameter =
                getTrimmedParameter(
                        parameters,
                        "status"
                );

        AppointmentBuilder builder =
                new AppointmentBuilder()
                        .patientId(patientId)
                        .dentistId(dentistId)
                        .treatmentId(treatmentId)
                        .appointmentDate(
                                appointmentDate
                        )
                        .appointmentTime(
                                appointmentTime
                        )
                        .notes(notes);

        if (statusParameter != null
                && !statusParameter.isBlank()) {

            builder.status(
                    AppointmentStatus.fromString(
                            statusParameter
                    )
            );
        }

        /*
         * createdBy is intentionally not supplied.
         *
         * AppointmentService preserves the original creator.
         */
        return builder.build();
    }

    /*
     * =========================================================
     * PUT FORM BODY PARSER
     * =========================================================
     */
    private Map<String, String> parseFormEncodedBody(
            HttpServletRequest request)
            throws IOException {

        Map<String, String> parameters =
                new HashMap<>();

        StringBuilder body =
                new StringBuilder();

        try (
            BufferedReader reader =
                    request.getReader()
        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                body.append(line);
            }
        }

        if (body.length() == 0) {

            return parameters;
        }

        String[] pairs =
                body
                        .toString()
                        .split("&");

        for (String pair : pairs) {

            if (pair == null
                    || pair.isBlank()) {

                continue;
            }

            String[] parts =
                    pair.split(
                            "=",
                            2
                    );

            String key =
                    URLDecoder.decode(
                            parts[0],
                            StandardCharsets.UTF_8
                    );

            String value =
                    parts.length > 1
                            ? URLDecoder.decode(
                                    parts[1],
                                    StandardCharsets.UTF_8
                            )
                            : "";

            parameters.put(
                    key,
                    value
            );
        }

        return parameters;
    }

    /*
     * =========================================================
     * DATE PARSER
     * =========================================================
     */
    private LocalDate parseDate(
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
                    "Appointment date must use YYYY-MM-DD format."
            );
        }
    }

    /*
     * =========================================================
     * TIME PARSER
     * =========================================================
     */
    private LocalTime parseTime(
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
                    "Appointment time must use HH:mm format."
            );
        }
    }

    /*
     * =========================================================
     * POSITIVE INTEGER VALIDATION
     * =========================================================
     */
    private int parsePositiveInteger(
            String value,
            String fieldName) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    fieldName + " is required."
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

    /*
     * =========================================================
     * NORMAL REQUEST PARAMETER
     * =========================================================
     */
    private String getTrimmedParameter(
            HttpServletRequest request,
            String parameterName) {

        String value =
                request.getParameter(
                        parameterName
                );

        return value == null
                ? null
                : value.trim();
    }

    /*
     * =========================================================
     * PUT MAP PARAMETER
     * =========================================================
     */
    private String getTrimmedParameter(
            Map<String, String> parameters,
            String parameterName) {

        String value =
                parameters.get(
                        parameterName
                );

        return value == null
                ? null
                : value.trim();
    }

    /*
     * =========================================================
     * WRITE APPOINTMENT LIST
     * =========================================================
     */
    private void writeAppointmentList(
            HttpServletResponse response,
            List<Appointment> appointments)
            throws IOException {

        PrintWriter writer =
                response.getWriter();

        writer.print("[");

        for (int index = 0;
             index < appointments.size();
             index++) {

            if (index > 0) {

                writer.print(",");
            }

            writer.print(
                    toJson(
                            appointments.get(index)
                    )
            );
        }

        writer.print("]");
    }

    /*
     * =========================================================
     * WRITE SINGLE APPOINTMENT
     * =========================================================
     */
    private void writeAppointmentJson(
            HttpServletResponse response,
            Appointment appointment)
            throws IOException {

        response.getWriter()
                .print(
                        toJson(
                                appointment
                        )
                );
    }

    /*
     * =========================================================
     * WRITE MESSAGE + APPOINTMENT
     * =========================================================
     */
    private void writeMessageWithAppointment(
            HttpServletResponse response,
            String message,
            Appointment appointment)
            throws IOException {

        response.getWriter()
                .print(
                        "{"
                        + "\"message\":\""
                        + escapeJson(message)
                        + "\","
                        + "\"appointment\":"
                        + toJson(appointment)
                        + "}"
                );
    }

    /*
     * =========================================================
     * APPOINTMENT → JSON
     * =========================================================
     */
    private String toJson(
            Appointment appointment) {

        return "{"

                + "\"appointmentId\":"
                + appointment.getAppointmentId()
                + ","

                + "\"appointmentNumber\":\""
                + escapeJson(
                        appointment.getAppointmentNumber()
                )
                + "\","

                + "\"patientId\":"
                + appointment.getPatientId()
                + ","

                + "\"dentistId\":"
                + appointment.getDentistId()
                + ","

                + "\"treatmentId\":"
                + appointment.getTreatmentId()
                + ","

                + "\"appointmentDate\":\""
                + appointment.getAppointmentDate()
                + "\","

                + "\"appointmentTime\":\""
                + appointment.getAppointmentTime()
                + "\","

                + "\"status\":\""
                + (
                    appointment.getStatus() == null
                            ? ""
                            : appointment
                                    .getStatus()
                                    .name()
                                    .toLowerCase()
                )
                + "\","

                + "\"notes\":\""
                + escapeJson(
                        appointment.getNotes()
                )
                + "\","

                + "\"createdBy\":"
                + appointment.getCreatedBy()

                + "}";
    }

    /*
     * =========================================================
     * JSON RESPONSE CONFIGURATION
     * =========================================================
     */
    private void configureJsonResponse(
            HttpServletResponse response) {

        response.setContentType(
                JSON_CONTENT_TYPE
        );

        response.setCharacterEncoding(
                CHARACTER_ENCODING
        );
    }

    /*
     * =========================================================
     * ERROR RESPONSE
     * =========================================================
     */
    private void sendErrorResponse(
            HttpServletResponse response,
            int statusCode,
            String message)
            throws IOException {

        response.setStatus(
                statusCode
        );

        response.getWriter()
                .print(
                        "{"
                        + "\"error\":\""
                        + escapeJson(message)
                        + "\""
                        + "}"
                );
    }

    /*
     * =========================================================
     * LOGGING
     * =========================================================
     */
    private void logError(
            String message,
            Exception exception) {

        getServletContext()
                .log(
                        message,
                        exception
                );
    }

    /*
     * =========================================================
     * JSON ESCAPING
     * =========================================================
     */
    private String escapeJson(
            String value) {

        if (value == null) {

            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}