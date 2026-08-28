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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet("/api/appointments")
public class AppointmentServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String CHARACTER_ENCODING = "UTF-8";

    private final AppointmentService appointmentService =
            new AppointmentService();

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

            if (dateParameter != null
                    && !dateParameter.isBlank()) {

                LocalDate appointmentDate =
                        parseDate(dateParameter);

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

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {

            int appointmentId =
                    parsePositiveInteger(
                            getTrimmedParameter(
                                    request,
                                    "appointmentId"
                            ),
                            "Appointment ID"
                    );

            Appointment appointment =
                    buildAppointmentFromRequest(
                            request,
                            false
                    );

            appointment.setAppointmentId(
                    appointmentId
            );

            /*
             * If status was not supplied during update,
             * AppointmentService will preserve the existing status.
             */
            String statusParameter =
                    getTrimmedParameter(
                            request,
                            "status"
                    );

            if (statusParameter == null
                    || statusParameter.isBlank()) {

                appointment.setStatus(null);
            }

            boolean updated =
                    appointmentService
                            .updateAppointment(
                                    appointment
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

    private LocalDate parseDate(
            String value) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }

        try {

            return LocalDate.parse(value);

        } catch (DateTimeParseException exception) {

            throw new IllegalArgumentException(
                    "Appointment date must use YYYY-MM-DD format."
            );
        }
    }

    private LocalTime parseTime(
            String value) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    "Appointment time is required."
            );
        }

        try {

            return LocalTime.parse(value);

        } catch (DateTimeParseException exception) {

            throw new IllegalArgumentException(
                    "Appointment time must use HH:mm format."
            );
        }
    }

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
                    Integer.parseInt(value);

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

    private void writeAppointmentJson(
            HttpServletResponse response,
            Appointment appointment)
            throws IOException {

        response.getWriter()
                .print(
                        toJson(appointment)
                );
    }

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

    private void configureJsonResponse(
            HttpServletResponse response) {

        response.setContentType(
                JSON_CONTENT_TYPE
        );

        response.setCharacterEncoding(
                CHARACTER_ENCODING
        );
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            int statusCode,
            String message)
            throws IOException {

        response.setStatus(statusCode);

        response.getWriter()
                .print(
                        "{"
                        + "\"error\":\""
                        + escapeJson(message)
                        + "\""
                        + "}"
                );
    }

    private void logError(
            String message,
            Exception exception) {

        getServletContext()
                .log(
                        message,
                        exception
                );
    }

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