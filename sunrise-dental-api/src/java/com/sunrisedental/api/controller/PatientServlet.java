package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.Patient;
import com.sunrisedental.api.service.PatientService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "PatientServlet", urlPatterns = {"/api/patients"})
public class PatientServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String CHARACTER_ENCODING = "UTF-8";

    private final PatientService patientService = new PatientService();


    // GET
    // View all patients or search patients
  
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {
            String searchKeyword = request.getParameter("search");

            List<Patient> patients;

            if (searchKeyword != null && !searchKeyword.isBlank()) {
                patients = patientService.searchPatients(
                        searchKeyword.trim()
                );
            } else {
                patients = patientService.getAllPatients();
            }

            response.setStatus(HttpServletResponse.SC_OK);

            writePatientList(response, patients);

        } catch (SQLException e) {

            logError(
                    "Patient retrieval failed",
                    e
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve patients."
            );
        }
    }

    
    // POST
    // Register a new patient
  
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {
            Patient patient = buildPatientFromRequest(request);

            Patient savedPatient =
                    patientService.registerPatient(patient);

            response.setStatus(
                    HttpServletResponse.SC_CREATED
            );

            try (PrintWriter out = response.getWriter()) {

                out.print("{");
                out.print(
                        "\"message\":\"Patient registered successfully\","
                );
                out.print(
                        "\"patientId\":"
                        + savedPatient.getPatientId()
                        + ","
                );
                out.print(
                        "\"patientCode\":\""
                        + escapeJson(savedPatient.getPatientCode())
                        + "\","
                );
                out.print(
                        "\"fullName\":\""
                        + escapeJson(savedPatient.getFullName())
                        + "\""
                );
                out.print("}");
            }

        } catch (DateTimeParseException e) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Date of birth must use YYYY-MM-DD format."
            );

        } catch (IllegalArgumentException e) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (SQLException e) {

            logError(
                    "Patient registration failed",
                    e
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to register patient."
            );
        }
    }

    
    // PUT
    // Update an existing patient
   
    @Override
    protected void doPut(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {
            int patientId = parsePatientId(
                    request.getParameter("patientId")
            );

            Patient patient =
                    buildPatientFromRequest(request);

            patient.setPatientId(patientId);

            boolean updated =
                    patientService.updatePatient(patient);

            if (!updated) {
                sendErrorResponse(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Patient was not found."
                );
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);

            try (PrintWriter out = response.getWriter()) {

                out.print("{");
                out.print(
                        "\"message\":\"Patient updated successfully\","
                );
                out.print(
                        "\"patientId\":"
                        + patient.getPatientId()
                );
                out.print("}");
            }

        } catch (NumberFormatException e) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Patient ID must be a valid number."
            );

        } catch (DateTimeParseException e) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Date of birth must use YYYY-MM-DD format."
            );

        } catch (IllegalArgumentException e) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (SQLException e) {

            logError(
                    "Patient update failed",
                    e
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update patient."
            );
        }
    }

   
    // Build Patient object from HTTP request
   
    private Patient buildPatientFromRequest(
            HttpServletRequest request) {

        Patient patient = new Patient();

        patient.setFullName(
                getTrimmedParameter(
                        request,
                        "fullName"
                )
        );

        patient.setAddress(
                getTrimmedParameter(
                        request,
                        "address"
                )
        );

        patient.setContactNumber(
                getTrimmedParameter(
                        request,
                        "contactNumber"
                )
        );

        patient.setEmail(
                getTrimmedParameter(
                        request,
                        "email"
                )
        );

        patient.setGender(
                getTrimmedParameter(
                        request,
                        "gender"
                )
        );

        String dateOfBirth =
                getTrimmedParameter(
                        request,
                        "dateOfBirth"
                );

        if (dateOfBirth != null
                && !dateOfBirth.isBlank()) {

            patient.setDateOfBirth(
                    LocalDate.parse(dateOfBirth)
            );
        }

        return patient;
    }

    
    // Parse and validate Patient ID
   
    private int parsePatientId(String patientIdValue) {

        if (patientIdValue == null
                || patientIdValue.isBlank()) {

            throw new IllegalArgumentException(
                    "Patient ID is required."
            );
        }

        int patientId =
                Integer.parseInt(
                        patientIdValue.trim()
                );

        if (patientId <= 0) {

            throw new IllegalArgumentException(
                    "Patient ID must be greater than zero."
            );
        }

        return patientId;
    }

    
    // Read and trim HTTP parameter
    
    private String getTrimmedParameter(
            HttpServletRequest request,
            String parameterName) {

        String value =
                request.getParameter(parameterName);

        if (value == null) {
            return null;
        }

        return value.trim();
    }

    
    // Write Patient List as JSON
   
    private void writePatientList(
            HttpServletResponse response,
            List<Patient> patients)
            throws IOException {

        try (PrintWriter out = response.getWriter()) {

            out.print("[");

            for (int i = 0; i < patients.size(); i++) {

                writePatientJson(
                        out,
                        patients.get(i)
                );

                if (i < patients.size() - 1) {
                    out.print(",");
                }
            }

            out.print("]");
        }
    }

    
    // Write one Patient as JSON
  
    private void writePatientJson(
            PrintWriter out,
            Patient patient) {

        out.print("{");

        out.print(
                "\"patientId\":"
                + patient.getPatientId()
                + ","
        );

        out.print(
                "\"patientCode\":\""
                + escapeJson(patient.getPatientCode())
                + "\","
        );

        out.print(
                "\"fullName\":\""
                + escapeJson(patient.getFullName())
                + "\","
        );

        out.print(
                "\"address\":\""
                + escapeJson(patient.getAddress())
                + "\","
        );

        out.print(
                "\"contactNumber\":\""
                + escapeJson(patient.getContactNumber())
                + "\","
        );

        if (patient.getEmail() != null) {

            out.print(
                    "\"email\":\""
                    + escapeJson(patient.getEmail())
                    + "\","
            );

        } else {

            out.print("\"email\":null,");
        }

        if (patient.getDateOfBirth() != null) {

            out.print(
                    "\"dateOfBirth\":\""
                    + patient.getDateOfBirth()
                    + "\","
            );

        } else {

            out.print("\"dateOfBirth\":null,");
        }

        if (patient.getGender() != null) {

            out.print(
                    "\"gender\":\""
                    + escapeJson(patient.getGender())
                    + "\""
            );

        } else {

            out.print("\"gender\":null");
        }

        out.print("}");
    }

    
    // Standard JSON response configuration
  
    private void configureJsonResponse(
            HttpServletResponse response) {

        response.setContentType(
                JSON_CONTENT_TYPE
        );

        response.setCharacterEncoding(
                CHARACTER_ENCODING
        );
    }

   
    // Standard JSON error response
    
    private void sendErrorResponse(
            HttpServletResponse response,
            int status,
            String message)
            throws IOException {

        response.setStatus(status);

        try (PrintWriter out = response.getWriter()) {

            out.print(
                    "{\"error\":\""
                    + escapeJson(message)
                    + "\"}"
            );
        }
    }

    
    // Simple server-side error logging
   
    private void logError(
            String message,
            Exception exception) {

        System.err.println(
                message
                + ": "
                + exception.getMessage()
        );
    }

   
    // Basic JSON escaping
   
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