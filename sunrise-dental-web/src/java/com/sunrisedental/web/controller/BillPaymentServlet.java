package com.sunrisedental.web.controller;

import com.google.gson.Gson;

import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.BillDetailsViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.util.Map;


/**
 * Presentation-tier controller for recording bill payment.
 *
 * GET displays a confirmation page only.
 * POST performs the actual state-changing payment operation.
 *
 * No direct database access is performed by the Web tier.
 */
@WebServlet("/bill-payment")
public class BillPaymentServlet extends HttpServlet {

    private final ApiClient apiClient;

    private final Gson gson;


    public BillPaymentServlet() {

        this.apiClient =
                new ApiClient();

        this.gson =
                new Gson();
    }


    // =========================================================
    // GET — SHOW PAYMENT CONFIRMATION
    // =========================================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            requireAuthenticatedSession(
                    request
            );


            int billId =
                    parsePositiveInteger(
                            request.getParameter(
                                    "billId"
                            ),
                            "Bill ID"
                    );


            BillDetailsViewModel bill =
                    getBillDetails(
                            billId
                    );


            /*
             * A paid bill must not be presented as if another
             * payment can be recorded.
             */
            if (bill.isPaid()) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/bill-details?billId="
                        + billId
                );

                return;
            }


            request.setAttribute(
                    "bill",
                    bill
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/bill-payment.jsp"
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
    // POST — RECORD PAYMENT
    // =========================================================

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            requireAuthenticatedSession(
                    request
            );


            int billId =
                    parsePositiveInteger(
                            request.getParameter(
                                    "billId"
                            ),
                            "Bill ID"
                    );


            /*
             * Reload trusted billing information from the API
             * immediately before updating the payment state.
             */
            BillDetailsViewModel bill =
                    getBillDetails(
                            billId
                    );


            if (bill.isPaid()) {

                throw new IllegalArgumentException(
                        "This bill has already been paid."
                );
            }


            if (!bill.isUnpaid()) {

                throw new IllegalArgumentException(
                        "Only unpaid bills can be marked as paid."
                );
            }


            // =================================================
            // CALL BILLING API
            // =================================================

            apiClient.put(
                    "/api/bills",
                    Map.of(
                            "billId",
                            String.valueOf(
                                    billId
                            ),

                            "paymentStatus",
                            "PAID"
                    )
            );


            // =================================================
            // SUCCESS
            // =================================================

            response.sendRedirect(
                    request.getContextPath()
                    + "/bills?success="
                    + java.net.URLEncoder.encode(
                            "Payment recorded successfully for "
                            + bill.getBillNumber()
                            + ".",
                            java.nio.charset.StandardCharsets.UTF_8
                    )
            );


        } catch (IllegalArgumentException exception) {

            showPostError(
                    request,
                    response,
                    exception.getMessage()
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            showPostError(
                    request,
                    response,
                    "The billing service request was interrupted."
            );


        } catch (IOException exception) {

            showPostError(
                    request,
                    response,
                    createFriendlyApiError(
                            exception
                    )
            );
        }
    }


    // =========================================================
    // LOAD ENRICHED BILL
    // =========================================================

    private BillDetailsViewModel getBillDetails(
            int billId)
            throws IOException, InterruptedException {

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
                    "Bill could not be found."
            );
        }


        return bill;
    }


    // =========================================================
    // SESSION SECURITY
    // =========================================================

    private void requireAuthenticatedSession(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(
                        false
                );


        if (session == null
                || session.getAttribute(
                        "userId"
                ) == null) {

            throw new IllegalArgumentException(
                    "Your login session has expired. "
                    + "Please sign in again."
            );
        }


        String role =
                session.getAttribute(
                        "role"
                ) == null
                        ? ""
                        : session.getAttribute(
                                "role"
                        ).toString();


        /*
         * Both clinic roles are intentionally permitted to
         * perform normal billing operations.
         */
        if (!"ADMIN".equalsIgnoreCase(
                role
        )
                && !"RECEPTIONIST".equalsIgnoreCase(
                        role
                )) {

            throw new IllegalArgumentException(
                    "You do not have permission "
                    + "to record payments."
            );
        }
    }


    // =========================================================
    // POSITIVE INTEGER
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

            int parsed =
                    Integer.parseInt(
                            value.trim()
                    );


            if (parsed <= 0) {

                throw new IllegalArgumentException(
                        fieldName
                        + " must be greater than zero."
                );
            }


            return parsed;


        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    fieldName
                    + " must be a valid number."
            );
        }
    }


    // =========================================================
    // GET ERROR
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
                "/WEB-INF/views/bill-payment.jsp"
        ).forward(
                request,
                response
        );
    }


    // =========================================================
    // POST ERROR
    // =========================================================

    private void showPostError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message)
            throws ServletException, IOException {

        request.setAttribute(
                "errorMessage",
                message
        );


        try {

            String billIdValue =
                    request.getParameter(
                            "billId"
                    );


            if (billIdValue != null
                    && !billIdValue.isBlank()) {

                int billId =
                        Integer.parseInt(
                                billIdValue
                        );


                if (billId > 0) {

                    request.setAttribute(
                            "bill",
                            getBillDetails(
                                    billId
                            )
                    );
                }
            }


        } catch (Exception ignored) {

            /*
             * Preserve the original payment error.
             */
        }


        request.getRequestDispatcher(
                "/WEB-INF/views/bill-payment.jsp"
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