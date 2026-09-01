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

@WebServlet("/patients/new")
public class RegisterPatientServlet extends HttpServlet {

    private static final String PATIENT_FORM_PAGE =
            "/WEB-INF/views/patient-form.jsp";

    private final ApiClient apiClient =
            new ApiClient();

    /**
     * Displays the patient registration form.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute(
                "pageTitle",
                "Register Patient"
        );

        request.setAttribute(
                "formMode",
                "create"
        );

        request.getRequestDispatcher(
                PATIENT_FORM_PAGE
        ).forward(
                request,
                response
        );
    }

    /**
     * Processes a new patient registration.
     *
     * The Web application sends the patient data
     * to the Sunrise Dental REST API.
     *
     * The Web tier does not access MySQL directly.
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String fullName =
                normalize(
                        request.getParameter(
                                "fullName"
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

        String dateOfBirth =
                normalize(
                        request.getParameter(
                                "dateOfBirth"
                        )
                );

        String gender =
                normalize(
                        request.getParameter(
                                "gender"
                        )
                );

        String address =
                normalize(
                        request.getParameter(
                                "address"
                        )
                );

        /*
         * Presentation-layer validation.
         *
         * The API/service layer will also perform
         * business validation independently.
         */
        String validationError =
                validatePatient(
                        fullName,
                        contactNumber,
                        email,
                        dateOfBirth,
                        gender,
                        address
                );

        if (validationError != null) {

            preserveFormValues(
                    request,
                    fullName,
                    contactNumber,
                    email,
                    dateOfBirth,
                    gender,
                    address
            );

            request.setAttribute(
                    "errorMessage",
                    validationError
            );

            request.getRequestDispatcher(
                    PATIENT_FORM_PAGE
            ).forward(
                    request,
                    response
            );

            return;
        }

        try {

            Map<String, String> patientData =
                    new HashMap<>();

            patientData.put(
                    "fullName",
                    fullName
            );

            patientData.put(
                    "address",
                    address
            );

            patientData.put(
                    "contactNumber",
                    contactNumber
            );

            patientData.put(
                    "email",
                    email == null
                            ? ""
                            : email
            );

            patientData.put(
                    "dateOfBirth",
                    dateOfBirth
            );

            patientData.put(
                    "gender",
                    gender
            );

            /*
             * Distributed call:
             *
             * sunrise-dental-web
             *        ↓ HTTP
             * sunrise-dental-api
             */
            apiClient.post(
                    "/api/patients",
                    patientData
            );

            /*
             * Post/Redirect/Get pattern.
             *
             * Prevents duplicate form submission
             * if the user refreshes the browser.
             */
            response.sendRedirect(
                    request.getContextPath()
                    + "/patients?registered=true"
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "Patient registration API request "
                    + "was interrupted.",
                    exception
            );

            showRegistrationError(
                    request,
                    response,
                    "Patient registration service "
                    + "is temporarily unavailable.",
                    fullName,
                    contactNumber,
                    email,
                    dateOfBirth,
                    gender,
                    address
            );

        } catch (IOException exception) {

            getServletContext().log(
                    "Unable to register patient "
                    + "through the API.",
                    exception
            );

            showRegistrationError(
                    request,
                    response,
                    "Unable to register the patient. "
                    + "Please check the information "
                    + "and try again.",
                    fullName,
                    contactNumber,
                    email,
                    dateOfBirth,
                    gender,
                    address
            );
        }
    }

    private String validatePatient(
            String fullName,
            String contactNumber,
            String email,
            String dateOfBirth,
            String gender,
            String address) {

        if (isBlank(fullName)) {
            return "Patient full name is required.";
        }

        if (fullName.length() < 2) {
            return "Patient full name must contain "
                    + "at least 2 characters.";
        }

        if (isBlank(contactNumber)) {
            return "Contact number is required.";
        }

        if (!contactNumber.matches(
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

            return "Please enter a valid email address.";
        }

        if (isBlank(dateOfBirth)) {
            return "Date of birth is required.";
        }

        if (isBlank(gender)) {
            return "Gender is required.";
        }

        if (!gender.equals("MALE")
                && !gender.equals("FEMALE")
                && !gender.equals("OTHER")) {

            return "Please select a valid gender.";
        }

        if (isBlank(address)) {
            return "Patient address is required.";
        }

        if (address.length() < 5) {
            return "Patient address is too short.";
        }

        return null;
    }

    private void showRegistrationError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message,
            String fullName,
            String contactNumber,
            String email,
            String dateOfBirth,
            String gender,
            String address)
            throws ServletException, IOException {

        preserveFormValues(
                request,
                fullName,
                contactNumber,
                email,
                dateOfBirth,
                gender,
                address
        );

        request.setAttribute(
                "errorMessage",
                message
        );

        request.getRequestDispatcher(
                PATIENT_FORM_PAGE
        ).forward(
                request,
                response
        );
    }

    private void preserveFormValues(
            HttpServletRequest request,
            String fullName,
            String contactNumber,
            String email,
            String dateOfBirth,
            String gender,
            String address) {

        request.setAttribute(
                "enteredFullName",
                safeValue(fullName)
        );

        request.setAttribute(
                "enteredContactNumber",
                safeValue(contactNumber)
        );

        request.setAttribute(
                "enteredEmail",
                safeValue(email)
        );

        request.setAttribute(
                "enteredDateOfBirth",
                safeValue(dateOfBirth)
        );

        request.setAttribute(
                "enteredGender",
                safeValue(gender)
        );

        request.setAttribute(
                "enteredAddress",
                safeValue(address)
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

    private String safeValue(
            String value) {

        return value == null
                ? ""
                : value;
    }
}