package com.sunrisedental.web.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.TreatmentViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@WebServlet("/treatments")
public class TreatmentsServlet extends HttpServlet {

    private static final String TREATMENTS_PAGE =
            "/WEB-INF/views/treatments.jsp";

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
                            "/api/treatments"
                    );

            Type treatmentListType =
                    new TypeToken<
                            List<TreatmentViewModel>>() {
                    }.getType();

            List<TreatmentViewModel> treatments =
                    gson.fromJson(
                            jsonResponse,
                            treatmentListType
                    );

            if (treatments == null) {

                treatments =
                        Collections.emptyList();
            }

            long activeTreatmentCount =
                    treatments.stream()
                            .filter(
                                    TreatmentViewModel::isActive
                            )
                            .count();

            BigDecimal highestTreatmentPrice =
                    treatments.stream()
                            .map(
                                    TreatmentViewModel
                                            ::getTreatmentPrice
                            )
                            .max(
                                    BigDecimal::compareTo
                            )
                            .orElse(
                                    BigDecimal.ZERO
                            );

            request.setAttribute(
                    "treatments",
                    treatments
            );

            request.setAttribute(
                    "treatmentCount",
                    treatments.size()
            );

            request.setAttribute(
                    "activeTreatmentCount",
                    activeTreatmentCount
            );

            request.setAttribute(
                    "highestTreatmentPrice",
                    highestTreatmentPrice
            );

            request.getRequestDispatcher(
                    TREATMENTS_PAGE
            ).forward(
                    request,
                    response
            );

        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            getServletContext().log(
                    "Treatment API request was interrupted.",
                    exception
            );

            showErrorPage(
                    request,
                    response,
                    "Treatment service is temporarily unavailable."
            );

        } catch (IOException | RuntimeException exception) {

            getServletContext().log(
                    "Unable to retrieve treatments "
                    + "from REST API.",
                    exception
            );

            showErrorPage(
                    request,
                    response,
                    "Unable to load treatment information."
            );
        }
    }

    private void showErrorPage(
            HttpServletRequest request,
            HttpServletResponse response,
            String message)
            throws ServletException, IOException {

        request.setAttribute(
                "treatments",
                Collections.emptyList()
        );

        request.setAttribute(
                "treatmentCount",
                0
        );

        request.setAttribute(
                "activeTreatmentCount",
                0L
        );

        request.setAttribute(
                "highestTreatmentPrice",
                BigDecimal.ZERO
        );

        request.setAttribute(
                "errorMessage",
                message
        );

        request.getRequestDispatcher(
                TREATMENTS_PAGE
        ).forward(
                request,
                response
        );
    }
}