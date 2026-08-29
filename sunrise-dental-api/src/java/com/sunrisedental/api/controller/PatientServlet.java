package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.Patient;
import com.sunrisedental.api.service.PatientService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.sql.SQLException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@WebServlet(
        name = "PatientServlet",
        urlPatterns = {"/api/patients"}
)
public class PatientServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE =
            "application/json";

    private static final String CHARACTER_ENCODING =
            "UTF-8";


    private final PatientService patientService =
            new PatientService();


    // =========================================================
    // GET
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

            String searchKeyword =
                    request.getParameter(
                            "search"
                    );


            List<Patient> patients;


            if (searchKeyword != null
                    && !searchKeyword.isBlank()) {

                patients =
                        patientService
                                .searchPatients(
                                        searchKeyword.trim()
                                );

            } else {

                patients =
                        patientService
                                .getAllPatients();
            }


            response.setStatus(
                    HttpServletResponse.SC_OK
            );


            writePatientList(
                    response,
                    patients
            );


        } catch (SQLException exception) {

            logError(
                    "Patient retrieval failed",
                    exception
            );


            sendErrorResponse(
                    response,
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve patients."
            );
        }
    }


    // =========================================================
    // POST
    // =========================================================

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(
                response
        );


        try {

            Patient patient =
                    buildPatientFromRequest(
                            request
                    );


            Patient savedPatient =
                    patientService
                            .registerPatient(
                                    patient
                            );


            response.setStatus(
                    HttpServletResponse.SC_CREATED
            );


            try (
                    PrintWriter out =
                            response.getWriter()
            ) {

                out.print("{");

                out.print(
                        "\"message\":"
                        + "\"Patient registered successfully\","
                );

                out.print(
                        "\"patientId\":"
                        + savedPatient.getPatientId()
                        + ","
                );

                out.print(
                        "\"patientCode\":\""
                        + escapeJson(
                                savedPatient
                                        .getPatientCode()
                        )
                        + "\","
                );

                out.print(
                        "\"fullName\":\""
                        + escapeJson(
                                savedPatient
                                        .getFullName()
                        )
                        + "\""
                );

                out.print("}");
            }


        } catch (DateTimeParseException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Date of birth must use YYYY-MM-DD format."
            );


        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );


        } catch (SQLException exception) {

            logError(
                    "Patient registration failed",
                    exception
            );


            sendErrorResponse(
                    response,
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR,
                    "Unable to register patient."
            );
        }
    }


    // =========================================================
    // PUT
    // =========================================================

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(
                response
        );


        try {

            /*
             * IMPORTANT:
             *
             * Tomcat does not reliably make
             * application/x-www-form-urlencoded PUT fields
             * available through request.getParameter().
             *
             * Therefore we manually parse the PUT request body.
             */
            Map<String, String> parameters =
                    parseFormEncodedBody(
                            request
                    );


            int patientId =
                    parsePatientId(
                            getTrimmedParameter(
                                    parameters,
                                    "patientId"
                            )
                    );


            Patient patient =
                    buildPatientFromParameters(
                            parameters
                    );


            patient.setPatientId(
                    patientId
            );


            boolean updated =
                    patientService
                            .updatePatient(
                                    patient
                            );


            if (!updated) {

                sendErrorResponse(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Patient was not found."
                );

                return;
            }


            response.setStatus(
                    HttpServletResponse.SC_OK
            );


            try (
                    PrintWriter out =
                            response.getWriter()
            ) {

                out.print("{");

                out.print(
                        "\"message\":"
                        + "\"Patient updated successfully\","
                );

                out.print(
                        "\"patientId\":"
                        + patient.getPatientId()
                );

                out.print("}");
            }


        } catch (NumberFormatException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Patient ID must be a valid number."
            );


        } catch (DateTimeParseException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Date of birth must use YYYY-MM-DD format."
            );


        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );


        } catch (SQLException exception) {

            logError(
                    "Patient update failed",
                    exception
            );


            sendErrorResponse(
                    response,
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR,
                    "Unable to update patient."
            );
        }
    }


    // =========================================================
    // BUILD PATIENT FROM POST REQUEST
    // =========================================================

    private Patient buildPatientFromRequest(
            HttpServletRequest request) {

        Patient patient =
                new Patient();


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
                    LocalDate.parse(
                            dateOfBirth
                    )
            );
        }


        return patient;
    }


    // =========================================================
    // BUILD PATIENT FROM PUT PARAMETERS
    // =========================================================

    private Patient buildPatientFromParameters(
            Map<String, String> parameters) {

        Patient patient =
                new Patient();


        patient.setFullName(
                getTrimmedParameter(
                        parameters,
                        "fullName"
                )
        );


        patient.setAddress(
                getTrimmedParameter(
                        parameters,
                        "address"
                )
        );


        patient.setContactNumber(
                getTrimmedParameter(
                        parameters,
                        "contactNumber"
                )
        );


        patient.setEmail(
                getTrimmedParameter(
                        parameters,
                        "email"
                )
        );


        patient.setGender(
                getTrimmedParameter(
                        parameters,
                        "gender"
                )
        );


        String dateOfBirth =
                getTrimmedParameter(
                        parameters,
                        "dateOfBirth"
                );


        if (dateOfBirth != null
                && !dateOfBirth.isBlank()) {

            patient.setDateOfBirth(
                    LocalDate.parse(
                            dateOfBirth
                    )
            );
        }


        return patient;
    }


    // =========================================================
    // PARSE FORM-URLENCODED PUT BODY
    // =========================================================

    private Map<String, String> parseFormEncodedBody(
            HttpServletRequest request)
            throws IOException {

        Map<String, String> parameters =
                new HashMap<>();


        StringBuilder body =
                new StringBuilder();


        try (
                BufferedReader reader =
                        request.getReader()
        ) {

            String line;


            while ((line = reader.readLine())
                    != null) {

                body.append(
                        line
                );
            }
        }


        if (body.length() == 0) {

            return parameters;
        }


        String[] pairs =
                body.toString()
                        .split("&");


        for (String pair : pairs) {

            if (pair == null
                    || pair.isBlank()) {

                continue;
            }


            String[] parts =
                    pair.split(
                            "=",
                            2
                    );


            String key =
                    URLDecoder.decode(
                            parts[0],
                            StandardCharsets.UTF_8
                    );


            String value =
                    parts.length > 1
                            ? URLDecoder.decode(
                                    parts[1],
                                    StandardCharsets.UTF_8
                            )
                            : "";


            parameters.put(
                    key,
                    value
            );
        }


        return parameters;
    }


    // =========================================================
    // PATIENT ID VALIDATION
    // =========================================================

    private int parsePatientId(
            String patientIdValue) {

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


    // =========================================================
    // NORMAL REQUEST PARAMETER
    // =========================================================

    private String getTrimmedParameter(
            HttpServletRequest request,
            String parameterName) {

        String value =
                request.getParameter(
                        parameterName
                );


        if (value == null) {

            return null;
        }


        return value.trim();
    }


    // =========================================================
    // PUT MAP PARAMETER
    // =========================================================

    private String getTrimmedParameter(
            Map<String, String> parameters,
            String parameterName) {

        String value =
                parameters.get(
                        parameterName
                );


        if (value == null) {

            return null;
        }


        return value.trim();
    }


    // =========================================================
    // PATIENT LIST JSON
    // =========================================================

    private void writePatientList(
            HttpServletResponse response,
            List<Patient> patients)
            throws IOException {

        try (
                PrintWriter out =
                        response.getWriter()
        ) {

            out.print("[");


            for (int index = 0;
                 index < patients.size();
                 index++) {

                writePatientJson(
                        out,
                        patients.get(index)
                );


                if (index
                        < patients.size() - 1) {

                    out.print(",");
                }
            }


            out.print("]");
        }
    }


    // =========================================================
    // SINGLE PATIENT JSON
    // =========================================================

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
                + escapeJson(
                        patient.getPatientCode()
                )
                + "\","
        );


        out.print(
                "\"fullName\":\""
                + escapeJson(
                        patient.getFullName()
                )
                + "\","
        );


        out.print(
                "\"address\":\""
                + escapeJson(
                        patient.getAddress()
                )
                + "\","
        );


        out.print(
                "\"contactNumber\":\""
                + escapeJson(
                        patient.getContactNumber()
                )
                + "\","
        );


        if (patient.getEmail() != null) {

            out.print(
                    "\"email\":\""
                    + escapeJson(
                            patient.getEmail()
                    )
                    + "\","
            );

        } else {

            out.print(
                    "\"email\":null,"
            );
        }


        if (patient.getDateOfBirth() != null) {

            out.print(
                    "\"dateOfBirth\":\""
                    + patient.getDateOfBirth()
                    + "\","
            );

        } else {

            out.print(
                    "\"dateOfBirth\":null,"
            );
        }


        if (patient.getGender() != null) {

            out.print(
                    "\"gender\":\""
                    + escapeJson(
                            patient.getGender()
                    )
                    + "\""
            );

        } else {

            out.print(
                    "\"gender\":null"
            );
        }


        out.print("}");
    }


    // =========================================================
    // JSON RESPONSE CONFIG
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
            int status,
            String message)
            throws IOException {

        response.setStatus(
                status
        );


        try (
                PrintWriter out =
                        response.getWriter()
        ) {

            out.print(
                    "{\"error\":\""
                    + escapeJson(
                            message
                    )
                    + "\"}"
            );
        }
    }


    // =========================================================
    // LOGGING
    // =========================================================

    private void logError(
            String message,
            Exception exception) {

        getServletContext()
                .log(
                        message,
                        exception
                );
    }


    // =========================================================
    // JSON ESCAPE
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
                        "\n",
                        "\\n"
                )
                .replace(
                        "\r",
                        "\\r"
                )
                .replace(
                        "\t",
                        "\\t"
                );
    }
}