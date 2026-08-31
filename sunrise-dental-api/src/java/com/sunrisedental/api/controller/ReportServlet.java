package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.ClinicReport;
import com.sunrisedental.api.service.ReportService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.SQLException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/api/reports")
public class ReportServlet extends HttpServlet {

    private final ReportService reportService;

    public ReportServlet() {
        this.reportService = new ReportService();
    }

    /*
     * Example:
     *
     * GET /api/reports?from=2026-09-01&to=2026-09-30
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        String fromValue =
                trimToNull(
                        request.getParameter("from")
                );

        String toValue =
                trimToNull(
                        request.getParameter("to")
                );

        if (fromValue == null
                || toValue == null) {

            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Both from and to dates are required."
            );

            return;
        }

        try {

            LocalDate fromDate =
                    LocalDate.parse(fromValue);

            LocalDate toDate =
                    LocalDate.parse(toValue);

            ClinicReport report =
                    reportService.generateClinicReport(
                            fromDate,
                            toDate
                    );

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            writeJson(
                    response,
                    toJson(report)
            );

        } catch (DateTimeParseException exception) {

            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Dates must use YYYY-MM-DD format."
            );

        } catch (IllegalArgumentException exception) {

            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (SQLException exception) {

            getServletContext().log(
                    "Unable to generate clinic report.",
                    exception
            );

            sendError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to generate the report at this time."
            );
        }
    }

    private void configureJsonResponse(
            HttpServletResponse response) {

        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                "UTF-8"
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

    private void sendError(
            HttpServletResponse response,
            int status,
            String message)
            throws IOException {

        response.setStatus(status);

        String json =
                "{"
                + "\"error\":\""
                + escapeJson(message)
                + "\""
                + "}";

        writeJson(
                response,
                json
        );
    }

    private void writeJson(
            HttpServletResponse response,
            String json)
            throws IOException {

        try (
            PrintWriter writer =
                    response.getWriter()
        ) {

            writer.print(json);
        }
    }

    /*
     * Converts the complete ClinicReport into JSON without
     * requiring an additional third-party JSON dependency.
     */
    private String toJson(
            ClinicReport report) {

        StringBuilder json =
                new StringBuilder();

        json.append("{");

        json.append("\"fromDate\":\"")
                .append(
                        escapeJson(
                                report.getFromDate()
                        )
                )
                .append("\",");

        json.append("\"toDate\":\"")
                .append(
                        escapeJson(
                                report.getToDate()
                        )
                )
                .append("\",");

        json.append("\"totalAppointments\":")
                .append(
                        report.getTotalAppointments()
                )
                .append(",");

        json.append("\"scheduledAppointments\":")
                .append(
                        report.getScheduledAppointments()
                )
                .append(",");

        json.append("\"completedAppointments\":")
                .append(
                        report.getCompletedAppointments()
                )
                .append(",");

        json.append("\"cancelledAppointments\":")
                .append(
                        report.getCancelledAppointments()
                )
                .append(",");

        json.append("\"totalBilled\":")
                .append(
                        report.getTotalBilled()
                )
                .append(",");

        json.append("\"totalPaid\":")
                .append(
                        report.getTotalPaid()
                )
                .append(",");

        json.append("\"outstandingAmount\":")
                .append(
                        report.getOutstandingAmount()
                )
                .append(",");

        appendDentistWorkload(
                json,
                report
        );

        json.append(",");

        appendTreatmentDemand(
                json,
                report
        );

        json.append("}");

        return json.toString();
    }

    private void appendDentistWorkload(
            StringBuilder json,
            ClinicReport report) {

        json.append("\"dentistWorkload\":[");

        for (int i = 0;
                i < report.getDentistWorkload().size();
                i++) {

            ClinicReport.DentistWorkload item =
                    report.getDentistWorkload().get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{");

            json.append("\"dentistId\":")
                    .append(
                            item.getDentistId()
                    )
                    .append(",");

            json.append("\"dentistName\":\"")
                    .append(
                            escapeJson(
                                    item.getDentistName()
                            )
                    )
                    .append("\",");

            json.append("\"specialization\":\"")
                    .append(
                            escapeJson(
                                    item.getSpecialization()
                            )
                    )
                    .append("\",");

            json.append("\"totalAppointments\":")
                    .append(
                            item.getTotalAppointments()
                    )
                    .append(",");

            json.append("\"completedAppointments\":")
                    .append(
                            item.getCompletedAppointments()
                    )
                    .append(",");

            json.append("\"cancelledAppointments\":")
                    .append(
                            item.getCancelledAppointments()
                    );

            json.append("}");
        }

        json.append("]");
    }

    private void appendTreatmentDemand(
            StringBuilder json,
            ClinicReport report) {

        json.append("\"treatmentDemand\":[");

        for (int i = 0;
                i < report.getTreatmentDemand().size();
                i++) {

            ClinicReport.TreatmentDemand item =
                    report.getTreatmentDemand().get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{");

            json.append("\"treatmentId\":")
                    .append(
                            item.getTreatmentId()
                    )
                    .append(",");

            json.append("\"treatmentCode\":\"")
                    .append(
                            escapeJson(
                                    item.getTreatmentCode()
                            )
                    )
                    .append("\",");

            json.append("\"treatmentName\":\"")
                    .append(
                            escapeJson(
                                    item.getTreatmentName()
                            )
                    )
                    .append("\",");

            json.append("\"appointmentCount\":")
                    .append(
                            item.getAppointmentCount()
                    )
                    .append(",");

            json.append("\"billedAmount\":")
                    .append(
                            item.getBilledAmount()
                    );

            json.append("}");
        }

        json.append("]");
    }

    /*
     * Escapes characters that could break a JSON string.
     */
    private String escapeJson(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}