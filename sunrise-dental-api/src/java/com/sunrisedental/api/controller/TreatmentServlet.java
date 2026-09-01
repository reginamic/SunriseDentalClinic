package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.Treatment;
import com.sunrisedental.api.service.TreatmentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;


import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/treatments")
public class TreatmentServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE =
            "application/json";

    private static final String CHARACTER_ENCODING =
            "UTF-8";

    private final TreatmentService treatmentService =
            new TreatmentService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {

            String search =
                    getTrimmedParameter(request, "search");

            List<Treatment> treatments;

            if (search == null || search.isBlank()) {
                treatments =
                        treatmentService.getAllTreatments();
            } else {
                treatments =
                        treatmentService.searchTreatments(search);
            }

            writeTreatmentList(response, treatments);

        } catch (SQLException exception) {

            logError(
                    "Unable to retrieve treatments.",
                    exception
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve treatments."
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

            Treatment treatment =
                    buildTreatmentFromRequest(request);

            Treatment savedTreatment =
                    treatmentService.registerTreatment(treatment);

            response.setStatus(
                    HttpServletResponse.SC_CREATED
            );

            writeMessageWithTreatment(
                    response,
                    "Treatment created successfully.",
                    savedTreatment
            );

        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (SQLException exception) {

            logError(
                    "Unable to create treatment.",
                    exception
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to create treatment."
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

        Map<String, String> formData =
                parsePutFormData(request);

        int treatmentId =
                parseTreatmentId(formData);

        Treatment treatment =
                buildTreatmentFromPutData(formData);

        treatment.setTreatmentId(
                treatmentId
        );

        String activeParameter =
                getPutValue(
                        formData,
                        "active"
                );

        if (activeParameter != null
                && !activeParameter.isBlank()) {

            treatment.setActive(
                    Boolean.parseBoolean(
                            activeParameter
                    )
            );

        } else {

            Treatment existingTreatment =
                    treatmentService
                            .getTreatmentById(
                                    treatmentId
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Treatment not found."
                                    )
                            );

            treatment.setActive(
                    existingTreatment.isActive()
            );
        }

        boolean updated =
                treatmentService
                        .updateTreatment(
                                treatment
                        );

        if (!updated) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "Treatment not found."
            );

            return;
        }

        writeMessageWithTreatment(
                response,
                "Treatment updated successfully.",
                treatment
        );

    } catch (IllegalArgumentException exception) {

        sendErrorResponse(
                response,
                HttpServletResponse.SC_BAD_REQUEST,
                exception.getMessage()
        );

    } catch (SQLException exception) {

        logError(
                "Unable to update treatment.",
                exception
        );

        sendErrorResponse(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Unable to update treatment."
        );
    }
}



private Map<String, String> parsePutFormData(
        HttpServletRequest request)
        throws IOException {

    byte[] bodyBytes =
            request.getInputStream()
                    .readAllBytes();

    String requestBody =
            new String(
                    bodyBytes,
                    StandardCharsets.UTF_8
            );

    Map<String, String> formData =
            new HashMap<>();

    if (requestBody.isBlank()) {
        return formData;
    }

    String[] parameters =
            requestBody.split("&");

    for (String parameter : parameters) {

        int equalsIndex =
                parameter.indexOf('=');

        String encodedKey;

        String encodedValue;

        if (equalsIndex >= 0) {

            encodedKey =
                    parameter.substring(
                            0,
                            equalsIndex
                    );

            encodedValue =
                    parameter.substring(
                            equalsIndex + 1
                    );

        } else {

            encodedKey =
                    parameter;

            encodedValue =
                    "";
        }

        String key =
                URLDecoder.decode(
                        encodedKey,
                        StandardCharsets.UTF_8
                );

        String value =
                URLDecoder.decode(
                        encodedValue,
                        StandardCharsets.UTF_8
                );

        formData.put(
                key,
                value.trim()
        );
    }

    return formData;
}


private Treatment buildTreatmentFromPutData(
        Map<String, String> formData) {

    Treatment treatment =
            new Treatment();

    treatment.setTreatmentName(
            getPutValue(
                    formData,
                    "treatmentName"
            )
    );

    treatment.setDescription(
            getPutValue(
                    formData,
                    "description"
            )
    );

    treatment.setTreatmentPrice(
            parseBigDecimalValue(
                    formData,
                    "treatmentPrice"
            )
    );

    treatment.setConsultationFee(
            parseBigDecimalValue(
                    formData,
                    "consultationFee"
            )
    );

    treatment.setEstimatedDurationMinutes(
            parseOptionalIntegerValue(
                    formData,
                    "estimatedDurationMinutes"
            )
    );

    return treatment;
}


private int parseTreatmentId(
        Map<String, String> formData) {

    String value =
            getPutValue(
                    formData,
                    "treatmentId"
            );

    if (value == null
            || value.isBlank()) {

        throw new IllegalArgumentException(
                "Treatment ID is required."
        );
    }

    try {

        int treatmentId =
                Integer.parseInt(value);

        if (treatmentId <= 0) {

            throw new IllegalArgumentException(
                    "Treatment ID must be greater than zero."
            );
        }

        return treatmentId;

    } catch (NumberFormatException exception) {

        throw new IllegalArgumentException(
                "Treatment ID must be a valid number."
        );
    }
}


