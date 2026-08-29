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
import java.util.Collections;
import java.util.List;

@WebServlet("/dentists")
public class DentistsServlet extends HttpServlet {

    private static final String DENTISTS_PAGE =
            "/WEB-INF/views/dentists.jsp";

    private final ApiClient apiClient =
            new ApiClient();

    private final Gson gson =
            new Gson();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String jsonResponse =
                    apiClient.get(
                            "/api/dentists"
                    );

            Type dentistListType =
                    new TypeToken<
                            List<DentistViewModel>>() {
                    }.getType();

            List<DentistViewModel> dentists =
                    gson.fromJson(
                            jsonResponse,
                            dentistListType
                    );

            if (dentists == null) {

                dentists =
                        Collections.emptyList();
            }

            long activeDentistCount =
                    dentists.stream()
                            .filter(
                                    DentistViewModel::isActive
                            )
                            .count();

            request.setAttribute(
                    "dentists",
                    dentists
            );

            request.setAttribute(
                    "dentistCount",
                    dentists.size()
            );

            request.setAttribute(
                    "activeDentistCount",
                    activeDentistCount
            );

            request.getRequestDispatcher(
                    DENTISTS_PAGE
            ).forward(
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

            request.setAttribute(
                    "dentists",
                    Collections.emptyList()
            );

            request.setAttribute(
                    "dentistCount",
                    0
            );

            request.setAttribute(
                    "activeDentistCount",
                    0
            );

            request.setAttribute(
                    "errorMessage",
                    "Dentist service is temporarily unavailable."
            );

            request.getRequestDispatcher(
                    DENTISTS_PAGE
            ).forward(
                    request,
                    response
            );

        } catch (IOException | RuntimeException exception) {

            getServletContext().log(
                    "Unable to retrieve dentists from REST API.",
                    exception
            );

            request.setAttribute(
                    "dentists",
                    Collections.emptyList()
            );

            request.setAttribute(
                    "dentistCount",
                    0
            );

            request.setAttribute(
                    "activeDentistCount",
                    0
            );

            request.setAttribute(
                    "errorMessage",
                    "Unable to load dentist information."
            );

            request.getRequestDispatcher(
                    DENTISTS_PAGE
            ).forward(
                    request,
                    response
            );
        }
    }
}