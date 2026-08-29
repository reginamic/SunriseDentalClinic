package com.sunrisedental.web.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.TreatmentViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/treatments/edit")
public class EditTreatmentServlet extends HttpServlet {

    private static final String EDIT_PAGE =
            "/WEB-INF/views/treatment-edit.jsp";

    private final ApiClient apiClient =
            new ApiClient();

    private final Gson gson =
            new Gson();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/treatments"
            );

            return;
        }

        String idParameter =
                request.getParameter("id");

        Integer treatmentId =
                parsePositiveInteger(idParameter);

        if (treatmentId == null) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "A valid treatment ID is required."
            );

            return;
        }

        try {

            TreatmentViewModel treatment =
                    findTreatmentById(
                            treatmentId
                    );

            if (treatment == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Treatment record was not found."
                );

                return;
            }

            request.setAttribute(
                    "treatment",
                    treatment
            );

            request.getRequestDispatcher(
                    EDIT_PAGE
            ).forward(
                    request,
                    response
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "Treatment API request was interrupted.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Treatment service is temporarily unavailable."
            );

        } catch (IOException | RuntimeException exception) {

            getServletContext().log(
                    "Unable to retrieve treatment through REST API.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_BAD_GATEWAY,
                    "Unable to load treatment information."
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Administrator access is required."
            );

            return;
        }

        request.setCharacterEncoding(
                "UTF-8"
        );

        String treatmentIdValue =
                getTrimmedParameter(
                        request,
                        "treatmentId"
                );

        String treatmentName =
                getTrimmedParameter(
                        request,
                        "treatmentName"
                );

        String description =
                getTrimmedParameter(
                        request,
                        "description"
                );

        String treatmentPrice =
                getTrimmedParameter(
                        request,
                        "treatmentPrice"
                );

        String consultationFee =
                getTrimmedParameter(
                        request,
                        "consultationFee"
                );

        String estimatedDurationMinutes =
                getTrimmedParameter(
                        request,
                        "estimatedDurationMinutes"
                );

        boolean active =
                "true".equalsIgnoreCase(
                        request.getParameter("active")
                );

        preserveSubmittedValues(
                request,
                treatmentIdValue,
                treatmentName,
                description,
                treatmentPrice,
                consultationFee,
                estimatedDurationMinutes,
                active
        );

        String validationError =
                validateTreatment(
                        treatmentIdValue,
                        treatmentName,
                        description,
                        treatmentPrice,
                        consultationFee,
                        estimatedDurationMinutes
                );

        if (validationError != null) {

            request.setAttribute(
                    "errorMessage",
                    validationError
            );

            request.getRequestDispatcher(
                    EDIT_PAGE
            ).forward(
                    request,
                    response
            );

            return;
        }

        Map<String, String> formData =
                new HashMap<>();

        formData.put(
                "treatmentId",
                treatmentIdValue
        );

        formData.put(
                "treatmentName",
                treatmentName
        );

        formData.put(
                "description",
                description == null
                        ? ""
                        : description
        );

        formData.put(
                "treatmentPrice",
                treatmentPrice
        );

        formData.put(
                "consultationFee",
                consultationFee
        );

        formData.put(
                "estimatedDurationMinutes",
                estimatedDurationMinutes
        );

        formData.put(
                "active",
                String.valueOf(active)
        );

        try {

            apiClient.put(
                    "/api/treatments",
                    formData
            );

            response.sendRedirect(
                    request.getContextPath()
                    + "/treatments?updated=true"
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "Treatment update request was interrupted.",
                    exception
            );

            showUpdateError(
                    request,
                    response,
                    "Treatment service is temporarily unavailable."
            );

        } catch (IOException | RuntimeException exception) {

            getServletContext().log(
                    "Unable to update treatment through REST API.",
                    exception
            );

            showUpdateError(
                    request,
                    response,
                    "Unable to update treatment. "
                    + "Please verify the information and try again."
            );
        }
    }

    private TreatmentViewModel findTreatmentById(
            int treatmentId)
            throws IOException, InterruptedException {

        String jsonResponse =
                apiClient.get(
                        "/api/treatments"
                );

        Type treatmentListType =
                new TypeToken<
                        List<TreatmentViewModel>>() {
                }.getType();

        List<TreatmentViewModel> treatments =
                gson.fromJson(
                        jsonResponse,
                        treatmentListType
                );

        if (treatments == null) {

            treatments =
                    Collections.emptyList();
        }

        for (TreatmentViewModel treatment
                : treatments) {

            if (treatment.getTreatmentId()
                    == treatmentId) {

                return treatment;
            }
        }

        return null;
    }

    private String validateTreatment(
            String treatmentId,
            String treatmentName,
            String description,
            String treatmentPrice,
            String consultationFee,
            String estimatedDurationMinutes) {

        Integer parsedTreatmentId =
                parsePositiveInteger(
                        treatmentId
                );

        if (parsedTreatmentId == null) {

            return "A valid treatment ID is required.";
        }

        if (treatmentName == null
                || treatmentName.isBlank()) {

            return "Treatment name is required.";
        }

        if (treatmentName.length() < 3) {

            return "Treatment name must contain at least 3 characters.";
        }

        if (treatmentName.length() > 100) {

            return "Treatment name must not exceed 100 characters.";
        }

        if (description != null
                && description.length() > 500) {

            return "Description must not exceed 500 characters.";
        }

        BigDecimal parsedTreatmentPrice =
                parseMoney(
                        treatmentPrice
                );

        if (parsedTreatmentPrice == null) {

            return "Treatment price must be a valid amount.";
        }

        if (parsedTreatmentPrice.compareTo(
                BigDecimal.ZERO) < 0) {

            return "Treatment price cannot be negative.";
        }

        BigDecimal parsedConsultationFee =
                parseMoney(
                        consultationFee
                );

        if (parsedConsultationFee == null) {

            return "Consultation fee must be a valid amount.";
        }

        if (parsedConsultationFee.compareTo(
                BigDecimal.ZERO) < 0) {

            return "Consultation fee cannot be negative.";
        }

        Integer duration =
                parsePositiveInteger(
                        estimatedDurationMinutes
                );

        if (duration == null) {

            return "Estimated duration must be a positive whole number.";
        }

        return null;
    }

    private void preserveSubmittedValues(
            HttpServletRequest request,
            String treatmentId,
            String treatmentName,
            String description,
            String treatmentPrice,
            String consultationFee,
            String estimatedDurationMinutes,
            boolean active) {

        request.setAttribute(
                "submittedTreatmentId",
                treatmentId
        );

        request.setAttribute(
                "submittedTreatmentName",
                treatmentName
        );

        request.setAttribute(
                "submittedDescription",
                description
        );

        request.setAttribute(
                "submittedTreatmentPrice",
                treatmentPrice
        );

        request.setAttribute(
                "submittedConsultationFee",
                consultationFee
        );

        request.setAttribute(
                "submittedEstimatedDurationMinutes",
                estimatedDurationMinutes
        );

        request.setAttribute(
                "submittedActive",
                active
        );
    }

    private void showUpdateError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message)
            throws ServletException, IOException {

        request.setAttribute(
                "errorMessage",
                message
        );

        request.getRequestDispatcher(
                EDIT_PAGE
        ).forward(
                request,
                response
        );
    }

    private BigDecimal parseMoney(
            String value) {

        if (value == null
                || value.isBlank()) {

            return null;
        }

        try {

            return new BigDecimal(
                    value
            );

        } catch (NumberFormatException exception) {

            return null;
        }
    }

    private Integer parsePositiveInteger(
            String value) {

        if (value == null
                || value.isBlank()) {

            return null;
        }

        try {

            int number =
                    Integer.parseInt(
                            value.trim()
                    );

            return number > 0
                    ? number
                    : null;

        } catch (NumberFormatException exception) {

            return null;
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

    private boolean isAdmin(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(false);

        if (session == null) {

            return false;
        }

        Object role =
                session.getAttribute(
                        "role"
                );

        return role != null
                && "ADMIN".equalsIgnoreCase(
                        role.toString()
                );
    }
}