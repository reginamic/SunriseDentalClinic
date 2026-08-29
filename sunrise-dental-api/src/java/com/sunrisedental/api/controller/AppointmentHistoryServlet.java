package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.AppointmentHistoryRecord;
import com.sunrisedental.api.service.AppointmentHistoryService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.SQLException;

import java.util.List;


/**
 * REST controller exposing appointment audit history.
 *
 * Historical states are created through the Memento
 * design pattern and stored in appointment_history.
 */
@WebServlet("/api/appointment-history")
public class AppointmentHistoryServlet
        extends HttpServlet {

    private static final String JSON_CONTENT_TYPE =
            "application/json";

    private static final String CHARACTER_ENCODING =
            "UTF-8";


    private final AppointmentHistoryService
            appointmentHistoryService =
            new AppointmentHistoryService();


    // =========================================================
    // GET APPOINTMENT HISTORY
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

            String appointmentIdParameter =
                    request.getParameter(
                            "appointmentId"
                    );


            int appointmentId =
                    parsePositiveInteger(
                            appointmentIdParameter,
                            "Appointment ID"
                    );


            List<AppointmentHistoryRecord> history =
                    appointmentHistoryService
                            .getAppointmentHistory(
                                    appointmentId
                            );


            response.setStatus(
                    HttpServletResponse.SC_OK
            );


            writeHistoryList(
                    response,
                    history
            );


        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );


        } catch (SQLException exception) {

            getServletContext()
                    .log(
                            "Unable to retrieve appointment history.",
                            exception
                    );


            sendErrorResponse(
                    response,
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve appointment history."
            );
        }
    }


    // =========================================================
    // WRITE HISTORY LIST
    // =========================================================

    private void writeHistoryList(
            HttpServletResponse response,
            List<AppointmentHistoryRecord> history)
            throws IOException {

        try (
                PrintWriter writer =
                        response.getWriter()
        ) {

            writer.print("[");


            for (int index = 0;
                 index < history.size();
                 index++) {

                if (index > 0) {

                    writer.print(",");
                }


                writer.print(
                        historyToJson(
                                history.get(index)
                        )
                );
            }


            writer.print("]");
        }
    }


    // =========================================================
    // HISTORY JSON
    // =========================================================

    private String historyToJson(
            AppointmentHistoryRecord record) {

        StringBuilder json =
                new StringBuilder();


        json.append("{");


        appendNumber(
                json,
                "historyId",
                record.getHistoryId()
        );


        appendNumber(
                json,
                "appointmentId",
                record.getAppointmentId()
        );


        appendString(
                json,
                "appointmentNumber",
                record.getAppointmentNumber()
        );


        appendNumber(
                json,
                "patientId",
                record.getPatientId()
        );


        appendNumber(
                json,
                "dentistId",
                record.getDentistId()
        );


        appendString(
                json,
                "dentistCode",
                record.getDentistCode()
        );


        appendString(
                json,
                "dentistName",
                record.getDentistName()
        );


        appendNumber(
                json,
                "treatmentId",
                record.getTreatmentId()
        );


        appendString(
                json,
                "treatmentCode",
                record.getTreatmentCode()
        );


        appendString(
                json,
                "treatmentName",
                record.getTreatmentName()
        );


        appendString(
                json,
                "appointmentDate",
                record.getAppointmentDate() == null
                        ? null
                        : record
                                .getAppointmentDate()
                                .toString()
        );


        appendString(
                json,
                "appointmentTime",
                record.getAppointmentTime() == null
                        ? null
                        : record
                                .getAppointmentTime()
                                .toString()
        );


        appendString(
                json,
                "status",
                record.getStatus()
        );


        appendString(
                json,
                "notes",
                record.getNotes()
        );


        appendNumber(
                json,
                "changedBy",
                record.getChangedBy()
        );


        appendString(
                json,
                "changedByName",
                record.getChangedByName()
        );


        appendString(
                json,
                "changeType",
                record.getChangeType()
        );


        appendStringLast(
                json,
                "changedAt",
                record.getChangedAt() == null
                        ? null
                        : record
                                .getChangedAt()
                                .toString()
        );


        json.append("}");


        return json.toString();
    }


    // =========================================================
    // JSON FIELD HELPERS
    // =========================================================

    private void appendNumber(
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


    private void appendString(
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

            json.append("null");

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


    private void appendStringLast(
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

            json.append("null");

        } else {

            json.append("\"")
                    .append(
                            escapeJson(
                                    value
                            )
                    )
                    .append("\"");
        }
    }


    // =========================================================
    // VALIDATE ID
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
                            value.trim()
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