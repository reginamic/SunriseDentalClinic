package com.sunrisedental.web.controller;

import com.google.gson.Gson;

import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.BillViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.math.BigDecimal;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Web controller for Billing Management.
 *
 * This servlet belongs to the Presentation Tier.
 *
 * It never accesses MySQL directly. All billing data is
 * retrieved from the Sunrise Dental REST API through
 * ApiClient using HTTP/JSON.
 */
@WebServlet("/bills")
public class BillsServlet extends HttpServlet {

    private final ApiClient apiClient;

    private final Gson gson;


    public BillsServlet() {

        this.apiClient =
                new ApiClient();

        this.gson =
                new Gson();
    }


    // =========================================================
    // GET — LIST / SEARCH BILLS
    // =========================================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String billNumber =
                    trimToNull(
                            request.getParameter(
                                    "billNumber"
                            )
                    );


            String appointmentIdText =
                    trimToNull(
                            request.getParameter(
                                    "appointmentId"
                            )
                    );


            String paymentStatus =
                    trimToNull(
                            request.getParameter(
                                    "paymentStatus"
                            )
                    );


            List<BillViewModel> bills;


            // =================================================
            // EXACT BILL NUMBER SEARCH
            // =================================================

            if (billNumber != null) {

                bills =
                        searchByBillNumber(
                                billNumber
                        );


            // =================================================
            // APPOINTMENT ID SEARCH
            // =================================================

            } else if (appointmentIdText != null) {

                bills =
                        searchByAppointmentId(
                                appointmentIdText
                        );


            // =================================================
            // ALL BILLS
            // =================================================

            } else {

                bills =
                        getAllBills();
            }


            // =================================================
            // OPTIONAL PAYMENT STATUS FILTER
            // =================================================

            if (paymentStatus != null) {

                bills =
                        bills.stream()
                                .filter(
                                        bill ->
                                                bill.getPaymentStatus()
                                                        != null
                                                && bill
                                                        .getPaymentStatus()
                                                        .equalsIgnoreCase(
                                                                paymentStatus
                                                        )
                                )
                                .toList();
            }


            // =================================================
            // BILLING STATISTICS
            // =================================================

            long totalBills =
                    bills.size();


            long paidBills =
                    bills.stream()
                            .filter(
                                    BillViewModel::isPaid
                            )
                            .count();


            long unpaidBills =
                    bills.stream()
                            .filter(
                                    BillViewModel::isUnpaid
                            )
                            .count();


