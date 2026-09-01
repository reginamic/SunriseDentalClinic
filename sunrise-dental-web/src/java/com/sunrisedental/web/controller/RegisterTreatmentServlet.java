package com.sunrisedental.web.controller;

import com.sunrisedental.web.client.ApiClient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/treatments/new")
public class RegisterTreatmentServlet extends HttpServlet {

    private static final String FORM_PAGE =
            "/WEB-INF/views/treatment-form.jsp";

    private final ApiClient apiClient =
            new ApiClient();

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

        request.getRequestDispatcher(
                FORM_PAGE
        ).forward(
                request,
                response
        );
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

        preserveFormValues(
                request,
                treatmentName,
                description,
                treatmentPrice,
                consultationFee,
                estimatedDurationMinutes
        );

        String validationError =
                validateTreatment(
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
                    FORM_PAGE
            ).forward(
                    request,
                    response
            );

            return;
        }

        Map<String, String> formData =
                new HashMap<>();

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

        try {

            apiClient.post(
                    "/api/treatments",
                    formData
            );

            response.sendRedirect(
                    request.getContextPath()
                    + "/treatments?registered=true"
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "Treatment registration request was interrupted.",
                    exception
            );

            showApiError(
                    request,
                    response,
                    "Treatment service is temporarily unavailable."
            );

        } catch (IOException | RuntimeException exception) {

            getServletContext().log(
                    "Unable to register treatment through REST API.",
                    exception
            );

            showApiError(
                    request,
                    response,
                    "Unable to register treatment. "
                    + "Please verify the information and try again."
            );
        }
    }

    private String validateTreatment(
            String treatmentName,
            String description,
            String treatmentPrice,
            String consultationFee,
            String estimatedDurationMinutes) {

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

        if (estimatedDurationMinutes == null
                || estimatedDurationMinutes.isBlank()) {

            return "Estimated duration is required.";
        }

        try {

            int duration =
                    Integer.parseInt(
                            estimatedDurationMinutes
                    );

            if (duration <= 0) {

                return "Estimated duration must be greater than zero.";
            }

        } catch (NumberFormatException exception) {

            return "Estimated duration must be a valid whole number.";
        }

        return null;
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

    private void preserveFormValues(
            HttpServletRequest request,
            String treatmentName,
            String description,
            String treatmentPrice,
            String consultationFee,
            String estimatedDurationMinutes) {

        request.setAttribute(
                "treatmentName",
                treatmentName
        );

        request.setAttribute(
                "description",
                description
        );

        request.setAttribute(
                "treatmentPrice",
                treatmentPrice
        );

        request.setAttribute(
                "consultationFee",
                consultationFee
        );

        request.setAttribute(
                "estimatedDurationMinutes",
                estimatedDurationMinutes
        );
    }

    private void showApiError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message)
            throws ServletException, IOException {

        request.setAttribute(
                "errorMessage",
                message
        );

        request.getRequestDispatcher(
                FORM_PAGE
        ).forward(
                request,
                response
        );
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