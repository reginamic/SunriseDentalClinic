package com.sunrisedental.web.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.DentistViewModel;

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

@WebServlet("/dentists/edit")
public class EditDentistServlet extends HttpServlet {

    private static final String EDIT_DENTIST_PAGE =
            "/WEB-INF/views/dentist-edit.jsp";

    private final ApiClient apiClient =
            new ApiClient();

    private final Gson gson =
            new Gson();


    /*
     * =========================================================
     * GET
     * Load existing dentist information
     * =========================================================
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/dentists"
            );

            return;
        }

        String dentistIdParameter =
                request.getParameter("id");

        if (dentistIdParameter == null
                || dentistIdParameter.isBlank()) {

            redirectToDentists(
                    request,
                    response
            );

            return;
        }

        try {

            int dentistId =
                    Integer.parseInt(
                            dentistIdParameter
                    );

            if (dentistId <= 0) {

                redirectToDentists(
                        request,
                        response
                );

                return;
            }

            DentistViewModel dentist =
                    loadDentistById(
                            dentistId
                    );

            if (dentist == null) {

                redirectToDentists(
                        request,
                        response
                );

                return;
            }

            request.setAttribute(
                    "dentist",
                    dentist
            );

            request.getRequestDispatcher(
                    EDIT_DENTIST_PAGE
            ).forward(
                    request,
                    response
            );

        } catch (NumberFormatException exception) {

            redirectToDentists(
                    request,
                    response
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "Dentist API request was interrupted.",
                    exception
            );

            redirectToDentists(
                    request,
                    response
            );

        } catch (IOException | RuntimeException exception) {

            getServletContext().log(
                    "Unable to retrieve dentist "
                    + "from REST API.",
                    exception
            );

            redirectToDentists(
                    request,
                    response
            );
        }
    }


    /*
     * =========================================================
     * POST
     * Web form submits here.
     *
     * This servlet then sends HTTP PUT
     * to the REST API.
     * =========================================================
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Server-side authorization.
         */
        if (!isAdmin(request)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );

            return;
        }

        String dentistIdValue =
                normalize(
                        request.getParameter(
                                "dentistId"
                        )
                );

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

        String active =
                normalize(
                        request.getParameter(
                                "active"
                        )
                );

        int dentistId;

        try {

            dentistId =
                    Integer.parseInt(
                            dentistIdValue
                    );

        } catch (NumberFormatException exception) {

            redirectToDentists(
                    request,
                    response
            );

            return;
        }

        if (dentistId <= 0) {

            redirectToDentists(
                    request,
                    response
            );

            return;
        }

        String validationError =
                validateDentist(
                        fullName,
                        specialization,
                        contactNumber,
                        email,
                        active
                );

        if (validationError != null) {

            showEditError(
                    request,
                    response,
                    dentistId,
                    validationError
            );

            return;
        }

        try {

            Map<String, String> dentistData =
                    new HashMap<>();

            dentistData.put(
                    "dentistId",
                    String.valueOf(dentistId)
            );

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

            dentistData.put(
                    "active",
                    active
            );

            /*
             * Distributed update:
             *
             * Web Form
             *      ↓
             * EditDentistServlet
             *      ↓ HTTP PUT
             * Dentist REST API
             *      ↓
             * DentistService
             *      ↓
             * Repository
             *      ↓
             * MySQL
             */
            apiClient.put(
                    "/api/dentists",
                    dentistData
            );

            /*
             * Post / Redirect / Get pattern.
             */
            response.sendRedirect(
                    request.getContextPath()
                    + "/dentists?updated=true"
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "Dentist update request "
                    + "was interrupted.",
                    exception
            );

            showEditError(
                    request,
                    response,
                    dentistId,
                    "Dentist update service is "
                    + "temporarily unavailable."
            );

        } catch (IOException exception) {

            getServletContext().log(
                    "Unable to update dentist "
                    + "through REST API.",
                    exception
            );

            showEditError(
                    request,
                    response,
                    dentistId,
                    "Unable to update the dentist. "
                    + "Please verify the information "
                    + "and try again."
            );
        }
    }


    /*
     * =========================================================
     * Load dentist from REST API
     * =========================================================
     */
    private DentistViewModel loadDentistById(
            int dentistId)
            throws IOException, InterruptedException {

        String jsonResponse =
                apiClient.get(
                        "/api/dentists"
                );

        if (jsonResponse == null
                || jsonResponse.isBlank()) {

            return null;
        }

        Type dentistListType =
                new TypeToken<
                        List<DentistViewModel>>() {
                }.getType();

        List<DentistViewModel> dentists =
                gson.fromJson(
                        jsonResponse,
                        dentistListType
                );

        if (dentists == null
                || dentists.isEmpty()) {

            return null;
        }

        for (DentistViewModel dentist
                : dentists) {

            if (dentist != null
                    && dentist.getDentistId()
                    == dentistId) {

                return dentist;
            }
        }

        return null;
    }


    /*
     * =========================================================
     * Validation
     * =========================================================
     */
    private String validateDentist(
            String fullName,
            String specialization,
            String contactNumber,
            String email,
            String active) {

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

            return "Please enter a valid email address.";
        }

        if (!"true".equalsIgnoreCase(active)
                && !"false".equalsIgnoreCase(active)) {

            return "Please select a valid dentist status.";
        }

        return null;
    }


    /*
     * =========================================================
     * Display error and reload dentist
     * =========================================================
     */
    private void showEditError(
            HttpServletRequest request,
            HttpServletResponse response,
            int dentistId,
            String message)
            throws ServletException, IOException {

        try {

            DentistViewModel dentist =
                    loadDentistById(
                            dentistId
                    );

            if (dentist == null) {

                redirectToDentists(
                        request,
                        response
                );

                return;
            }

            request.setAttribute(
                    "dentist",
                    dentist
            );

            request.setAttribute(
                    "errorMessage",
                    message
            );

            request.getRequestDispatcher(
                    EDIT_DENTIST_PAGE
            ).forward(
                    request,
                    response
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            redirectToDentists(
                    request,
                    response
            );

        } catch (IOException exception) {

            redirectToDentists(
                    request,
                    response
            );
        }
    }


    /*
     * =========================================================
     * ADMIN authorization helper
     * =========================================================
     */
    private boolean isAdmin(
            HttpServletRequest request) {

        Object role =
                request.getSession()
                        .getAttribute("role");

        return role != null
                && "ADMIN".equalsIgnoreCase(
                        String.valueOf(role)
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


    private void redirectToDentists(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath()
                + "/dentists"
        );
    }
}