private BigDecimal parseBigDecimalValue(
        Map<String, String> formData,
        String parameterName) {

    String value =
            getPutValue(
                    formData,
                    parameterName
            );

    if (value == null
            || value.isBlank()) {

        return null;
    }

    try {

        return new BigDecimal(
                value
        );

    } catch (NumberFormatException exception) {

        throw new IllegalArgumentException(
                parameterName
                        + " must be a valid number."
        );
    }
}


private Integer parseOptionalIntegerValue(
        Map<String, String> formData,
        String parameterName) {

    String value =
            getPutValue(
                    formData,
                    parameterName
            );

    if (value == null
            || value.isBlank()) {

        return null;
    }

    try {

        return Integer.valueOf(
                value
        );

    } catch (NumberFormatException exception) {

        throw new IllegalArgumentException(
                parameterName
                        + " must be a valid integer."
        );
    }
}


private String getPutValue(
        Map<String, String> formData,
        String parameterName) {

    String value =
            formData.get(
                    parameterName
            );

    return value == null
            ? null
            : value.trim();
}

    private Treatment buildTreatmentFromRequest(
            HttpServletRequest request) {

        Treatment treatment = new Treatment();

        treatment.setTreatmentName(
                getTrimmedParameter(
                        request,
                        "treatmentName"
                )
        );

        treatment.setDescription(
                getTrimmedParameter(
                        request,
                        "description"
                )
        );

        treatment.setTreatmentPrice(
                parseBigDecimalParameter(
                        request,
                        "treatmentPrice"
                )
        );

        treatment.setConsultationFee(
                parseBigDecimalParameter(
                        request,
                        "consultationFee"
                )
        );

        treatment.setEstimatedDurationMinutes(
                parseOptionalIntegerParameter(
                        request,
                        "estimatedDurationMinutes"
                )
        );

        return treatment;
    }

    private BigDecimal parseBigDecimalParameter(
            HttpServletRequest request,
            String parameterName) {

        String value =
                getTrimmedParameter(
                        request,
                        parameterName
                );

        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    parameterName
                            + " must be a valid number."
            );
        }
    }

    private Integer parseOptionalIntegerParameter(
            HttpServletRequest request,
            String parameterName) {

        String value =
                getTrimmedParameter(
                        request,
                        parameterName
                );

        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    parameterName
                            + " must be a valid integer."
            );
        }
    }

    private int parseTreatmentId(
            HttpServletRequest request) {

        String value =
                getTrimmedParameter(
                        request,
                        "treatmentId"
                );

        if (value == null || value.isBlank()) {

            throw new IllegalArgumentException(
                    "Treatment ID is required."
            );
        }

        try {

            int treatmentId =
                    Integer.parseInt(value);

            if (treatmentId <= 0) {

                throw new IllegalArgumentException(
                        "Treatment ID must be greater than zero."
                );
            }

            return treatmentId;

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "Treatment ID must be a valid number."
            );
        }
    }

    private String getTrimmedParameter(
            HttpServletRequest request,
            String parameterName) {

        String value =
                request.getParameter(parameterName);

        return value == null
                ? null
                : value.trim();
    }

    private void writeTreatmentList(
            HttpServletResponse response,
            List<Treatment> treatments)
            throws IOException {

        PrintWriter writer =
                response.getWriter();

        writer.print("[");

        for (int index = 0;
             index < treatments.size();
             index++) {

            if (index > 0) {
                writer.print(",");
            }

            writer.print(
                    toJson(treatments.get(index))
            );
        }

        writer.print("]");
    }

    private void writeMessageWithTreatment(
            HttpServletResponse response,
            String message,
            Treatment treatment)
            throws IOException {

        PrintWriter writer =
                response.getWriter();

        writer.print(
                "{"
                        + "\"message\":\""
                        + escapeJson(message)
                        + "\","
                        + "\"treatment\":"
                        + toJson(treatment)
                        + "}"
        );
    }

    private String toJson(Treatment treatment) {

        return "{"
                + "\"treatmentId\":"
                + treatment.getTreatmentId()
                + ","
                + "\"treatmentCode\":\""
                + escapeJson(
                        treatment.getTreatmentCode()
                )
                + "\","
                + "\"treatmentName\":\""
                + escapeJson(
                        treatment.getTreatmentName()
                )
                + "\","
                + "\"description\":\""
                + escapeJson(
                        treatment.getDescription()
                )
                + "\","
                + "\"treatmentPrice\":"
                + treatment.getTreatmentPrice()
                + ","
                + "\"consultationFee\":"
                + treatment.getConsultationFee()
                + ","
                + "\"estimatedDurationMinutes\":"
                + (
                    treatment.getEstimatedDurationMinutes()
                            == null
                            ? "null"
                            : treatment
                                    .getEstimatedDurationMinutes()
                )
                + ","
                + "\"active\":"
                + treatment.isActive()
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

        PrintWriter writer =
                response.getWriter();

        writer.print(
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
                .log(message, exception);
    }

    private String escapeJson(String value) {

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