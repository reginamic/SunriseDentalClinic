<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="com.sunrisedental.web.model.BillDetailsViewModel" %>
<%@ page import="com.sunrisedental.web.model.BillViewModel" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>


<%!
    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }


    private String formatMoney(BigDecimal amount) {

        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        DecimalFormat formatter =
                new DecimalFormat("#,##0.00");

        return formatter.format(amount);
    }


    private String formatDateTime(String value) {

        if (value == null || value.isBlank()) {
            return "-";
        }

        return value.replace("T", " ");
    }


    private String displayValue(String value) {

        if (value == null || value.isBlank()) {
            return "-";
        }

        return escapeHtml(value);
    }
%>


<%
    String contextPath =
            request.getContextPath();


    BillDetailsViewModel bill =
            (BillDetailsViewModel)
                    request.getAttribute(
                            "bill"
                    );


    String errorMessage =
            request.getAttribute(
                    "errorMessage"
            ) == null
                    ? ""
                    : request.getAttribute(
                            "errorMessage"
                    ).toString();


    String fullName =
            session.getAttribute(
                    "fullName"
            ) == null
                    ? ""
                    : session.getAttribute(
                            "fullName"
                    ).toString();


    String role =
            session.getAttribute(
                    "role"
            ) == null
                    ? ""
                    : session.getAttribute(
                            "role"
                    ).toString();


    List<BillViewModel.BillItemViewModel> items =
            bill == null
                    || bill.getItems() == null
                    ? Collections.emptyList()
                    : bill.getItems();
%>


