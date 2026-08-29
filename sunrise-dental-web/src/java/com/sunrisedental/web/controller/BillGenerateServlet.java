package com.sunrisedental.web.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.AppointmentViewModel;
import com.sunrisedental.web.model.BillViewModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.math.BigDecimal;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Presentation-tier controller for generating patient bills.
 *
 * Only completed appointments that do not already have a bill
 * are offered for selection.
 *
 * The logged-in user's session ID is used as generatedBy.
 * The Web application never accepts generatedBy from
 * user-editable form data.
 */
@WebServlet("/bill-generate")
public class BillGenerateServlet extends HttpServlet {

    private final ApiClient apiClient;

    private final Gson gson;


    public BillGenerateServlet() {

        this.apiClient =
                new ApiClient();

        this.gson =
                new Gson();
    }


    // =========================================================
    // GET — SHOW GENERATE BILL FORM
    // =========================================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            List<AppointmentViewModel> appointments =
                    getAvailableCompletedAppointments();


            request.setAttribute(
                    "appointments",
                    appointments
            );


            String selectedAppointmentId =
                    trimToNull(
                            request.getParameter(
                                    "appointmentId"
                            )
                    );


            request.setAttribute(
                    "selectedAppointmentId",
                    selectedAppointmentId
            );


            request.getRequestDispatcher(
                    "/WEB-INF/views/bill-generate.jsp"
            ).forward(
                    request,
                    response
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            showFormError(
                    request,
                    response,
                    "The billing service request was interrupted."
            );


        } catch (IOException exception) {

            showFormError(
                    request,
                    response,
                    createFriendlyApiError(
                            exception
                    )
            );
        }
    }


    // =========================================================
    // POST — GENERATE BILL
    // =========================================================

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            int appointmentId =
                    parsePositiveInteger(
                            request.getParameter(
                                    "appointmentId"
                            ),
                            "Appointment"
                    );


            BigDecimal additionalCharge =
                    parseNonNegativeAmount(
                            request.getParameter(
                                    "additionalCharge"
                            ),
                            "Additional charge"
                    );


            BigDecimal discountAmount =
                    parseNonNegativeAmount(
                            request.getParameter(
                                    "discountAmount"
                            ),
                            "Discount amount"
                    );


            // =================================================
            // TRUSTED SESSION USER
            // =================================================

            int generatedBy =
                    getLoggedInUserId(
                            request
                    );


            /*
             * Defense in depth:
             * verify the selected appointment is still
             * completed and unbilled immediately before
             * sending the generation request.
             */
            AppointmentViewModel selectedAppointment =
                    findAvailableAppointment(
                            appointmentId
                    );


            if (selectedAppointment == null) {

                throw new IllegalArgumentException(
                        "The selected appointment is not available "
                        + "for billing. It may already be billed "
                        + "or may no longer be completed."
                );
            }


            // =================================================
            // CALL BILLING REST API
            // =================================================

            String json =
                    apiClient.post(
                            "/api/bills",
                            Map.of(
                                    "appointmentId",
                                    String.valueOf(
                                            appointmentId
                                    ),

                                    "generatedBy",
                                    String.valueOf(
                                            generatedBy
                                    ),

                                    "additionalCharge",
                                    additionalCharge
                                            .toPlainString(),

                                    "discountAmount",
                                    discountAmount
                                            .toPlainString()
                            )
                    );


            // =================================================
            // READ GENERATED BILL ID
            // =================================================

            JsonObject root =
                    JsonParser
                            .parseString(
                                    json
                            )
                            .getAsJsonObject();


            if (!root.has("bill")
                    || !root
                            .get("bill")
                            .isJsonObject()) {

                throw new IOException(
                        "The billing service generated a response "
                        + "without bill details."
                );
            }


            JsonObject billJson =
                    root.getAsJsonObject(
                            "bill"
                    );


            if (!billJson.has("billId")) {

                throw new IOException(
                        "The generated bill ID was not returned."
                );
            }


            int billId =
                    billJson
                            .get("billId")
                            .getAsInt();


            if (billId <= 0) {

                throw new IOException(
                        "The generated bill ID is invalid."
                );
            }


            // =================================================
            // REDIRECT TO RECEIPT
            // =================================================

            response.sendRedirect(
                    request.getContextPath()
                    + "/bill-details?billId="
                    + billId
            );


        } catch (IllegalArgumentException exception) {

            preserveSubmittedValues(
                    request
            );


            showFormError(
                    request,
                    response,
                    exception.getMessage()
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            preserveSubmittedValues(
                    request
            );


            showFormError(
                    request,
                    response,
                    "The billing service request was interrupted."
            );


        } catch (IOException exception) {

            preserveSubmittedValues(
                    request
            );


            showFormError(
                    request,
                    response,
                    createFriendlyApiError(
                            exception
                    )
            );
        }
    }


    // =========================================================
    // AVAILABLE COMPLETED APPOINTMENTS
    // =========================================================

    private List<AppointmentViewModel>
            getAvailableCompletedAppointments()
            throws IOException, InterruptedException {

        String appointmentJson =
                apiClient.get(
                        "/api/appointments"
                );


        AppointmentViewModel[] appointmentArray =
                gson.fromJson(
                        appointmentJson,
                        AppointmentViewModel[].class
                );


        if (appointmentArray == null) {

            return Collections.emptyList();
        }


        String billsJson =
                apiClient.get(
                        "/api/bills"
                );


        BillViewModel[] billArray =
                gson.fromJson(
                        billsJson,
                        BillViewModel[].class
                );


        Set<Integer> billedAppointmentIds =
                new HashSet<>();


        if (billArray != null) {

            Arrays.stream(
                    billArray
            )
                    .forEach(
                            bill ->
                                    billedAppointmentIds.add(
                                            bill.getAppointmentId()
                                    )
                    );
        }


        return Arrays.stream(
                        appointmentArray
                )
                .filter(
                        AppointmentViewModel::isCompleted
                )
                .filter(
                        appointment ->
                                !billedAppointmentIds.contains(
                                        appointment
                                                .getAppointmentId()
                                )
                )
                .toList();
    }


    // =========================================================
    // FIND ONE AVAILABLE APPOINTMENT
    // =========================================================

    private AppointmentViewModel findAvailableAppointment(
            int appointmentId)
            throws IOException, InterruptedException {

        return getAvailableCompletedAppointments()
                .stream()
                .filter(
                        appointment ->
                                appointment
                                        .getAppointmentId()
                                        == appointmentId
                )
                .findFirst()
                .orElse(
                        null
                );
    }


    // =========================================================
    // TRUSTED SESSION USER ID
    // =========================================================

    private int getLoggedInUserId(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(
                        false
                );


        if (session == null) {

            throw new IllegalArgumentException(
                    "Your login session has expired. "
                    + "Please sign in again."
            );
        }


        Object userIdObject =
                session.getAttribute(
                        "userId"
                );


        if (userIdObject == null) {

            throw new IllegalArgumentException(
                    "Unable to identify the logged-in user."
            );
        }


        try {

            int userId =
                    Integer.parseInt(
                            userIdObject.toString()
                    );


            if (userId <= 0) {

                throw new NumberFormatException();
            }


            return userId;


        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    "The logged-in user ID is invalid."
            );
        }
    }


    // =========================================================
    // MONEY VALIDATION
    // =========================================================

    private BigDecimal parseNonNegativeAmount(
            String value,
            String fieldName) {

        String trimmed =
                trimToNull(
                        value
                );


        if (trimmed == null) {

            return BigDecimal.ZERO;
        }


        try {

            BigDecimal amount =
                    new BigDecimal(
                            trimmed
                    );


            if (amount.compareTo(
                    BigDecimal.ZERO
            ) < 0) {

                throw new IllegalArgumentException(
                        fieldName
                        + " cannot be negative."
                );
            }


            return amount;


        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    fieldName
                    + " must be a valid monetary amount."
            );
        }
    }


    // =========================================================
    // POSITIVE INTEGER
    // =========================================================

    private int parsePositiveInteger(
            String value,
            String fieldName) {

        String trimmed =
                trimToNull(
                        value
                );


        if (trimmed == null) {

            throw new IllegalArgumentException(
                    fieldName
                    + " is required."
            );
        }


        try {

            int parsedValue =
                    Integer.parseInt(
                            trimmed
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
    // PRESERVE FORM VALUES
    // =========================================================

    private void preserveSubmittedValues(
            HttpServletRequest request) {

        request.setAttribute(
                "selectedAppointmentId",
                trimToNull(
                        request.getParameter(
                                "appointmentId"
                        )
                )
        );


        request.setAttribute(
                "submittedAdditionalCharge",
                trimToNull(
                        request.getParameter(
                                "additionalCharge"
                        )
                )
        );


        request.setAttribute(
                "submittedDiscountAmount",
                trimToNull(
                        request.getParameter(
                                "discountAmount"
                        )
                )
        );
    }


    // =========================================================
    // SHOW FORM WITH ERROR
    // =========================================================

    private void showFormError(
            HttpServletRequest request,
            HttpServletResponse response,
            String errorMessage)
            throws ServletException, IOException {

        request.setAttribute(
                "errorMessage",
                errorMessage
        );


        try {

            request.setAttribute(
                    "appointments",
                    getAvailableCompletedAppointments()
            );


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();

            request.setAttribute(
                    "appointments",
                    Collections.emptyList()
            );


        } catch (IOException exception) {

            request.setAttribute(
                    "appointments",
                    Collections.emptyList()
            );
        }


        request.getRequestDispatcher(
                "/WEB-INF/views/bill-generate.jsp"
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


    // =========================================================
    // STRING HELPER
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