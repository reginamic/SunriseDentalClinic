package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.Patient;
import com.sunrisedental.api.service.PatientService;

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

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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


    /*
     * =========================================================
     * GET
     * View all patients or search patients
     * =========================================================
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {

            String searchKeyword =
                    request.getParameter(
                            "search"
                    );

            List<Patient> patients;

            if (searchKeyword != null
                    && !searchKeyword.isBlank()) {

                patients =
                        patientService.searchPatients(
                                searchKeyword.trim()
                        );

            } else {

                patients =
                        patientService.getAllPatients();
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


    /*
     * =========================================================
     * POST
     * Register a new patient
     * =========================================================
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {

            Patient patient =
                    buildPatientFromRequest(
                            request
                    );

            Patient savedPatient =
                    patientService.registerPatient(
                            patient
                    );

            response.setStatus(
                    HttpServletResponse.SC_CREATED
            );

            try (PrintWriter out =
                    response.getWriter()) {

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
                    "Date of birth must use "
                    + "YYYY-MM-DD format."
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


    /*
     * =========================================================
     * PUT
     * Update an existing patient
     *
     * IMPORTANT:
     *
     * The Web application sends:
     *
     * application/x-www-form-urlencoded
     *
     * Tomcat does not always expose form parameters from
     * PUT requests using request.getParameter().
     *
     * Therefore this method manually reads and parses
     * the PUT request body.
     * =========================================================
     */
    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {

            /*
             * Read all values sent in the PUT body.
             */
            Map<String, String> putData =
                    parsePutFormData(
                            request
                    );

            /*
             * Read and validate Patient ID.
             */
            int patientId =
                    parsePatientId(
                            getPutValue(
                                    putData,
                                    "patientId"
                            )
                    );

            /*
             * Build Patient object using
             * the edited values.
             */
            Patient patient =
                    buildPatientFromPutData(
                            putData
                    );

            patient.setPatientId(
                    patientId
            );

            /*
             * Business layer update.
             */
            boolean updated =
                    patientService.updatePatient(
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

            try (PrintWriter out =
                    response.getWriter()) {

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
                    "Date of birth must use "
                    + "YYYY-MM-DD format."
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


    /*
     * =========================================================
     * BUILD PATIENT FROM NORMAL HTTP REQUEST
     *
     * Used by POST when registering a patient.
     * =========================================================
     */
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


    /*
     * =========================================================
     * BUILD PATIENT FROM PUT DATA
     *
     * Used when updating an existing patient.
     * =========================================================
     */
    private Patient buildPatientFromPutData(
            Map<String, String> putData) {

        Patient patient =
                new Patient();

        patient.setFullName(
                getPutValue(
                        putData,
                        "fullName"
                )
        );

        patient.setAddress(
                getPutValue(
                        putData,
                        "address"
                )
        );

        patient.setContactNumber(
                getPutValue(
                        putData,
                        "contactNumber"
                )
        );

        patient.setEmail(
                getPutValue(
                        putData,
                        "email"
                )
        );

        patient.setGender(
                getPutValue(
                        putData,
                        "gender"
                )
        );

        String dateOfBirth =
                getPutValue(
                        putData,
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


    /*
     * =========================================================
     * PARSE PUT FORM DATA
     *
     * Example body:
     *
     * patientId=3
     * &fullName=Tharushi+Perera
     * &address=Galle+Fort%2C+Sri+Lanka
     *
     * =========================================================
     */
    private Map<String, String> parsePutFormData(
            HttpServletRequest request)
            throws IOException {

        Map<String, String> formData =
                new HashMap<>();

        /*
         * Read the raw PUT request body.
         */
        byte[] bodyBytes =
                request.getInputStream()
                        .readAllBytes();

        /*
         * Nothing was sent.
         */
        if (bodyBytes.length == 0) {

            return formData;
        }

        String requestBody =
                new String(
                        bodyBytes,
                        StandardCharsets.UTF_8
                );

        if (requestBody.isBlank()) {

            return formData;
        }

        /*
         * Form-urlencoded data separates
         * each value using &.
         */
        String[] pairs =
                requestBody.split("&");

        for (String pair : pairs) {

            if (pair == null
                    || pair.isBlank()) {

                continue;
            }

            /*
             * Split only on the first "=".
             */
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

            String value = "";

            if (parts.length > 1) {

                value =
                        URLDecoder.decode(
                                parts[1],
                                StandardCharsets.UTF_8
                        );
            }

            formData.put(
                    key,
                    value
            );
        }

        return formData;
    }


    /*
     * =========================================================
     * GET VALUE FROM PUT DATA
     * =========================================================
     */
    private String getPutValue(
            Map<String, String> putData,
            String parameterName) {

        if (putData == null
                || parameterName == null) {

            return null;
        }

        String value =
                putData.get(
                        parameterName
                );

        if (value == null) {

            return null;
        }

        return value.trim();
    }


    /*
     * =========================================================
     * PARSE AND VALIDATE PATIENT ID
     * =========================================================
     */
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


    /*
     * =========================================================
     * READ AND TRIM NORMAL HTTP PARAMETER
     * =========================================================
     */
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


    /*
     * =========================================================
     * WRITE PATIENT LIST AS JSON
     * =========================================================
     */
    private void writePatientList(
            HttpServletResponse response,
            List<Patient> patients)
            throws IOException {

        try (PrintWriter out =
                response.getWriter()) {

            out.print("[");

            for (int i = 0;
                    i < patients.size();
                    i++) {

                writePatientJson(
                        out,
                        patients.get(i)
                );

                if (i
                        < patients.size() - 1) {

                    out.print(",");
                }
            }

            out.print("]");
        }
    }


    /*
     * =========================================================
     * WRITE ONE PATIENT AS JSON
     * =========================================================
     */
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

        if (patient.getDateOfBirth()
                != null) {

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


    /*
     * =========================================================
     * STANDARD JSON RESPONSE CONFIGURATION
     * =========================================================
     */
    private void configureJsonResponse(
            HttpServletResponse response) {

        response.setContentType(
                JSON_CONTENT_TYPE
        );

        response.setCharacterEncoding(
                CHARACTER_ENCODING
        );
    }


    /*
     * =========================================================
     * STANDARD JSON ERROR RESPONSE
     * =========================================================
     */
    private void sendErrorResponse(
            HttpServletResponse response,
            int status,
            String message)
            throws IOException {

        response.setStatus(
                status
        );

        try (PrintWriter out =
                response.getWriter()) {

            out.print(
                    "{\"error\":\""
                    + escapeJson(
                            message
                    )
                    + "\"}"
            );
        }
    }


    /*
     * =========================================================
     * SERVER-SIDE ERROR LOGGING
     * =========================================================
     */
    private void logError(
            String message,
            Exception exception) {

        System.err.println(
                message
                + ": "
                + exception.getMessage()
        );
    }


    /*
     * =========================================================
     * BASIC JSON ESCAPING
     * =========================================================
     */
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