            BigDecimal totalBilledAmount =
                    bills.stream()
                            .map(
                                    BillViewModel::getTotalAmount
                            )
                            .filter(
                                    amount ->
                                            amount != null
                            )
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );


            BigDecimal totalPaidAmount =
                    bills.stream()
                            .filter(
                                    BillViewModel::isPaid
                            )
                            .map(
                                    BillViewModel::getTotalAmount
                            )
                            .filter(
                                    amount ->
                                            amount != null
                            )
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );


            BigDecimal outstandingAmount =
                    bills.stream()
                            .filter(
                                    BillViewModel::isUnpaid
                            )
                            .map(
                                    BillViewModel::getTotalAmount
                            )
                            .filter(
                                    amount ->
                                            amount != null
                            )
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );


            // =================================================
            // JSP ATTRIBUTES
            // =================================================

            request.setAttribute(
                    "bills",
                    bills
            );


            request.setAttribute(
                    "totalBills",
                    totalBills
            );


            request.setAttribute(
                    "paidBills",
                    paidBills
            );


            request.setAttribute(
                    "unpaidBills",
                    unpaidBills
            );


            request.setAttribute(
                    "totalBilledAmount",
                    totalBilledAmount
            );


            request.setAttribute(
                    "totalPaidAmount",
                    totalPaidAmount
            );


            request.setAttribute(
                    "outstandingAmount",
                    outstandingAmount
            );


            // =================================================
            // PRESERVE SEARCH / FILTER VALUES
            // =================================================

            request.setAttribute(
                    "selectedBillNumber",
                    billNumber
            );


            request.setAttribute(
                    "selectedAppointmentId",
                    appointmentIdText
            );


            request.setAttribute(
                    "selectedPaymentStatus",
                    paymentStatus
            );


            // =================================================
            // SUCCESS MESSAGE AFTER REDIRECT
            // =================================================

            String success =
                    trimToNull(
                            request.getParameter(
                                    "success"
                            )
                    );


            if (success != null) {

                request.setAttribute(
                        "successMessage",
                        success
                );
            }


            // =================================================
            // FORWARD TO JSP
            // =================================================

            request.getRequestDispatcher(
                    "/WEB-INF/views/bills.jsp"
            ).forward(
                    request,
                    response
            );


        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );


            showEmptyPage(
                    request,
                    response
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            request.setAttribute(
                    "errorMessage",
                    "The billing service request was interrupted."
            );


            showEmptyPage(
                    request,
                    response
            );


        } catch (IOException exception) {

            request.setAttribute(
                    "errorMessage",
                    createFriendlyApiError(
                            exception
                    )
            );


            showEmptyPage(
                    request,
                    response
            );
        }
    }


    // =========================================================
    // GET ALL BILLS
    // =========================================================

    private List<BillViewModel> getAllBills()
            throws IOException, InterruptedException {

        String json =
                apiClient.get(
                        "/api/bills"
                );


        BillViewModel[] result =
                gson.fromJson(
                        json,
                        BillViewModel[].class
                );


        if (result == null) {

            return Collections.emptyList();
        }


        return Arrays.asList(
                result
        );
    }


    // =========================================================
    // SEARCH BY BILL NUMBER
    // =========================================================

    private List<BillViewModel> searchByBillNumber(
            String billNumber)
            throws IOException, InterruptedException {

        String encodedBillNumber =
                URLEncoder.encode(
                        billNumber.trim(),
                        StandardCharsets.UTF_8
                );


        String json =
                apiClient.get(
                        "/api/bills"
                        + "?billNumber="
                        + encodedBillNumber
                );


        /*
         * Exact bill-number search returns one JSON object,
         * not an array.
         */
        BillViewModel bill =
                gson.fromJson(
                        json,
                        BillViewModel.class
                );


        if (bill == null
                || bill.getBillId() <= 0) {

            return Collections.emptyList();
        }


        return List.of(
                bill
        );
    }


    // =========================================================
    // SEARCH BY APPOINTMENT ID
    // =========================================================

    private List<BillViewModel> searchByAppointmentId(
            String appointmentIdText)
            throws IOException, InterruptedException {

        int appointmentId;


        try {

            appointmentId =
                    Integer.parseInt(
                            appointmentIdText
                    );


        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "Appointment ID must be a valid number."
            );
        }


        if (appointmentId <= 0) {

            throw new IllegalArgumentException(
                    "Appointment ID must be greater than zero."
            );
        }


        String json =
                apiClient.get(
                        "/api/bills"
                        + "?appointmentId="
                        + appointmentId
                );


        /*
         * Appointment lookup also returns a single bill object.
         */
        BillViewModel bill =
                gson.fromJson(
                        json,
                        BillViewModel.class
                );


        if (bill == null
                || bill.getBillId() <= 0) {

            return Collections.emptyList();
        }


        return List.of(
                bill
        );
    }


    // =========================================================
    // EMPTY / ERROR PAGE PREPARATION
    // =========================================================

    private void showEmptyPage(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute(
                "bills",
                Collections.emptyList()
        );


        request.setAttribute(
                "totalBills",
                0L
        );


        request.setAttribute(
                "paidBills",
                0L
        );


        request.setAttribute(
                "unpaidBills",
                0L
        );


        request.setAttribute(
                "totalBilledAmount",
                BigDecimal.ZERO
        );


        request.setAttribute(
                "totalPaidAmount",
                BigDecimal.ZERO
        );


        request.setAttribute(
                "outstandingAmount",
                BigDecimal.ZERO
        );


        request.getRequestDispatcher(
                "/WEB-INF/views/bills.jsp"
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


        /*
         * Keep useful API validation messages visible while
         * preventing internal Java exceptions from reaching
         * the JSP.
         */
        return message;
    }


    // =========================================================
    // GENERAL STRING HELPER
    // =========================================================

    private String trimToNull(
            String value) {

        if (value == null) {

            return null;
        }


        String trimmed =
                value.trim();


        return trimmed.isEmpty()
                ? null
                : trimmed;
    }
}