package com.sunrisedental.web.controller;

import com.sunrisedental.web.client.ApiClient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/dentists/new")
public class RegisterDentistServlet extends HttpServlet {

    private static final String DENTIST_FORM_PAGE =
            "/WEB-INF/views/dentist-form.jsp";

    private final ApiClient apiClient =
            new ApiClient();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Only ADMIN users may register dentists.
         */
        String role =
                (String)
                request.getSession()
                        .getAttribute("role");

        if (!"ADMIN".equalsIgnoreCase(role)) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/dentists"
            );

            return;
        }

        request.getRequestDispatcher(
                DENTIST_FORM_PAGE
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

        /*
         * Server-side role authorization.
         */
        String role =
                (String)
                request.getSession()
                        .getAttribute("role");

        if (!"ADMIN".equalsIgnoreCase(role)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );

            return;
        }

        String fullName =
                normalize(
                        request.getParameter(
                                "fullName"
                        )
                );

        String specialization =
                normalize(
                        request.getParameter(
                                "specialization"
                        )
                );

        String contactNumber =
                normalize(
                        request.getParameter(
                                "contactNumber"
                        )
                );

        String email =
                normalize(
                        request.getParameter(
                                "email"
                        )
                );

        String validationError =
                validateDentist(
                        fullName,
                        specialization,
                        contactNumber,
                        email
                );

        if (validationError != null) {

            preserveFormValues(
                    request,
                    fullName,
                    specialization,
                    contactNumber,
                    email
            );

            request.setAttribute(
                    "errorMessage",
                    validationError
            );

            request.getRequestDispatcher(
                    DENTIST_FORM_PAGE
            ).forward(
                    request,
                    response
            );

            return;
        }

        try {

            Map<String, String> dentistData =
                    new HashMap<>();

            dentistData.put(
                    "fullName",
                    fullName
            );

            dentistData.put(
                    "specialization",
                    specialization
            );

            dentistData.put(
                    "contactNumber",
                    contactNumber == null
                            ? ""
                            : contactNumber
            );

            dentistData.put(
                    "email",
                    email == null
                            ? ""
                            : email
            );

            /*
             * Distributed architecture:
             *
             * Web
             *   ↓ HTTP POST
             * Dentist REST API
             *   ↓
             * Service
             *   ↓
             * Repository
             *   ↓
             * MySQL
             */
            apiClient.post(
                    "/api/dentists",
                    dentistData
            );

            response.sendRedirect(
                    request.getContextPath()
                    + "/dentists?registered=true"
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "Dentist registration request "
                    + "was interrupted.",
                    exception
            );

            showApiError(
                    request,
                    response,
                    fullName,
                    specialization,
                    contactNumber,
                    email
            );

        } catch (IOException exception) {

            getServletContext().log(
                    "Unable to register dentist "
                    + "through REST API.",
                    exception
            );

            showApiError(
                    request,
                    response,
                    fullName,
                    specialization,
                    contactNumber,
                    email
            );
        }
    }

    private String validateDentist(
            String fullName,
            String specialization,
            String contactNumber,
            String email) {

        if (isBlank(fullName)) {

            return "Dentist full name is required.";
        }

        if (fullName.length() < 3) {

            return "Dentist full name must contain "
                    + "at least 3 characters.";
        }

        if (isBlank(specialization)) {

            return "Specialization is required.";
        }

        if (specialization.length() < 3) {

            return "Specialization must contain "
                    + "at least 3 characters.";
        }

        if (!isBlank(contactNumber)
                && !contactNumber.matches(
                        "\\d{10,15}"
                )) {

            return "Contact number must contain "
                    + "10 to 15 digits.";
        }

        if (!isBlank(email)
                && !email.matches(
                        "^[A-Za-z0-9+_.-]+"
                        + "@[A-Za-z0-9.-]+$"
                )) {

            return "Please enter a valid "
                    + "email address.";
        }

        return null;
    }

    private void preserveFormValues(
            HttpServletRequest request,
            String fullName,
            String specialization,
            String contactNumber,
            String email) {

        request.setAttribute(
                "fullNameValue",
                fullName
        );

        request.setAttribute(
                "specializationValue",
                specialization
        );

        request.setAttribute(
                "contactNumberValue",
                contactNumber
        );

        request.setAttribute(
                "emailValue",
                email
        );
    }

    private void showApiError(
            HttpServletRequest request,
            HttpServletResponse response,
            String fullName,
            String specialization,
            String contactNumber,
            String email)
            throws ServletException, IOException {

        preserveFormValues(
                request,
                fullName,
                specialization,
                contactNumber,
                email
        );

        request.setAttribute(
                "errorMessage",
                "Unable to register dentist. "
                + "Please verify the information "
                + "and try again."
        );

        request.getRequestDispatcher(
                DENTIST_FORM_PAGE
        ).forward(
                request,
                response
        );
    }

    private String normalize(
            String value) {

        if (value == null) {
            return null;
        }

        return value.trim();
    }

    private boolean isBlank(
            String value) {

        return value == null
                || value.isBlank();
    }
}