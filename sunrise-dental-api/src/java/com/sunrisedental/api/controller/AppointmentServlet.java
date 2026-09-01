package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.AppointmentDetails;
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

import java.math.BigDecimal;

import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;

import java.sql.SQLException;

import java.time.LocalDate;
import java.time.LocalTime;

import java.time.format.DateTimeParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * REST controller for Appointment Management.
 *
 * GET requests return enriched appointment information
 * including patient, dentist and treatment details.
 *
 * POST and PUT requests perform appointment creation,
 * rescheduling, cancellation and completion operations.
 */
@WebServlet("/api/appointments")
public class AppointmentServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE =
            "application/json";

    private static final String CHARACTER_ENCODING =
            "UTF-8";


    private final AppointmentService appointmentService =
            new AppointmentService();


    // =========================================================
    // GET
    // =========================================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(
                response
        );

        try {

            String idParameter =
                    getTrimmedParameter(
                            request,
                            "id"
                    );


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
             * =================================================
             * SEARCH BY INTERNAL APPOINTMENT ID
             * =================================================
             *
             * Primarily used by the Appointment Details page.
             */
            if (idParameter != null
                    && !idParameter.isBlank()) {

                int appointmentId =
                        parsePositiveInteger(
                                idParameter,
                                "Appointment ID"
                        );


                AppointmentDetails details =
                        appointmentService
                                .getAppointmentDetailsById(
                                        appointmentId
                                )
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Appointment not found."
                                        )
                                );


                writeAppointmentDetailsJson(
                        response,
                        details
                );

                return;
            }


            /*
             * =================================================
             * EXACT APPOINTMENT NUMBER SEARCH
             * =================================================
             */

            if (appointmentNumber != null
                    && !appointmentNumber.isBlank()) {

                AppointmentDetails details =
                        appointmentService
                                .getAppointmentDetailsByNumber(
                                        appointmentNumber
                                )
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Appointment not found."
                                        )
                                );


                writeAppointmentDetailsJson(
                        response,
                        details
                );

                return;
            }


            /*
             * =================================================
             * DATE SEARCH
             * =================================================
             */

            if (dateParameter != null
                    && !dateParameter.isBlank()) {

                LocalDate appointmentDate =
                        parseDate(
                                dateParameter
                        );


                List<AppointmentDetails> appointments =
                        appointmentService
                                .getAppointmentDetailsByDate(
                                        appointmentDate
                                );


                writeAppointmentDetailsList(
                        response,
                        appointments
                );

                return;
            }


            /*
             * =================================================
             * PATIENT SEARCH
             * =================================================
             */

            if (patientIdParameter != null
                    && !patientIdParameter.isBlank()) {

                int patientId =
                        parsePositiveInteger(
                                patientIdParameter,
                                "Patient ID"
                        );


                List<AppointmentDetails> appointments =
                        appointmentService
                                .getAppointmentDetailsByPatient(
                                        patientId
                                );


                writeAppointmentDetailsList(
                        response,
                        appointments
                );

                return;
            }


            /*
             * =================================================
             * ALL APPOINTMENTS
             * =================================================
             */

            List<AppointmentDetails> appointments =
                    appointmentService
                            .getAllAppointmentDetails();


            writeAppointmentDetailsList(
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


    // =========================================================
    // POST
    // =========================================================

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(
                response
        );

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


    // =========================================================
    // PUT
    // =========================================================

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(
                response
        );

        try {

            /*
             * Tomcat does not reliably expose
             * application/x-www-form-urlencoded PUT body fields
             * through request.getParameter().
             *
             * Therefore the PUT body is parsed manually.
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
             * changedBy represents the user performing
             * THIS appointment change.
             *
             * It is different from createdBy, which identifies
             * the original appointment creator.
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
             * If status is omitted, AppointmentService
             * preserves the existing status.
             */
            String statusParameter =
                    getTrimmedParameter(
                            parameters,
                            "status"
                    );


            if (statusParameter == null
                    || statusParameter.isBlank()) {

                appointment.setStatus(
                        null
                );
            }


            /*
             * Memento-aware AppointmentService update.
             *
             * AppointmentService:
             *
             * 1. loads existing appointment
             * 2. captures old state as Memento
             * 3. validates lifecycle
             * 4. runs scheduling Chain when required
             * 5. checks conflicts
             * 6. updates appointment
             * 7. stores previous state in history
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


    // =========================================================
    // BUILD APPOINTMENT FROM POST REQUEST
    // =========================================================

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
                        .patientId(
                                patientId
                        )
                        .dentistId(
                                dentistId
                        )
                        .treatmentId(
                                treatmentId
                        )
                        .appointmentDate(
                                appointmentDate
                        )
                        .appointmentTime(
                                appointmentTime
                        )
                        .notes(
                                notes
                        );


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


    // =========================================================
    // BUILD APPOINTMENT FROM PUT PARAMETERS
    // =========================================================

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
                        .patientId(
                                patientId
                        )
                        .dentistId(
                                dentistId
                        )
                        .treatmentId(
                                treatmentId
                        )
                        .appointmentDate(
                                appointmentDate
                        )
                        .appointmentTime(
                                appointmentTime
                        )
                        .notes(
                                notes
                        );


        if (statusParameter != null
                && !statusParameter.isBlank()) {

            builder.status(
                    AppointmentStatus.fromString(
                            statusParameter
                    )
            );
        }


        /*
         * createdBy is intentionally omitted.
         *
         * AppointmentService preserves the original
         * appointment creator during updates.
         */
        return builder.build();
    }


    // =========================================================
    // PUT FORM BODY PARSER
    // =========================================================

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

                body.append(
                        line
                );
            }
        }


        if (body.length() == 0) {

            return parameters;
        }


        String[] pairs =
                body.toString()
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


    // =========================================================
    // DATE PARSER
    // =========================================================

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


    // =========================================================
    // TIME PARSER
    // =========================================================

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
    // NORMAL REQUEST PARAMETER HELPER
    // =========================================================

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


    // =========================================================
    // MAP PARAMETER HELPER FOR PUT
    // =========================================================

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


    // =========================================================
    // ENRICHED APPOINTMENT DETAILS LIST JSON
    // =========================================================

    private void writeAppointmentDetailsList(
            HttpServletResponse response,
            List<AppointmentDetails> appointments)
            throws IOException {

        PrintWriter writer =
                response.getWriter();


        writer.print(
                "["
        );


        for (int index = 0;
             index < appointments.size();
             index++) {

            if (index > 0) {

                writer.print(
                        ","
                );
            }


            writer.print(
                    appointmentDetailsToJson(
                            appointments.get(index)
                    )
            );
        }


        writer.print(
                "]"
        );
    }


    // =========================================================
    // ENRICHED SINGLE APPOINTMENT JSON
    // =========================================================

    private void writeAppointmentDetailsJson(
            HttpServletResponse response,
            AppointmentDetails appointment)
            throws IOException {

        response.getWriter()
                .print(
                        appointmentDetailsToJson(
                                appointment
                        )
                );
    }


    // =========================================================
    // APPOINTMENT DETAILS JSON SERIALIZATION
    // =========================================================

    private String appointmentDetailsToJson(
            AppointmentDetails appointment) {

        StringBuilder json =
                new StringBuilder();


        json.append("{");


        // Appointment

        appendJsonNumber(
                json,
                "appointmentId",
                appointment.getAppointmentId()
        );

        appendJsonString(
                json,
                "appointmentNumber",
                appointment.getAppointmentNumber()
        );

        appendJsonString(
                json,
                "appointmentDate",
                appointment.getAppointmentDate() == null
                        ? null
                        : appointment
                                .getAppointmentDate()
                                .toString()
        );

        appendJsonString(
                json,
                "appointmentTime",
                appointment.getAppointmentTime() == null
                        ? null
                        : appointment
                                .getAppointmentTime()
                                .toString()
        );

        appendJsonString(
                json,
                "status",
                appointment.getStatus()
        );

        appendJsonString(
                json,
                "notes",
                appointment.getNotes()
        );


        // Patient

        appendJsonNumber(
                json,
                "patientId",
                appointment.getPatientId()
        );

        appendJsonString(
                json,
                "patientCode",
                appointment.getPatientCode()
        );

        appendJsonString(
                json,
                "patientName",
                appointment.getPatientName()
        );

        appendJsonString(
                json,
                "patientAddress",
                appointment.getPatientAddress()
        );

        appendJsonString(
                json,
                "patientContactNumber",
                appointment.getPatientContactNumber()
        );

        appendJsonString(
                json,
                "patientEmail",
                appointment.getPatientEmail()
        );


        // Dentist

        appendJsonNumber(
                json,
                "dentistId",
                appointment.getDentistId()
        );

        appendJsonString(
                json,
                "dentistCode",
                appointment.getDentistCode()
        );

        appendJsonString(
                json,
                "dentistName",
                appointment.getDentistName()
        );

        appendJsonString(
                json,
                "dentistSpecialization",
                appointment.getDentistSpecialization()
        );

        appendJsonString(
                json,
                "dentistContactNumber",
                appointment.getDentistContactNumber()
        );

        appendJsonString(
                json,
                "dentistEmail",
                appointment.getDentistEmail()
        );

        appendJsonBoolean(
                json,
                "dentistActive",
                appointment.isDentistActive()
        );


        // Treatment

        appendJsonNumber(
                json,
                "treatmentId",
                appointment.getTreatmentId()
        );

        appendJsonString(
                json,
                "treatmentCode",
                appointment.getTreatmentCode()
        );

        appendJsonString(
                json,
                "treatmentName",
                appointment.getTreatmentName()
        );

        appendJsonString(
                json,
                "treatmentDescription",
                appointment.getTreatmentDescription()
        );

        appendJsonDecimal(
                json,
                "treatmentPrice",
                appointment.getTreatmentPrice()
        );

        appendJsonDecimal(
                json,
                "consultationFee",
                appointment.getConsultationFee()
        );

        appendJsonNullableInteger(
                json,
                "estimatedDurationMinutes",
                appointment.getEstimatedDurationMinutes()
        );

        appendJsonBoolean(
                json,
                "treatmentActive",
                appointment.isTreatmentActive()
        );


        // Calculated database View values

        appendJsonString(
                json,
                "estimatedEndTime",
                appointment.getEstimatedEndTime() == null
                        ? null
                        : appointment
                                .getEstimatedEndTime()
                                .toString()
        );

        appendJsonDecimal(
                json,
                "estimatedTotalCost",
                appointment.getEstimatedTotalCost()
        );


        // Audit

        appendJsonNumberLast(
                json,
                "createdBy",
                appointment.getCreatedBy()
        );


        json.append("}");


        return json.toString();
    }


    // =========================================================
    // MESSAGE + BASIC APPOINTMENT RESPONSE
    // =========================================================

    private void writeMessageWithAppointment(
            HttpServletResponse response,
            String message,
            Appointment appointment)
            throws IOException {

        response.getWriter()
                .print(
                        "{"
                        + "\"message\":\""
                        + escapeJson(
                                message
                        )
                        + "\","
                        + "\"appointment\":"
                        + appointmentToJson(
                                appointment
                        )
                        + "}"
                );
    }


    // =========================================================
    // BASIC APPOINTMENT JSON SERIALIZATION
    // Used for POST / PUT responses.
    // =========================================================

    private String appointmentToJson(
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
                + (
                    appointment.getAppointmentDate() == null
                            ? ""
                            : appointment
                                    .getAppointmentDate()
                                    .toString()
                )
                + "\","

                + "\"appointmentTime\":\""
                + (
                    appointment.getAppointmentTime() == null
                            ? ""
                            : appointment
                                    .getAppointmentTime()
                                    .toString()
                )
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


    // =========================================================
    // JSON FIELD HELPERS
    // =========================================================

    private void appendJsonString(
            StringBuilder json,
            String fieldName,
            String value) {

        json.append("\"")
                .append(
                        escapeJson(
                                fieldName
                        )
                )
                .append("\":");


        if (value == null) {

            json.append(
                    "null"
            );

        } else {

            json.append("\"")
                    .append(
                            escapeJson(
                                    value
                            )
                    )
                    .append("\"");
        }


        json.append(",");
    }


    private void appendJsonNumber(
            StringBuilder json,
            String fieldName,
            int value) {

        json.append("\"")
                .append(
                        escapeJson(
                                fieldName
                        )
                )
                .append("\":")
                .append(
                        value
                )
                .append(",");
    }


    private void appendJsonNullableInteger(
            StringBuilder json,
            String fieldName,
            Integer value) {

        json.append("\"")
                .append(
                        escapeJson(
                                fieldName
                        )
                )
                .append("\":");


        if (value == null) {

            json.append(
                    "null"
            );

        } else {

            json.append(
                    value
            );
        }


        json.append(",");
    }


    private void appendJsonDecimal(
            StringBuilder json,
            String fieldName,
            BigDecimal value) {

        json.append("\"")
                .append(
                        escapeJson(
                                fieldName
                        )
                )
                .append("\":");


        if (value == null) {

            json.append(
                    "null"
            );

        } else {

            json.append(
                    value.toPlainString()
            );
        }


        json.append(",");
    }


    private void appendJsonBoolean(
            StringBuilder json,
            String fieldName,
            boolean value) {

        json.append("\"")
                .append(
                        escapeJson(
                                fieldName
                        )
                )
                .append("\":")
                .append(
                        value
                )
                .append(",");
    }


    private void appendJsonNumberLast(
            StringBuilder json,
            String fieldName,
            int value) {

        json.append("\"")
                .append(
                        escapeJson(
                                fieldName
                        )
                )
                .append("\":")
                .append(
                        value
                );
    }


    // =========================================================
    // RESPONSE CONFIGURATION
    // =========================================================

    private void configureJsonResponse(
            HttpServletResponse response) {

        response.setContentType(
                JSON_CONTENT_TYPE
        );

        response.setCharacterEncoding(
                CHARACTER_ENCODING
        );
    }


    // =========================================================
    // ERROR RESPONSE
    // =========================================================

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
                        + escapeJson(
                                message
                        )
                        + "\""
                        + "}"
                );
    }


    // =========================================================
    // SERVER LOGGING
    // =========================================================

    private void logError(
            String message,
            Exception exception) {

        getServletContext()
                .log(
                        message,
                        exception
                );
    }


    // =========================================================
    // JSON ESCAPING
    // =========================================================

    private String escapeJson(
            String value) {

        if (value == null) {

            return "";
        }


        return value
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\r",
                        "\\r"
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\t",
                        "\\t"
                );
    }
}