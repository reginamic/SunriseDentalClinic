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

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/bills")
public class BillingServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE =
            "application/json";

    private static final String CHARACTER_ENCODING =
            "UTF-8";

    private final BillingService billingService =
            new BillingService();

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

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        configureJsonResponse(response);

        try {

            int billId =
                    parsePositiveInteger(
                            getTrimmedParameter(
                                    request,
                                    "billId"
                            ),
                            "Bill ID"
                    );

            String statusParameter =
                    getTrimmedParameter(
                            request,
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

    private BigDecimal parseOptionalAmount(
            HttpServletRequest request,
            String parameterName) {

        String value =
                getTrimmedParameter(
                        request,
                        parameterName
                );

        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }

        try {

            return new BigDecimal(value);

        } catch (NumberFormatException exception) {

            throw new IllegalArgumentException(
                    parameterName
                    + " must be a valid monetary amount."
            );
        }
    }

    private int parsePositiveInteger(
            String value,
            String fieldName) {

        if (value == null || value.isBlank()) {

            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        try {

            int parsedValue =
                    Integer.parseInt(value);

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

    private void writeBillJson(
            HttpServletResponse response,
            Bill bill)
            throws IOException {

        response.getWriter()
                .print(
                        toJson(bill)
                );
    }

    private void writeMessageWithBill(
            HttpServletResponse response,
            String message,
            Bill bill)
            throws IOException {

        response.getWriter()
                .print(
                        "{"
                        + "\"message\":\""
                        + escapeJson(message)
                        + "\","
                        + "\"bill\":"
                        + toJson(bill)
                        + "}"
                );
    }

    private String toJson(
            Bill bill) {

        StringBuilder json =
                new StringBuilder();

        json.append("{")
                .append("\"billId\":")
                .append(bill.getBillId())
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

    private void configureJsonResponse(
            HttpServletResponse response) {

        response.setContentType(
                JSON_CONTENT_TYPE
        );

        response.setCharacterEncoding(
                CHARACTER_ENCODING
        );
    }

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
                        + escapeJson(message)
                        + "\""
                        + "}"
                );
    }

    private void logError(
            String message,
            Exception exception) {

        getServletContext()
                .log(
                        message,
                        exception
                );
    }

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