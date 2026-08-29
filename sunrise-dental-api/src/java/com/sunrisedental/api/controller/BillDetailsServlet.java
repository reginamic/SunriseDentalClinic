package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.BillDetails;
import com.sunrisedental.api.model.BillItem;
import com.sunrisedental.api.service.BillingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.SQLException;

import java.util.List;


/**
 * REST endpoint that exposes enriched billing information
 * required by the Web application and printable receipts.
 *
 * Financial information comes from the persisted bill,
 * while patient, appointment, dentist and treatment details
 * are assembled by BillingService.
 */
@WebServlet("/api/bill-details")
public class BillDetailsServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE =
            "application/json";

    private static final String CHARACTER_ENCODING =
            "UTF-8";


    private final BillingService billingService =
            new BillingService();


    // =========================================================
    // GET ENRICHED BILL DETAILS
    // =========================================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(
                response
        );


        try {

            String billIdParameter =
                    getTrimmedParameter(
                            request,
                            "billId"
                    );


            String billNumber =
                    getTrimmedParameter(
                            request,
                            "billNumber"
                    );


            String appointmentIdParameter =
                    getTrimmedParameter(
                            request,
                            "appointmentId"
                    );


            BillDetails details;


            // =================================================
            // SEARCH BY BILL ID
            // =================================================

            if (billIdParameter != null
                    && !billIdParameter.isBlank()) {

                int billId =
                        parsePositiveInteger(
                                billIdParameter,
                                "Bill ID"
                        );


                details =
                        billingService
                                .getBillDetailsById(
                                        billId
                                )
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Bill not found."
                                        )
                                );


                writeBillDetailsJson(
                        response,
                        details
                );

                return;
            }


            // =================================================
            // SEARCH BY BILL NUMBER
            // =================================================

            if (billNumber != null
                    && !billNumber.isBlank()) {

                details =
                        billingService
                                .getBillDetailsByNumber(
                                        billNumber
                                )
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Bill not found."
                                        )
                                );


                writeBillDetailsJson(
                        response,
                        details
                );

                return;
            }


            // =================================================
            // SEARCH BY APPOINTMENT ID
            // =================================================

            if (appointmentIdParameter != null
                    && !appointmentIdParameter.isBlank()) {

                int appointmentId =
                        parsePositiveInteger(
                                appointmentIdParameter,
                                "Appointment ID"
                        );


                details =
                        billingService
                                .getBillDetailsByAppointmentId(
                                        appointmentId
                                )
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Bill not found."
                                        )
                                );


                writeBillDetailsJson(
                        response,
                        details
                );

                return;
            }


            throw new IllegalArgumentException(
                    "Bill ID, bill number or appointment ID "
                    + "is required."
            );


        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );


        } catch (SQLException exception) {

            getServletContext()
                    .log(
                            "Unable to retrieve enriched bill details.",
                            exception
                    );


            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve bill details."
            );
        }
    }


    // =========================================================
    // WRITE BILL DETAILS JSON
    // =========================================================

    private void writeBillDetailsJson(
            HttpServletResponse response,
            BillDetails details)
            throws IOException {

        response.getWriter()
                .print(
                        toJson(
                                details
                        )
                );
    }


    // =========================================================
    // COMPLETE RECEIPT JSON
    // =========================================================

    private String toJson(
            BillDetails details) {

        StringBuilder json =
                new StringBuilder();


        json.append("{")

                // ---------------------------------------------
                // Bill
                // ---------------------------------------------

                .append("\"billId\":")
                .append(
                        details.getBillId()
                )
                .append(",")

                .append("\"billNumber\":\"")
                .append(
                        escapeJson(
                                details.getBillNumber()
                        )
                )
                .append("\",")

                .append("\"subtotal\":")
                .append(
                        details.getSubtotal()
                )
                .append(",")

                .append("\"additionalCharges\":")
                .append(
                        details.getAdditionalCharges()
                )
                .append(",")

                .append("\"discountAmount\":")
                .append(
                        details.getDiscountAmount()
                )
                .append(",")

                .append("\"totalAmount\":")
                .append(
                        details.getTotalAmount()
                )
                .append(",")

                .append("\"paymentStatus\":\"")
                .append(
                        details.getPaymentStatus() == null
                                ? ""
                                : details
                                        .getPaymentStatus()
                                        .name()
                )
                .append("\",")

                .append("\"generatedBy\":")
                .append(
                        details.getGeneratedBy()
                )
                .append(",")

                .append("\"generatedAt\":\"")
                .append(
                        details.getGeneratedAt() == null
                                ? ""
                                : escapeJson(
                                        details
                                                .getGeneratedAt()
                                                .toString()
                                )
                )
                .append("\",")


                // ---------------------------------------------
                // Appointment
                // ---------------------------------------------

                .append("\"appointmentId\":")
                .append(
                        details.getAppointmentId()
                )
                .append(",")

                .append("\"appointmentNumber\":\"")
                .append(
                        escapeJson(
                                details.getAppointmentNumber()
                        )
                )
                .append("\",")

                .append("\"appointmentDate\":\"")
                .append(
                        details.getAppointmentDate() == null
                                ? ""
                                : details
                                        .getAppointmentDate()
                                        .toString()
                )
                .append("\",")

                .append("\"appointmentTime\":\"")
                .append(
                        details.getAppointmentTime() == null
                                ? ""
                                : details
                                        .getAppointmentTime()
                                        .toString()
                )
                .append("\",")

                .append("\"appointmentStatus\":\"")
                .append(
                        escapeJson(
                                details.getAppointmentStatus()
                        )
                )
                .append("\",")


                // ---------------------------------------------
                // Patient
                // ---------------------------------------------

                .append("\"patientCode\":\"")
                .append(
                        escapeJson(
                                details.getPatientCode()
                        )
                )
                .append("\",")

                .append("\"patientName\":\"")
                .append(
                        escapeJson(
                                details.getPatientName()
                        )
                )
                .append("\",")

                .append("\"patientAddress\":\"")
                .append(
                        escapeJson(
                                details.getPatientAddress()
                        )
                )
                .append("\",")

                .append("\"patientContactNumber\":\"")
                .append(
                        escapeJson(
                                details.getPatientContactNumber()
                        )
                )
                .append("\",")

                .append("\"patientEmail\":\"")
                .append(
                        escapeJson(
                                details.getPatientEmail()
                        )
                )
                .append("\",")


                // ---------------------------------------------
                // Dentist
                // ---------------------------------------------

                .append("\"dentistCode\":\"")
                .append(
                        escapeJson(
                                details.getDentistCode()
                        )
                )
                .append("\",")

                .append("\"dentistName\":\"")
                .append(
                        escapeJson(
                                details.getDentistName()
                        )
                )
                .append("\",")

                .append("\"dentistSpecialization\":\"")
                .append(
                        escapeJson(
                                details.getDentistSpecialization()
                        )
                )
                .append("\",")


                // ---------------------------------------------
                // Treatment
                // ---------------------------------------------

                .append("\"treatmentCode\":\"")
                .append(
                        escapeJson(
                                details.getTreatmentCode()
                        )
                )
                .append("\",")

                .append("\"treatmentName\":\"")
                .append(
                        escapeJson(
                                details.getTreatmentName()
                        )
                )
                .append("\",")


                // ---------------------------------------------
                // Persisted bill items
                // ---------------------------------------------

                .append("\"items\":[");


        List<BillItem> items =
                details.getItems();


        for (int index = 0;
             index < items.size();
             index++) {

            if (index > 0) {

                json.append(",");
            }


            json.append(
                    billItemToJson(
                            items.get(index)
                    )
            );
        }


        json.append("]}");


        return json.toString();
    }


    // =========================================================
    // BILL ITEM JSON
    // =========================================================

    private String billItemToJson(
            BillItem item) {

        return "{"

                + "\"billItemId\":"
                + item.getBillItemId()
                + ","

                + "\"itemName\":\""
                + escapeJson(
                        item.getItemName()
                )
                + "\","

                + "\"itemType\":\""
                + (
                    item.getItemType() == null
                            ? ""
                            : item
                                    .getItemType()
                                    .name()
                )
                + "\","

                + "\"quantity\":"
                + item.getQuantity()
                + ","

                + "\"unitPrice\":"
                + item.getUnitPrice()
                + ","

                + "\"totalPrice\":"
                + item.getTotalPrice()

                + "}";
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
                            value
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
    // REQUEST PARAMETER HELPER
    // =========================================================

    private String getTrimmedParameter(
            HttpServletRequest request,
            String parameterName) {

        String value =
                request.getParameter(
                        parameterName
                );


        return value == null
                ? null
                : value.trim();
    }


    // =========================================================
    // JSON RESPONSE CONFIGURATION
    // =========================================================

    private void configureJsonResponse(
            HttpServletResponse response) {

        response.setContentType(
                JSON_CONTENT_TYPE
        );


        response.setCharacterEncoding(
                CHARACTER_ENCODING
        );
    }


    // =========================================================
    // ERROR RESPONSE
    // =========================================================

    private void sendErrorResponse(
            HttpServletResponse response,
            int statusCode,
            String message)
            throws IOException {

        response.setStatus(
                statusCode
        );


        response.getWriter()
                .print(
                        "{"
                        + "\"error\":\""
                        + escapeJson(
                                message
                        )
                        + "\""
                        + "}"
                );
    }


    // =========================================================
    // JSON ESCAPE
    // =========================================================

    private String escapeJson(
            String value) {

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