<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta
            name="viewport"
            content="width=device-width, initial-scale=1.0">

    <title>
        Patient Receipt | Sunrise Dental Clinic
    </title>


    <style>

        * {
            box-sizing: border-box;
        }


        body {
            margin: 0;
            font-family:
                    "Segoe UI",
                    Arial,
                    sans-serif;
            background: #f4f7fb;
            color: #1f2937;
        }


        .page-container {
            min-height: 100vh;
            display: flex;
        }


        /* =====================================================
           SIDEBAR
           ===================================================== */

        .sidebar {
            width: 245px;
            background: #153f63;
            color: white;
            min-height: 100vh;
            padding: 24px 18px;
            position: fixed;
            top: 0;
            left: 0;
            bottom: 0;
        }


        .brand {
            margin-bottom: 32px;
        }


        .brand h2 {
            margin: 0;
            font-size: 21px;
        }


        .brand p {
            margin: 6px 0 0;
            font-size: 12px;
            color: #d9e8f5;
        }


        .nav-link {
            display: block;
            padding: 12px 14px;
            margin-bottom: 7px;
            border-radius: 8px;
            text-decoration: none;
            color: #e9f3fb;
            font-size: 14px;
        }


        .nav-link:hover {
            background: rgba(255,255,255,0.12);
        }


        .nav-link.active {
            background: white;
            color: #153f63;
            font-weight: 700;
        }


        .logout-link {
            margin-top: 28px;
            border-top:
                    1px solid rgba(255,255,255,0.2);
            padding-top: 18px;
        }


        /* =====================================================
           MAIN CONTENT
           ===================================================== */

        .main-content {
            margin-left: 245px;
            width: calc(100% - 245px);
            padding: 28px;
        }


        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 22px;
        }


        .page-title h1 {
            margin: 0;
            color: #153f63;
            font-size: 28px;
        }


        .page-title p {
            margin: 6px 0 0;
            color: #6b7280;
            font-size: 14px;
        }


        .user-panel {
            text-align: right;
        }


        .user-panel strong {
            display: block;
            color: #153f63;
        }


        .role-badge {
            display: inline-block;
            margin-top: 4px;
            background: #e7f1f8;
            color: #153f63;
            padding: 4px 10px;
            border-radius: 14px;
            font-size: 11px;
            font-weight: 700;
        }


        /* =====================================================
           ACTIONS
           ===================================================== */

        .actions {
            display: flex;
            justify-content: space-between;
            gap: 10px;
            flex-wrap: wrap;
            margin-bottom: 20px;
        }


        .action-right {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }


        .btn {
            display: inline-block;
            border: none;
            border-radius: 7px;
            padding: 10px 15px;
            text-decoration: none;
            cursor: pointer;
            font-family: inherit;
            font-size: 13px;
            font-weight: 600;
        }


        .btn-primary {
            background: #153f63;
            color: white;
        }


        .btn-secondary {
            background: #edf2f7;
            color: #374151;
        }


        .btn-success {
            background: #198754;
            color: white;
        }


        .btn-outline {
            background: white;
            border: 1px solid #b8ccdc;
            color: #153f63;
        }


        /* =====================================================
           ERROR
           ===================================================== */

        .error-box {
            background: white;
            border: 1px solid #efc1c1;
            border-radius: 10px;
            padding: 25px;
            color: #a02a2a;
        }


        /* =====================================================
           RECEIPT
           ===================================================== */

        .receipt {
            max-width: 950px;
            margin: 0 auto;
            background: white;
            border-radius: 12px;
            border: 1px solid #dfe7ee;
            box-shadow:
                    0 3px 14px rgba(0,0,0,0.07);
            overflow: hidden;
        }


        .receipt-header {
            padding: 28px 32px;
            background: #153f63;
            color: white;
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 20px;
        }


        .clinic-name {
            margin: 0;
            font-size: 26px;
        }


        .clinic-subtitle {
            margin: 5px 0 0;
            color: #dcecf7;
            font-size: 13px;
        }


        .receipt-title {
            text-align: right;
        }


        .receipt-title h2 {
            margin: 0;
            font-size: 23px;
        }


        .receipt-title p {
            margin: 6px 0 0;
            color: #dcecf7;
            font-size: 13px;
        }


        .receipt-body {
            padding: 30px 32px;
        }


        .status-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 15px;
            margin-bottom: 24px;
            padding-bottom: 18px;
            border-bottom: 1px solid #e4e9ef;
        }


        .bill-number-label {
            color: #6b7280;
            font-size: 12px;
            font-weight: 700;
            text-transform: uppercase;
        }


        .bill-number-value {
            display: block;
            margin-top: 4px;
            color: #153f63;
            font-size: 21px;
            font-weight: 800;
        }


        .status {
            display: inline-block;
            padding: 7px 13px;
            border-radius: 16px;
            font-size: 12px;
            font-weight: 800;
        }


        .status-paid {
            background: #e7f7ed;
            color: #17713c;
        }


        .status-unpaid {
            background: #fff3dd;
            color: #9b5b00;
        }


        .info-grid {
            display: grid;
            grid-template-columns:
                    repeat(2, minmax(0,1fr));
            gap: 20px;
            margin-bottom: 26px;
        }


        .info-card {
            border: 1px solid #e3e9ef;
            border-radius: 9px;
            padding: 18px;
            background: #fbfcfd;
        }


        .info-card h3 {
            margin: 0 0 14px;
            color: #153f63;
            font-size: 15px;
        }


        .info-row {
            display: grid;
            grid-template-columns: 135px 1fr;
            gap: 10px;
            margin-bottom: 8px;
            font-size: 13px;
        }


        .info-row:last-child {
            margin-bottom: 0;
        }


        .info-label {
            color: #6b7280;
            font-weight: 600;
        }


        .info-value {
            color: #1f2937;
            word-break: break-word;
        }


        /* =====================================================
           ITEM TABLE
           ===================================================== */

        .section-title {
            margin: 0 0 12px;
            color: #153f63;
            font-size: 17px;
        }


        .table-wrapper {
            overflow-x: auto;
        }


        table {
            width: 100%;
            border-collapse: collapse;
        }


        th {
            padding: 11px;
            background: #f2f5f8;
            color: #4b5563;
            font-size: 12px;
            text-align: left;
            border-bottom: 1px solid #dce4eb;
        }


        td {
            padding: 12px 11px;
            font-size: 13px;
            border-bottom: 1px solid #edf1f4;
        }


        .number {
            text-align: right;
            white-space: nowrap;
        }


        /* =====================================================
           TOTALS
           ===================================================== */

        .totals-container {
            display: flex;
            justify-content: flex-end;
            margin-top: 24px;
        }


        .totals {
            width: 390px;
            max-width: 100%;
        }


        .total-row {
            display: flex;
            justify-content: space-between;
            gap: 20px;
            padding: 8px 0;
            font-size: 13px;
        }


        .total-row span:first-child {
            color: #6b7280;
        }


        .total-row.discount span:last-child {
            color: #a04444;
        }


        .grand-total {
            margin-top: 8px;
            padding-top: 13px;
            border-top: 2px solid #153f63;
            font-size: 20px;
            font-weight: 800;
            color: #153f63;
        }


        /* =====================================================
           FOOTER
           ===================================================== */

        .receipt-footer {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px dashed #cbd5df;
            text-align: center;
            color: #6b7280;
            font-size: 12px;
            line-height: 1.6;
        }


        /* =====================================================
           PRINT
           ===================================================== */

    @media print {

    @page {
        size: A4 portrait;
        margin: 7mm;
    }

    html,
    body {
        margin: 0;
        padding: 0;
        background: white;
        font-size: 10px;
    }

    .sidebar,
    .topbar,
    .actions {
        display: none !important;
    }

    .page-container {
        display: block;
        min-height: auto;
    }

    .main-content {
        margin: 0;
        padding: 0;
        width: 100%;
    }

    .receipt {
        width: 100%;
        max-width: none;
        margin: 0;
        border: none;
        border-radius: 0;
        box-shadow: none;
        overflow: visible;
    }

    .receipt-header {
        padding: 14px 20px;
        min-height: auto;
        -webkit-print-color-adjust: exact;
        print-color-adjust: exact;
    }

    .clinic-name {
        font-size: 20px;
    }

    .clinic-subtitle {
        margin-top: 2px;
        font-size: 9px;
    }

    .receipt-title h2 {
        font-size: 18px;
    }

    .receipt-title p {
        margin-top: 3px;
        font-size: 9px;
    }

    .receipt-body {
        padding: 14px 20px;
    }

    .status-row {
        margin-bottom: 10px;
        padding-bottom: 9px;
    }

    .bill-number-label {
        font-size: 9px;
    }

    .bill-number-value {
        margin-top: 2px;
        font-size: 16px;
    }

    .status {
        padding: 4px 9px;
        font-size: 9px;
        -webkit-print-color-adjust: exact;
        print-color-adjust: exact;
    }

    .info-grid {
        grid-template-columns: repeat(2, 1fr);
        gap: 8px;
        margin-bottom: 12px;
    }

    .info-card {
        padding: 9px 11px;
        border-radius: 5px;
        break-inside: avoid;
        page-break-inside: avoid;
    }

    .info-card h3 {
        margin-bottom: 7px;
        font-size: 11px;
    }

    .info-row {
        grid-template-columns: 105px 1fr;
        gap: 5px;
        margin-bottom: 3px;
        font-size: 9px;
    }

    .section-title {
        margin-bottom: 6px;
        font-size: 12px;
    }

    table {
        break-inside: avoid;
        page-break-inside: avoid;
    }

    th {
        padding: 6px;
        font-size: 9px;
    }

    td {
        padding: 6px;
        font-size: 9px;
    }

    .totals-container {
        margin-top: 9px;
        break-inside: avoid;
        page-break-inside: avoid;
    }

    .totals {
        width: 330px;
    }

    .total-row {
        padding: 4px 0;
        font-size: 9px;
    }

    .grand-total {
        margin-top: 4px;
        padding-top: 7px;
        font-size: 14px;
    }

    .receipt-footer {
        margin-top: 12px;
        padding-top: 8px;
        font-size: 8px;
        line-height: 1.35;
        break-inside: avoid;
        page-break-inside: avoid;
    }
}

    </style>

