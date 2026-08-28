package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.Dentist;
import com.sunrisedental.api.service.DentistService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DentistServlet", urlPatterns = {"/api/dentists"})
public class DentistServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String CHARACTER_ENCODING = "UTF-8";

    private final DentistService dentistService = new DentistService();

   
    // GET
    // View all dentists or search dentists
   
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {
            String searchKeyword = request.getParameter("search");

            List<Dentist> dentists;

            if (searchKeyword != null && !searchKeyword.isBlank()) {
                dentists = dentistService.searchDentists(
                        searchKeyword.trim()
                );
            } else {
                dentists = dentistService.getAllDentists();
            }

            response.setStatus(HttpServletResponse.SC_OK);

            writeDentistList(response, dentists);

        } catch (SQLException e) {

            logError(
                    "Dentist retrieval failed",
                    e
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve dentists."
            );
        }
    }

   
    // POST
    // Register a new dentist
   
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {
            Dentist dentist = buildDentistFromRequest(request);

            Dentist savedDentist =
                    dentistService.registerDentist(dentist);

            response.setStatus(
                    HttpServletResponse.SC_CREATED
            );

            try (PrintWriter out = response.getWriter()) {

                out.print("{");

                out.print(
                        "\"message\":\"Dentist registered successfully\","
                );

                out.print(
                        "\"dentistId\":"
                        + savedDentist.getDentistId()
                        + ","
                );

                out.print(
                        "\"dentistCode\":\""
                        + escapeJson(savedDentist.getDentistCode())
                        + "\","
                );

                out.print(
                        "\"fullName\":\""
                        + escapeJson(savedDentist.getFullName())
                        + "\""
                );

                out.print("}");
            }

        } catch (IllegalArgumentException e) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (SQLException e) {

            logError(
                    "Dentist registration failed",
                    e
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to register dentist."
            );
        }
    }

   
    // PUT
    // Update an existing dentist

    @Override
    protected void doPut(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {
            int dentistId = parseDentistId(
                    request.getParameter("dentistId")
            );

            Dentist dentist =
                    buildDentistFromRequest(request);

            dentist.setDentistId(dentistId);

            String activeValue =
                    request.getParameter("active");

            if (activeValue != null
                    && !activeValue.isBlank()) {

                dentist.setActive(
                        Boolean.parseBoolean(
                                activeValue.trim()
                        )
                );

            } else {

                Dentist existingDentist =
                        dentistService
                                .getDentistById(dentistId)
                                .orElseThrow(
                                        () -> new IllegalArgumentException(
                                                "Dentist was not found."
                                        )
                                );

                dentist.setActive(
                        existingDentist.isActive()
                );
            }

            boolean updated =
                    dentistService.updateDentist(dentist);

            if (!updated) {

                sendErrorResponse(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Dentist was not found."
                );

                return;
            }

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            try (PrintWriter out = response.getWriter()) {

                out.print("{");

                out.print(
                        "\"message\":\"Dentist updated successfully\","
                );

                out.print(
                        "\"dentistId\":"
                        + dentist.getDentistId()
                );

                out.print("}");
            }

        } catch (NumberFormatException e) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Dentist ID must be a valid number."
            );

        } catch (IllegalArgumentException e) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (SQLException e) {

            logError(
                    "Dentist update failed",
                    e
            );

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update dentist."
            );
        }
    }

    
    // Build Dentist object from HTTP request
   
    private Dentist buildDentistFromRequest(
            HttpServletRequest request) {

        Dentist dentist = new Dentist();

        dentist.setFullName(
                getTrimmedParameter(
                        request,
                        "fullName"
                )
        );

        dentist.setSpecialization(
                getTrimmedParameter(
                        request,
                        "specialization"
                )
        );

        dentist.setContactNumber(
                getTrimmedParameter(
                        request,
                        "contactNumber"
                )
        );

        dentist.setEmail(
                getTrimmedParameter(
                        request,
                        "email"
                )
        );

        return dentist;
    }

   
    // Parse Dentist ID

    private int parseDentistId(String dentistIdValue) {

        if (dentistIdValue == null
                || dentistIdValue.isBlank()) {

            throw new IllegalArgumentException(
                    "Dentist ID is required."
            );
        }

        int dentistId =
                Integer.parseInt(
                        dentistIdValue.trim()
                );

        if (dentistId <= 0) {

            throw new IllegalArgumentException(
                    "Dentist ID must be greater than zero."
            );
        }

        return dentistId;
    }

   
    // Read and trim request parameter
   
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

   
    // Write Dentist list as JSON
 
    private void writeDentistList(
            HttpServletResponse response,
            List<Dentist> dentists)
            throws IOException {

        try (PrintWriter out = response.getWriter()) {

            out.print("[");

            for (int i = 0; i < dentists.size(); i++) {

                writeDentistJson(
                        out,
                        dentists.get(i)
                );

                if (i < dentists.size() - 1) {
                    out.print(",");
                }
            }

            out.print("]");
        }
    }

   
    // Write one Dentist as JSON
   
    private void writeDentistJson(
            PrintWriter out,
            Dentist dentist) {

        out.print("{");

        out.print(
                "\"dentistId\":"
                + dentist.getDentistId()
                + ","
        );

        out.print(
                "\"dentistCode\":\""
                + escapeJson(dentist.getDentistCode())
                + "\","
        );

        out.print(
                "\"fullName\":\""
                + escapeJson(dentist.getFullName())
                + "\","
        );

        out.print(
                "\"specialization\":\""
                + escapeJson(dentist.getSpecialization())
                + "\","
        );

        if (dentist.getContactNumber() != null) {

            out.print(
                    "\"contactNumber\":\""
                    + escapeJson(dentist.getContactNumber())
                    + "\","
            );

        } else {

            out.print("\"contactNumber\":null,");
        }

        if (dentist.getEmail() != null) {

            out.print(
                    "\"email\":\""
                    + escapeJson(dentist.getEmail())
                    + "\","
            );

        } else {

            out.print("\"email\":null,");
        }

        out.print(
                "\"active\":"
                + dentist.isActive()
        );

        out.print("}");
    }

   
    // Standard JSON configuration
    
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

   
    // Server-side error logging
    
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