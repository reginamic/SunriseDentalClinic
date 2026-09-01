
package com.sunrisedental.api.controller;

import com.sunrisedental.api.model.Bill;
import com.sunrisedental.api.model.BillItem;
import com.sunrisedental.api.model.BillStatus;
import com.sunrisedental.api.service.BillingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.math.BigDecimal;

import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@WebServlet("/api/bills")
public class BillingServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE =
            "application/json";

    private static final String CHARACTER_ENCODING =
            "UTF-8";


    private final BillingService billingService =
            new BillingService();


    // =========================================================
    // GET
    // =========================================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);


        try {

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


            // -------------------------------------------------
            // Search by bill number
            // -------------------------------------------------

            if (billNumber != null
                    && !billNumber.isBlank()) {

                Bill bill =
                        billingService
                                .getBillByNumber(
                                        billNumber
                                )
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Bill not found."
                                        )
                                );


                writeBillJson(
                        response,
                        bill
                );

                return;
            }


            // -------------------------------------------------
            // Search by appointment ID
            // -------------------------------------------------

            if (appointmentIdParameter != null
                    && !appointmentIdParameter.isBlank()) {

                int appointmentId =
                        parsePositiveInteger(
                                appointmentIdParameter,
                                "Appointment ID"
                        );


                Bill bill =
                        billingService
                                .getBillByAppointmentId(
                                        appointmentId
                                )
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Bill not found."
                                        )
                                );


                writeBillJson(
                        response,
                        bill
                );

                return;
            }


            // -------------------------------------------------
            // Get all bills
            // -------------------------------------------------

            List<Bill> bills =
                    billingService.getAllBills();


            writeBillList(
                    response,
                    bills
            );


        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );


        } catch (SQLException exception) {

            logError(
                    "Unable to retrieve bills.",
                    exception
            );


            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve bills."
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

        configureJsonResponse(response);


        try {

            int appointmentId =
                    parsePositiveInteger(
                            getTrimmedParameter(
                                    request,
                                    "appointmentId"
                            ),
                            "Appointment ID"
                    );


            int generatedBy =
                    parsePositiveInteger(
                            getTrimmedParameter(
                                    request,
                                    "generatedBy"
                            ),
                            "Generator user ID"
                    );


            BigDecimal additionalCharge =
                    parseOptionalAmount(
                            request,
                            "additionalCharge"
                    );


            BigDecimal discountAmount =
                    parseOptionalAmount(
                            request,
                            "discountAmount"
                    );


            Bill bill =
                    billingService.generateBill(
                            appointmentId,
                            generatedBy,
                            additionalCharge,
                            discountAmount
                    );


            response.setStatus(
                    HttpServletResponse.SC_CREATED
            );


            writeMessageWithBill(
                    response,
                    "Bill generated successfully.",
                    bill
            );


        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );


        } catch (SQLException exception) {

            logError(
                    "Unable to generate bill.",
                    exception
            );


            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to generate bill."
            );
        }
    }


    // =========================================================
    // PUT — UPDATE PAYMENT STATUS
    // =========================================================

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);


        try {

            /*
             * Tomcat does not reliably expose
             * application/x-www-form-urlencoded PUT request
             * bodies through request.getParameter().
             *
             * Therefore the PUT request body is parsed
             * manually.
             */
            Map<String, String> formData =
                    parseFormUrlEncodedBody(
                            request
                    );


            int billId =
                    parsePositiveInteger(
                            getTrimmedValue(
                                    formData,
                                    "billId"
                            ),
                            "Bill ID"
                    );


            String statusParameter =
                    getTrimmedValue(
                            formData,
                            "paymentStatus"
                    );


            if (statusParameter == null
                    || statusParameter.isBlank()) {

                throw new IllegalArgumentException(
                        "Payment status is required."
                );
            }


            BillStatus paymentStatus =
                    BillStatus.fromString(
                            statusParameter
                    );


            boolean updated =
                    billingService
                            .updatePaymentStatus(
                                    billId,
                                    paymentStatus
                            );


            if (!updated) {

                sendErrorResponse(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Bill not found."
                );

                return;
            }


            response.getWriter()
                    .print(
                            "{"
                            + "\"message\":"
                            + "\"Payment status updated successfully.\","
                            + "\"billId\":"
                            + billId
                            + ","
                            + "\"paymentStatus\":\""
                            + paymentStatus.name()
                            + "\""
                            + "}"
                    );


        } catch (IllegalArgumentException exception) {

            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );


        } catch (SQLException exception) {

            logError(
                    "Unable to update payment status.",
                    exception
            );


            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update payment status."
            );
        }
    }


    // =========================================================
    // PARSE FORM-URLENCODED PUT BODY
    // =========================================================

    private Map<String, String> parseFormUrlEncodedBody(
            HttpServletRequest request)
            throws IOException {

        Map<String, String> formData =
                new HashMap<>();


        StringBuilder body =
                new StringBuilder();


        try (
            BufferedReader reader =
                    request.getReader()
        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                body.append(line);
            }
        }


        if (body.length() == 0) {

            return formData;
        }


        String[] pairs =
                body.toString()
                        .split("&");


        for (String pair : pairs) {

            if (pair == null
                    || pair.isBlank()) {

                continue;
            }


            String[] keyValue =
                    pair.split(
                            "=",
                            2
                    );


            String key =
                    URLDecoder.decode(
                            keyValue[0],
                            StandardCharsets.UTF_8
                    );


            String value =
                    keyValue.length > 1
                            ? URLDecoder.decode(
                                    keyValue[1],
                                    StandardCharsets.UTF_8
                            )
                            : "";


            formData.put(
                    key,
                    value
            );
        }


        return formData;
    }


    // =========================================================
    // GET TRIMMED MAP VALUE
    // =========================================================

    private String getTrimmedValue(
            Map<String, String> formData,
            String parameterName) {

        String value =
                formData.get(
                        parameterName
                );


        return value == null
                ? null
                : value.trim();
    }


    // =========================================================
    // OPTIONAL MONEY
    // =========================================================

    private BigDecimal parseOptionalAmount(
            HttpServletRequest request,
            String parameterName) {

        String value =
                getTrimmedParameter(
                        request,
                        parameterName
                );


        if (value == null
                || value.isBlank()) {

            return BigDecimal.ZERO;
        }


        try {

            return new BigDecimal(
                    value
            );


        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    parameterName
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
    // NORMAL REQUEST PARAMETER
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
    // WRITE BILL LIST
    // =========================================================

    private void writeBillList(
            HttpServletResponse response,
            List<Bill> bills)
            throws IOException {

        PrintWriter writer =
                response.getWriter();


        writer.print("[");


        for (int index = 0;
             index < bills.size();
             index++) {

            if (index > 0) {

                writer.print(",");
            }


            writer.print(
                    toJson(
                            bills.get(index)
                    )
            );
        }


        writer.print("]");
    }


    // =========================================================
    // WRITE SINGLE BILL
    // =========================================================

    private void writeBillJson(
            HttpServletResponse response,
            Bill bill)
            throws IOException {

        response.getWriter()
                .print(
                        toJson(
                                bill
                        )
                );
    }


    // =========================================================
    // WRITE SUCCESS MESSAGE WITH BILL
    // =========================================================

    private void writeMessageWithBill(
            HttpServletResponse response,
            String message,
            Bill bill)
            throws IOException {

        response.getWriter()
                .print(
                        "{"
                        + "\"message\":\""
                        + escapeJson(
                                message
                        )
                        + "\","
                        + "\"bill\":"
                        + toJson(
                                bill
                        )
                        + "}"
                );
    }


    // =========================================================
    // BILL JSON
    // =========================================================

    private String toJson(
            Bill bill) {

        StringBuilder json =
                new StringBuilder();


        json.append("{")

                .append("\"billId\":")
                .append(
                        bill.getBillId()
                )
                .append(",")

                .append("\"billNumber\":\"")
                .append(
                        escapeJson(
                                bill.getBillNumber()
                        )
                )
                .append("\",")

                .append("\"appointmentId\":")
                .append(
                        bill.getAppointmentId()
                )
                .append(",")

                .append("\"subtotal\":")
                .append(
                        bill.getSubtotal()
                )
                .append(",")

                .append("\"additionalCharges\":")
                .append(
                        bill.getAdditionalCharges()
                )
                .append(",")

                .append("\"discountAmount\":")
                .append(
                        bill.getDiscountAmount()
                )
                .append(",")

                .append("\"totalAmount\":")
                .append(
                        bill.getTotalAmount()
                )
                .append(",")

                .append("\"paymentStatus\":\"")
                .append(
                        bill.getPaymentStatus() == null
                                ? ""
                                : bill
                                        .getPaymentStatus()
                                        .name()
                )
                .append("\",")

                .append("\"generatedBy\":")
                .append(
                        bill.getGeneratedBy()
                )
                .append(",")

                .append("\"items\":[");


        List<BillItem> items =
                bill.getItems();


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
    // RESPONSE CONFIGURATION
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
    // SERVER LOGGING
    // =========================================================

    private void logError(
            String message,
            Exception exception) {

        getServletContext()
                .log(
                        message,
                        exception
                );
    }


    // =========================================================
    // JSON ESCAPING
    // =========================================================

    private String escapeJson(
            String value) {

        if (value == null) {

            return "";
        }


        return value
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\r",
                        "\\r"
                )
                .replace(
                        "\t",
                        "\\t"
                );
    }
}