</head>


<body>


<div class="page-container">


    <!-- =====================================================
         SIDEBAR
         ===================================================== -->

    <aside class="sidebar">


        <div class="brand">

            <h2>Sunrise Dental</h2>

            <p>Clinic Management System</p>

        </div>


        <a
                class="nav-link"
                href="<%= contextPath %>/dashboard">
            Dashboard
        </a>


        <a
                class="nav-link"
                href="<%= contextPath %>/patients">
            Patients
        </a>


        <a
                class="nav-link"
                href="<%= contextPath %>/appointments">
            Appointments
        </a>


        <a
                class="nav-link active"
                href="<%= contextPath %>/bills">
            Billing
        </a>


        <a
                class="nav-link"
                href="<%= contextPath %>/dentists">
            Dentists
        </a>


        <a
                class="nav-link"
                href="<%= contextPath %>/treatments">
            Treatments
        </a>


        <div class="logout-link">

            <a
                    class="nav-link"
                    href="<%= contextPath %>/logout">
                Logout
            </a>

        </div>


    </aside>


    <!-- =====================================================
         MAIN
         ===================================================== -->

    <main class="main-content">


        <div class="topbar">


            <div class="page-title">

                <h1>Patient Receipt</h1>

                <p>
                    Complete treatment and billing information.
                </p>

            </div>


            <div class="user-panel">

                <strong>
                    <%= escapeHtml(fullName) %>
                </strong>

                <span class="role-badge">
                    <%= escapeHtml(role) %>
                </span>

            </div>


        </div>


        <!-- =================================================
             ERROR
             ================================================= -->

        <%
            if (bill == null) {
        %>


        <div class="actions">

            <a
                    class="btn btn-secondary"
                    href="<%= contextPath %>/bills">
                ← Back to Billing
            </a>

        </div>


        <div class="error-box">

            <strong>Unable to display receipt.</strong>

            <p>
                <%= escapeHtml(
                        errorMessage.isBlank()
                                ? "Billing information could not be loaded."
                                : errorMessage
                ) %>
            </p>

        </div>


        <%
            } else {
        %>


        <!-- =================================================
             ACTIONS
             ================================================= -->

        <div class="actions">


            <a
                    class="btn btn-secondary"
                    href="<%= contextPath %>/bills">

                ← Back to Billing

            </a>


            <div class="action-right">


                <%
                    if (bill.isUnpaid()) {
                %>

                <a
                        class="btn btn-success"
                        href="<%= contextPath %>/bill-payment?billId=<%= bill.getBillId() %>">

                    Record Payment

                </a>

                <%
                    }
                %>


                <button
                        type="button"
                        class="btn btn-primary"
                        onclick="window.print();">

                    Print Receipt

                </button>


            </div>


        </div>


        <!-- =================================================
             RECEIPT
             ================================================= -->

        <section class="receipt">


            <!-- =============================================
                 RECEIPT HEADER
                 ============================================= -->

            <header class="receipt-header">


                <div>

                    <h2 class="clinic-name">
                        Sunrise Dental Clinic
                    </h2>

                    <p class="clinic-subtitle">
                        Colombo, Sri Lanka
                    </p>

                    <p class="clinic-subtitle">
                        Patient Appointment & Billing System
                    </p>

                </div>


                <div class="receipt-title">

                    <h2>PAYMENT RECEIPT</h2>

                    <p>
                        Generated:
                        <%= displayValue(
                                formatDateTime(
                                        bill.getGeneratedAt()
                                )
                        ) %>
                    </p>

                </div>


            </header>


            <div class="receipt-body">


                <!-- =========================================
                     BILL NUMBER / STATUS
                     ========================================= -->

                <div class="status-row">


                    <div>

                        <span class="bill-number-label">
                            Bill Number
                        </span>

                        <span class="bill-number-value">
                            <%= escapeHtml(
                                    bill.getBillNumber()
                            ) %>
                        </span>

                    </div>


                    <div>

                        <%
                            if (bill.isPaid()) {
                        %>

                        <span class="status status-paid">
                            PAID
                        </span>

                        <%
                            } else {
                        %>

                        <span class="status status-unpaid">
                            UNPAID
                        </span>

                        <%
                            }
                        %>

                    </div>


                </div>


                <!-- =========================================
                     INFORMATION
                     ========================================= -->

                <div class="info-grid">


                    <!-- PATIENT -->

                    <div class="info-card">


                        <h3>Patient Information</h3>


                        <div class="info-row">

                            <span class="info-label">
                                Patient Code
                            </span>

                            <span class="info-value">
                                <%= displayValue(
                                        bill.getPatientCode()
                                ) %>
                            </span>

                        </div>


                        <div class="info-row">

                            <span class="info-label">
                                Patient Name
                            </span>

                            <span class="info-value">
                                <%= displayValue(
                                        bill.getPatientName()
                                ) %>
                            </span>

                        </div>


                        <div class="info-row">

                            <span class="info-label">
                                Contact
                            </span>

                            <span class="info-value">
                                <%= displayValue(
                                        bill.getPatientContactNumber()
                                ) %>
                            </span>

                        </div>


                        <div class="info-row">

                            <span class="info-label">
                                Email
                            </span>

                            <span class="info-value">
                                <%= displayValue(
                                        bill.getPatientEmail()
                                ) %>
                            </span>

                        </div>


                        <div class="info-row">

                            <span class="info-label">
                                Address
                            </span>

                            <span class="info-value">
                                <%= displayValue(
                                        bill.getPatientAddress()
                                ) %>
                            </span>

                        </div>


                    </div>


                    <!-- APPOINTMENT -->

                    <div class="info-card">


                        <h3>Appointment Information</h3>


                        <div class="info-row">

                            <span class="info-label">
                                Appointment No.
                            </span>

                            <span class="info-value">

                                <strong>
                                    <%= displayValue(
                                            bill.getAppointmentNumber()
                                    ) %>
                                </strong>

                            </span>

                        </div>


                        <div class="info-row">

                            <span class="info-label">
                                Date
                            </span>

                            <span class="info-value">
                                <%= displayValue(
                                        bill.getAppointmentDate()
                                ) %>
                            </span>

                        </div>


                        <div class="info-row">

                            <span class="info-label">
                                Time
                            </span>

                            <span class="info-value">
                                <%= displayValue(
                                        bill.getAppointmentTime()
                                ) %>
                            </span>

                        </div>


                        <div class="info-row">

                            <span class="info-label">
                                Visit Status
                            </span>

                            <span class="info-value">
                                <%= displayValue(
                                        bill.getAppointmentStatus()
                                ) %>
                            </span>

                        </div>


                    </div>


                    <!-- DENTIST -->

                    <div class="info-card">


                        <h3>Dentist Information</h3>


                        <div class="info-row">

                            <span class="info-label">
                                Dentist Code
                            </span>

                            <span class="info-value">
                                <%= displayValue(
                                        bill.getDentistCode()
                                ) %>
                            </span>

                        </div>


                        <div class="info-row">

                            <span class="info-label">
                                Dentist
                            </span>

                            <span class="info-value">
                                <%= displayValue(
                                        bill.getDentistName()
                                ) %>
                            </span>

                        </div>


                        <div class="info-row">

                            <span class="info-label">
                                Specialization
                            </span>

                            <span class="info-value">
                                <%= displayValue(
                                        bill.getDentistSpecialization()
                                ) %>
                            </span>

                        </div>


                    </div>


                    <!-- TREATMENT -->

                    <div class="info-card">


                        <h3>Treatment Information</h3>


                        <div class="info-row">

                            <span class="info-label">
                                Treatment Code
                            </span>

                            <span class="info-value">
                                <%= displayValue(
                                        bill.getTreatmentCode()
                                ) %>
                            </span>

                        </div>


                        <div class="info-row">

                            <span class="info-label">
                                Treatment
                            </span>

                            <span class="info-value">
                                <%= displayValue(
                                        bill.getTreatmentName()
                                ) %>
                            </span>

                        </div>


                        <div class="info-row">

                            <span class="info-label">
                                Generated By
                            </span>

                            <span class="info-value">

                                User #
                                <%= bill.getGeneratedBy() %>

                            </span>

                        </div>


                    </div>


                </div>


                <!-- =========================================
                     BILL ITEMS
                     ========================================= -->

                <h3 class="section-title">
                    Charge Breakdown
                </h3>


                <div class="table-wrapper">


                    <table>


                        <thead>

                        <tr>

                            <th>Description</th>

                            <th>Type</th>

                            <th class="number">
                                Qty
                            </th>

                            <th class="number">
                                Unit Price
                            </th>

                            <th class="number">
                                Amount
                            </th>

                        </tr>

                        </thead>


                        <tbody>


                        <%
                            for (
                                    BillViewModel.BillItemViewModel item
                                    : items
                            ) {
                        %>


                        <tr>

                            <td>
                                <%= displayValue(
                                        item.getItemName()
                                ) %>
                            </td>


                            <td>
                                <%= displayValue(
                                        item.getItemType()
                                ) %>
                            </td>


                            <td class="number">
                                <%= item.getQuantity() %>
                            </td>


                            <td class="number">

                                Rs.
                                <%= formatMoney(
                                        item.getUnitPrice()
                                ) %>

                            </td>


                            <td class="number">

                                <strong>

                                    Rs.
                                    <%= formatMoney(
                                            item.getTotalPrice()
                                    ) %>

                                </strong>

                            </td>

                        </tr>


                        <%
                            }
                        %>


                        </tbody>


                    </table>


                </div>


                <!-- =========================================
                     TOTAL CALCULATION
                     ========================================= -->

                <div class="totals-container">


                    <div class="totals">


                        <div class="total-row">

                            <span>Subtotal</span>

                            <span>

                                Rs.
                                <%= formatMoney(
                                        bill.getSubtotal()
                                ) %>

                            </span>

                        </div>


                        <div class="total-row">

                            <span>Additional Charges</span>

                            <span>

                                +
                                Rs.
                                <%= formatMoney(
                                        bill.getAdditionalCharges()
                                ) %>

                            </span>

                        </div>


                        <div class="total-row discount">

                            <span>Discount</span>

                            <span>

                                -
                                Rs.
                                <%= formatMoney(
                                        bill.getDiscountAmount()
                                ) %>

                            </span>

                        </div>


                        <div class="total-row grand-total">

                            <span>Total Amount</span>

                            <span>

                                Rs.
                                <%= formatMoney(
                                        bill.getTotalAmount()
                                ) %>

                            </span>

                        </div>


                    </div>


                </div>


                <!-- =========================================
                     RECEIPT FOOTER
                     ========================================= -->

                <footer class="receipt-footer">

                    <strong>
                        Thank you for choosing Sunrise Dental Clinic.
                    </strong>

                    <br>

                    This receipt was generated electronically
                    by the Sunrise Dental Clinic Management System.

                    <br>

                    Bill:
                    <%= escapeHtml(
                            bill.getBillNumber()
                    ) %>

                    |
                    Appointment:
                    <%= escapeHtml(
                            bill.getAppointmentNumber()
                    ) %>

                </footer>


            </div>


        </section>


        <%
            }
        %>


    </main>


</div>


</body>

</html>