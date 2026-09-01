package com.sunrisedental.web.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.PatientViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/patients/edit")
public class EditPatientServlet extends HttpServlet {

    private static final String EDIT_PATIENT_PAGE =
            "/WEB-INF/views/patient-edit.jsp";

    private final ApiClient apiClient =
            new ApiClient();

    private final Gson gson =
            new Gson();

    /**
     * Loads the selected patient from the REST API.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String patientIdParameter =
                request.getParameter("id");

        if (patientIdParameter == null
                || patientIdParameter.isBlank()) {

            redirectToPatients(
                    request,
                    response
            );

            return;
        }

        try {

            int patientId =
                    Integer.parseInt(
                            patientIdParameter
                    );

            if (patientId <= 0) {

                redirectToPatients(
                        request,
                        response
                );

                return;
            }

            PatientViewModel patient =
                    loadPatientById(
                            patientId
                    );

            if (patient == null) {

                redirectToPatients(
                        request,
                        response
                );

                return;
            }

            request.setAttribute(
                    "patient",
                    patient
            );

            request.getRequestDispatcher(
                    EDIT_PATIENT_PAGE
            ).forward(
                    request,
                    response
            );

        } catch (NumberFormatException exception) {

            redirectToPatients(
                    request,
                    response
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "Patient API request was interrupted.",
                    exception
            );

            redirectToPatients(
                    request,
                    response
            );

        } catch (IOException exception) {

            getServletContext().log(
                    "Unable to retrieve patient "
                    + "from REST API.",
                    exception
            );

            redirectToPatients(
                    request,
                    response
            );
        }
    }

    /**
     * Updates an existing patient.
     *
     * Web Form
     *    ↓
     * EditPatientServlet
     *    ↓ HTTP PUT
     * Patient REST API
     *    ↓
     * PatientService
     *    ↓
     * Repository
     *    ↓
     * MySQL
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String patientIdParameter =
                normalize(
                        request.getParameter(
                                "patientId"
                        )
                );

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

        int patientId;

        try {

            patientId =
                    Integer.parseInt(
                            patientIdParameter
                    );

        } catch (NumberFormatException exception) {

            redirectToPatients(
                    request,
                    response
            );

            return;
        }

        if (patientId <= 0) {

            redirectToPatients(
                    request,
                    response
            );

            return;
        }

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

            showEditError(
                    request,
                    response,
                    patientId,
                    validationError
            );

            return;
        }

        try {

            Map<String, String> patientData =
                    new HashMap<>();

            patientData.put(
                    "patientId",
                    String.valueOf(patientId)
            );

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
             * Distributed update.
             *
             * The Web project never updates
             * MySQL directly.
             */
            apiClient.put(
                    "/api/patients",
                    patientData
            );

            /*
             * Post / Redirect / Get pattern.
             */
            response.sendRedirect(
                    request.getContextPath()
                    + "/patients?updated=true"
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "Patient update request "
                    + "was interrupted.",
                    exception
            );

            showEditError(
                    request,
                    response,
                    patientId,
                    "Patient update service is "
                    + "temporarily unavailable."
            );

        } catch (IOException exception) {

            getServletContext().log(
                    "Unable to update patient "
                    + "through REST API.",
                    exception
            );

            showEditError(
                    request,
                    response,
                    patientId,
                    "Unable to update the patient. "
                    + "Please verify the information "
                    + "and try again."
            );
        }
    }

    /**
     * Loads one patient from the JSON array
     * currently returned by the Patient API.
     */
    private PatientViewModel loadPatientById(
            int patientId)
            throws IOException, InterruptedException {

        String jsonResponse =
                apiClient.get(
                        "/api/patients?patientId="
                        + patientId
                );

        if (jsonResponse == null
                || jsonResponse.isBlank()) {

            return null;
        }

        Type patientListType =
                new TypeToken<
                        List<PatientViewModel>>() {
                }.getType();

        List<PatientViewModel> patients =
                gson.fromJson(
                        jsonResponse,
                        patientListType
                );

        if (patients == null
                || patients.isEmpty()) {

            return null;
        }

        for (PatientViewModel patient
                : patients) {

            if (patient != null
                    && patient.getPatientId()
                    == patientId) {

                return patient;
            }
        }

        return null;
    }

    private void showEditError(
            HttpServletRequest request,
            HttpServletResponse response,
            int patientId,
            String message)
            throws ServletException, IOException {

        try {

            PatientViewModel patient =
                    loadPatientById(
                            patientId
                    );

            if (patient == null) {

                redirectToPatients(
                        request,
                        response
                );

                return;
            }

            request.setAttribute(
                    "patient",
                    patient
            );

            request.setAttribute(
                    "errorMessage",
                    message
            );

            request.getRequestDispatcher(
                    EDIT_PATIENT_PAGE
            ).forward(
                    request,
                    response
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            redirectToPatients(
                    request,
                    response
            );

        } catch (IOException exception) {

            redirectToPatients(
                    request,
                    response
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

            return "Please enter a valid "
                    + "email address.";
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

    private void redirectToPatients(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath()
                + "/patients"
        );
    }
}