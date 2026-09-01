package com.sunrisedental.web.controller;

import com.google.gson.Gson;

import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.BillDetailsViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


/**
 * Presentation-tier controller for displaying
 * one complete patient bill / receipt.
 *
 * This servlet never accesses MySQL directly.
 * Complete receipt information is obtained from
 * the Sunrise Dental REST API through HTTP/JSON.
 */
@WebServlet("/bill-details")
public class BillDetailsServlet extends HttpServlet {

    private final ApiClient apiClient;

    private final Gson gson;


    public BillDetailsServlet() {

        this.apiClient =
                new ApiClient();

        this.gson =
                new Gson();
    }


    // =========================================================
    // GET — DISPLAY RECEIPT
    // =========================================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            int billId =
                    parsePositiveInteger(
                            request.getParameter(
                                    "billId"
                            ),
                            "Bill ID"
                    );


            // =================================================
            // REQUEST ENRICHED RECEIPT FROM API
            // =================================================

            String json =
                    apiClient.get(
                            "/api/bill-details"
                            + "?billId="
                            + billId
                    );


            BillDetailsViewModel bill =
                    gson.fromJson(
                            json,
                            BillDetailsViewModel.class
                    );


            if (bill == null
                    || bill.getBillId() <= 0) {

                throw new IllegalArgumentException(
                        "Bill details could not be found."
                );
            }


            // =================================================
            // JSP ATTRIBUTES
            // =================================================

            request.setAttribute(
                    "bill",
                    bill
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/bill-details.jsp"
            ).forward(
                    request,
                    response
            );


        } catch (IllegalArgumentException exception) {

            showError(
                    request,
                    response,
                    exception.getMessage()
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            showError(
                    request,
                    response,
                    "The billing service request was interrupted."
            );


        } catch (IOException exception) {

            showError(
                    request,
                    response,
                    createFriendlyApiError(
                            exception
                    )
            );
        }
    }


    // =========================================================
    // POSITIVE INTEGER VALIDATION
    // =========================================================

    private int parsePositiveInteger(
            String value,
            String fieldName) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    fieldName
                    + " is required."
            );
        }


        try {

            int parsedValue =
                    Integer.parseInt(
                            value.trim()
                    );


            if (parsedValue <= 0) {

                throw new IllegalArgumentException(
                        fieldName
                        + " must be greater than zero."
                );
            }


            return parsedValue;


        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    fieldName
                    + " must be a valid number."
            );
        }
    }


    // =========================================================
    // ERROR PAGE
    // =========================================================

    private void showError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message)
            throws ServletException, IOException {

        request.setAttribute(
                "errorMessage",
                message
        );


        request.getRequestDispatcher(
                "/WEB-INF/views/bill-details.jsp"
        ).forward(
                request,
                response
        );
    }


    // =========================================================
    // FRIENDLY API ERROR
    // =========================================================

    private String createFriendlyApiError(
            IOException exception) {

        String message =
                exception.getMessage();


        if (message == null
                || message.isBlank()) {

            return "Unable to communicate with the billing service.";
        }


        return message;
    }
}