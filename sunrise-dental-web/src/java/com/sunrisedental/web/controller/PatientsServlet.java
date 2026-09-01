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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/patients")
public class PatientsServlet extends HttpServlet {

    private static final String PATIENTS_PAGE =
            "/WEB-INF/views/patients.jsp";

    private final ApiClient apiClient =
            new ApiClient();

    private final Gson gson =
            new Gson();

    /**
     * Displays the patient management page.
     *
     * Patient information is retrieved from the
     * Sunrise Dental API through HTTP/JSON.
     *
     * The Web application never connects directly
     * to the MySQL database.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String jsonResponse =
                    apiClient.get(
                            "/api/patients"
                    );

            List<PatientViewModel> patients =
                    parsePatients(
                            jsonResponse
                    );

            request.setAttribute(
                    "patients",
                    patients
            );

            request.setAttribute(
                    "patientCount",
                    patients.size()
            );

            request.getRequestDispatcher(
                    PATIENTS_PAGE
            ).forward(
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

            showErrorPage(
                    request,
                    response,
                    "The patient service is temporarily unavailable."
            );

        } catch (IOException exception) {

            getServletContext().log(
                    "Unable to retrieve patients from API.",
                    exception
            );

            showErrorPage(
                    request,
                    response,
                    "Unable to load patient records at this time."
            );

        } catch (RuntimeException exception) {

            getServletContext().log(
                    "Unable to process patient API response.",
                    exception
            );

            showErrorPage(
                    request,
                    response,
                    "Patient information could not be processed."
            );
        }
    }

    /**
     * Converts the JSON array returned by
     * /api/patients into Web view models.
     */
    private List<PatientViewModel> parsePatients(
            String jsonResponse) {

        if (jsonResponse == null
                || jsonResponse.isBlank()) {

            return new ArrayList<>();
        }

        Type patientListType =
                new TypeToken<List<PatientViewModel>>() {
                }.getType();

        List<PatientViewModel> patients =
                gson.fromJson(
                        jsonResponse,
                        patientListType
                );

        if (patients == null) {
            return new ArrayList<>();
        }

        return patients;
    }

    /**
     * Allows the same JSP to display a professional
     * error message if the REST API is unavailable.
     */
    private void showErrorPage(
            HttpServletRequest request,
            HttpServletResponse response,
            String message)
            throws ServletException, IOException {

        request.setAttribute(
                "errorMessage",
                message
        );

        request.setAttribute(
                "patients",
                new ArrayList<PatientViewModel>()
        );

        request.setAttribute(
                "patientCount",
                0
        );

        request.getRequestDispatcher(
                PATIENTS_PAGE
        ).forward(
                request,
                response
        );
    }
}