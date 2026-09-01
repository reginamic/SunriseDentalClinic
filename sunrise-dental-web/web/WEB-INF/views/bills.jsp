<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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


    private String valueOrEmpty(Object value) {

        return value == null
                ? ""
                : value.toString();
    }
%>


<%
    String contextPath =
            request.getContextPath();


    List<BillViewModel> bills =
            (List<BillViewModel>)
                    request.getAttribute("bills");


    if (bills == null) {

        bills =
                Collections.emptyList();
    }


    Long totalBills =
            (Long)
                    request.getAttribute(
                            "totalBills"
                    );


    Long paidBills =
            (Long)
                    request.getAttribute(
                            "paidBills"
                    );


    Long unpaidBills =
            (Long)
                    request.getAttribute(
                            "unpaidBills"
                    );


    BigDecimal totalBilledAmount =
            (BigDecimal)
                    request.getAttribute(
                            "totalBilledAmount"
                    );


    BigDecimal totalPaidAmount =
            (BigDecimal)
                    request.getAttribute(
                            "totalPaidAmount"
                    );


    BigDecimal outstandingAmount =
            (BigDecimal)
                    request.getAttribute(
                            "outstandingAmount"
                    );


    String selectedBillNumber =
            valueOrEmpty(
                    request.getAttribute(
                            "selectedBillNumber"
                    )
            );


    String selectedAppointmentId =
            valueOrEmpty(
                    request.getAttribute(
                            "selectedAppointmentId"
                    )
            );


    String selectedPaymentStatus =
            valueOrEmpty(
                    request.getAttribute(
                            "selectedPaymentStatus"
                    )
            );


    String errorMessage =
            valueOrEmpty(
                    request.getAttribute(
                            "errorMessage"
                    )
            );


    String successMessage =
            valueOrEmpty(
                    request.getAttribute(
                            "successMessage"
                    )
            );


    String fullName =
            valueOrEmpty(
                    session.getAttribute(
                            "fullName"
                    )
            );


    String role =
            valueOrEmpty(
                    session.getAttribute(
                            "role"
                    )
            );
%>


<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta
            name="viewport"
            content="width=device-width, initial-scale=1.0">

    <title>
        Billing Management | Sunrise Dental Clinic
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
            background: rgba(255, 255, 255, 0.12);
        }


        .nav-link.active {
            background: #ffffff;
            color: #153f63;
            font-weight: 700;
        }


        .logout-link {
            margin-top: 28px;
            border-top: 1px solid rgba(255,255,255,0.2);
            padding-top: 18px;
        }


        /* =====================================================
           MAIN
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
            margin-bottom: 26px;
        }


        .page-title h1 {
            margin: 0;
            font-size: 28px;
            color: #153f63;
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
            border-radius: 14px;
            padding: 4px 10px;
            font-size: 11px;
            font-weight: 700;
        }


        /* =====================================================
           ALERTS
           ===================================================== */

        .alert {
            padding: 13px 16px;
            border-radius: 8px;
            margin-bottom: 18px;
            font-size: 14px;
        }


        .alert-success {
            background: #e9f8ee;
            color: #176b36;
            border: 1px solid #bce8ca;
        }


        .alert-error {
            background: #fff0f0;
            color: #a02a2a;
            border: 1px solid #efc1c1;
        }


        /* =====================================================
           ACTION BAR
           ===================================================== */

        .action-bar {
            display: flex;
            justify-content: flex-end;
            margin-bottom: 20px;
        }


        .btn {
            display: inline-block;
            border: none;
            border-radius: 7px;
            padding: 10px 15px;
            text-decoration: none;
            cursor: pointer;
            font-weight: 600;
            font-size: 13px;
            font-family: inherit;
        }


        .btn-primary {
            background: #153f63;
            color: white;
        }


        .btn-primary:hover {
            background: #0f304c;
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
            color: #153f63;
            border: 1px solid #b8ccdc;
        }


        .btn-small {
            padding: 7px 10px;
            font-size: 12px;
        }


        /* =====================================================
           STATISTICS
           ===================================================== */

        .stats-grid {
            display: grid;
            grid-template-columns:
                    repeat(3, minmax(0, 1fr));
            gap: 16px;
            margin-bottom: 18px;
        }


        .stats-grid.money {
            margin-bottom: 24px;
        }


        .stat-card {
            background: white;
            border-radius: 11px;
            padding: 18px;
            box-shadow:
                    0 2px 10px rgba(0,0,0,0.05);
            border: 1px solid #e7edf2;
        }


        .stat-label {
            color: #6b7280;
            font-size: 12px;
            text-transform: uppercase;
            font-weight: 700;
            margin-bottom: 8px;
        }


        .stat-number {
            color: #153f63;
            font-size: 27px;
            font-weight: 700;
        }


        .money-value {
            font-size: 21px;
        }


        .paid-value {
            color: #198754;
        }


        .unpaid-value {
            color: #b56b00;
        }


        /* =====================================================
           CARD
           ===================================================== */

        .card {
            background: white;
            border-radius: 11px;
            padding: 20px;
            border: 1px solid #e7edf2;
            box-shadow:
                    0 2px 10px rgba(0,0,0,0.04);
            margin-bottom: 22px;
        }


        .card-title {
            margin: 0 0 18px;
            color: #153f63;
            font-size: 18px;
        }


        /* =====================================================
           SEARCH
           ===================================================== */

        .search-grid {
            display: grid;
            grid-template-columns:
                    1.5fr 1fr 1fr auto;
            gap: 12px;
            align-items: end;
        }


        .field label {
            display: block;
            font-size: 12px;
            font-weight: 700;
            color: #4b5563;
            margin-bottom: 6px;
        }


        .field input,
        .field select {
            width: 100%;
            padding: 10px 11px;
            border: 1px solid #cfd9e3;
            border-radius: 7px;
            font-size: 13px;
            background: white;
        }


        .field input:focus,
        .field select:focus {
            outline: none;
            border-color: #4c87b3;
            box-shadow:
                    0 0 0 2px rgba(76,135,179,0.12);
        }


        .search-actions {
            display: flex;
            gap: 8px;
        }


        /* =====================================================
           TABLE
           ===================================================== */

        .table-wrapper {
            overflow-x: auto;
        }


        table {
            width: 100%;
            border-collapse: collapse;
        }


        th {
            background: #f3f6f9;
            color: #465465;
            text-align: left;
            font-size: 12px;
            padding: 12px;
            border-bottom: 1px solid #dfe7ee;
            white-space: nowrap;
        }


        td {
            padding: 13px 12px;
            border-bottom: 1px solid #edf1f5;
            font-size: 13px;
            vertical-align: middle;
        }


        tr:hover td {
            background: #fafcfe;
        }


        .bill-number {
            font-weight: 700;
            color: #153f63;
        }


        .amount {
            text-align: right;
            white-space: nowrap;
            font-variant-numeric: tabular-nums;
        }


        .status {
            display: inline-block;
            padding: 5px 9px;
            border-radius: 14px;
            font-size: 11px;
            font-weight: 700;
        }


        .status-paid {
            background: #e7f7ed;
            color: #17713c;
        }


        .status-unpaid {
            background: #fff3dd;
            color: #9b5b00;
        }


        .status-other {
            background: #edf2f7;
            color: #4b5563;
        }


        .action-group {
            display: flex;
            gap: 6px;
            flex-wrap: wrap;
        }


        .empty-state {
            text-align: center;
            padding: 45px 15px;
            color: #6b7280;
        }


        .empty-state h3 {
            color: #374151;
            margin-bottom: 6px;
        }


        /* =====================================================
           RESPONSIVE
           ===================================================== */

        @media (max-width: 1050px) {

            .stats-grid {
                grid-template-columns:
                        repeat(2, 1fr);
            }


            .search-grid {
                grid-template-columns:
                        repeat(2, 1fr);
            }
        }


        @media (max-width: 760px) {

            .sidebar {
                position: static;
                width: 100%;
                min-height: auto;
            }


            .page-container {
                display: block;
            }


            .main-content {
                margin-left: 0;
                width: 100%;
                padding: 18px;
            }


            .topbar {
                align-items: flex-start;
                gap: 15px;
            }


            .stats-grid,
            .search-grid {
                grid-template-columns: 1fr;
            }


            .search-actions {
                flex-wrap: wrap;
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
         MAIN CONTENT
         ===================================================== -->

    <main class="main-content">


        <div class="topbar">


            <div class="page-title">

                <h1>Billing Management</h1>

                <p>
                    Generate, monitor and manage patient billing
                    records.
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
             MESSAGES
             ================================================= -->

        <%
            if (!successMessage.isBlank()) {
        %>

        <div class="alert alert-success">

            <%= escapeHtml(successMessage) %>

        </div>

        <%
            }
        %>


        <%
            if (!errorMessage.isBlank()) {
        %>

        <div class="alert alert-error">

            <%= escapeHtml(errorMessage) %>

        </div>

        <%
            }
        %>


        <!-- =================================================
             MAIN ACTION
             ================================================= -->

        <div class="action-bar">

            <a
                    href="<%= contextPath %>/bill-generate"
                    class="btn btn-primary">

                + Generate New Bill

            </a>

        </div>


        <!-- =================================================
             NUMBER STATISTICS
             ================================================= -->

        <section class="stats-grid">


            <div class="stat-card">

                <div class="stat-label">
                    Total Bills
                </div>

                <div class="stat-number">
                    <%= totalBills == null ? 0 : totalBills %>
                </div>

            </div>


            <div class="stat-card">

                <div class="stat-label">
                    Paid Bills
                </div>

                <div class="stat-number paid-value">
                    <%= paidBills == null ? 0 : paidBills %>
                </div>

            </div>


            <div class="stat-card">

                <div class="stat-label">
                    Unpaid Bills
                </div>

                <div class="stat-number unpaid-value">
                    <%= unpaidBills == null ? 0 : unpaidBills %>
                </div>

            </div>


        </section>


        <!-- =================================================
             FINANCIAL STATISTICS
             ================================================= -->

        <section class="stats-grid money">


            <div class="stat-card">

                <div class="stat-label">
                    Total Billed
                </div>

                <div class="stat-number money-value">

                    Rs.
                    <%= formatMoney(totalBilledAmount) %>

                </div>

            </div>


            <div class="stat-card">

                <div class="stat-label">
                    Total Paid
                </div>

                <div class="stat-number money-value paid-value">

                    Rs.
                    <%= formatMoney(totalPaidAmount) %>

                </div>

            </div>


            <div class="stat-card">

                <div class="stat-label">
                    Outstanding
                </div>

                <div class="stat-number money-value unpaid-value">

                    Rs.
                    <%= formatMoney(outstandingAmount) %>

                </div>

            </div>


        </section>


        <!-- =================================================
             SEARCH / FILTER
             ================================================= -->

        <section class="card">


            <h2 class="card-title">
                Search & Filter Bills
            </h2>


            <form
                    method="get"
                    action="<%= contextPath %>/bills">


                <div class="search-grid">


                    <div class="field">

                        <label for="billNumber">
                            Bill Number
                        </label>

                        <input
                                type="text"
                                id="billNumber"
                                name="billNumber"
                                placeholder="Example: BILL-41847ED0"
                                value="<%= escapeHtml(selectedBillNumber) %>">

                    </div>


                    <div class="field">

                        <label for="appointmentId">
                            Appointment ID
                        </label>

                        <input
                                type="number"
                                id="appointmentId"
                                name="appointmentId"
                                min="1"
                                placeholder="Example: 2"
                                value="<%= escapeHtml(selectedAppointmentId) %>">

                    </div>


                    <div class="field">

                        <label for="paymentStatus">
                            Payment Status
                        </label>

                        <select
                                id="paymentStatus"
                                name="paymentStatus">


                            <option value="">
                                All statuses
                            </option>


                            <option
                                    value="PAID"
                                    <%= "PAID".equalsIgnoreCase(
                                            selectedPaymentStatus
                                    )
                                            ? "selected"
                                            : "" %>>

                                Paid

                            </option>


                            <option
                                    value="UNPAID"
                                    <%= "UNPAID".equalsIgnoreCase(
                                            selectedPaymentStatus
                                    )
                                            ? "selected"
                                            : "" %>>

                                Unpaid

                            </option>


                        </select>

                    </div>


                    <div class="search-actions">

                        <button
                                type="submit"
                                class="btn btn-primary">

                            Search

                        </button>


                        <a
                                href="<%= contextPath %>/bills"
                                class="btn btn-secondary">

                            Reset

                        </a>

                    </div>


                </div>


            </form>


        </section>


        <!-- =================================================
             BILL TABLE
             ================================================= -->

        <section class="card">


            <h2 class="card-title">
                Billing Records
            </h2>


            <%
                if (bills.isEmpty()) {
            %>


            <div class="empty-state">

                <h3>No billing records found</h3>

                <p>
                    No bills match the selected search
                    or filtering criteria.
                </p>

            </div>


            <%
                } else {
            %>


            <div class="table-wrapper">


                <table>


                    <thead>

                    <tr>

                        <th>Bill Number</th>

                        <th>Appointment</th>

                        <th>Subtotal</th>

                        <th>Additional</th>

                        <th>Discount</th>

                        <th>Total</th>

                        <th>Status</th>

                        <th>Actions</th>

                    </tr>

                    </thead>


                    <tbody>


                    <%
                        for (BillViewModel bill : bills) {

                            String paymentStatus =
                                    bill.getPaymentStatus();


                            String statusClass =
                                    "status-other";


                            if (bill.isPaid()) {

                                statusClass =
                                        "status-paid";

                            } else if (bill.isUnpaid()) {

                                statusClass =
                                        "status-unpaid";
                            }
                    %>


                    <tr>


                        <td>

                            <span class="bill-number">

                                <%= escapeHtml(
                                        bill.getBillNumber()
                                ) %>

                            </span>

                        </td>


                        <td>

                            #<%= bill.getAppointmentId() %>

                        </td>


                        <td class="amount">

                            Rs.
                            <%= formatMoney(
                                    bill.getSubtotal()
                            ) %>

                        </td>


                        <td class="amount">

                            Rs.
                            <%= formatMoney(
                                    bill.getAdditionalCharges()
                            ) %>

                        </td>


                        <td class="amount">

                            Rs.
                            <%= formatMoney(
                                    bill.getDiscountAmount()
                            ) %>

                        </td>


                        <td class="amount">

                            <strong>

                                Rs.
                                <%= formatMoney(
                                        bill.getTotalAmount()
                                ) %>

                            </strong>

                        </td>


                        <td>

                            <span class="status <%= statusClass %>">

                                <%= escapeHtml(
                                        paymentStatus == null
                                                ? "UNKNOWN"
                                                : paymentStatus
                                ) %>

                            </span>

                        </td>


                        <td>


                            <div class="action-group">


                                <a
                                        class="btn btn-outline btn-small"
                                        href="<%= contextPath %>/bill-details?billId=<%= bill.getBillId() %>">

                                    View Receipt

                                </a>


                                <%
                                    if (bill.isUnpaid()) {
                                %>


                                <a
                                        class="btn btn-success btn-small"
                                        href="<%= contextPath %>/bill-payment?billId=<%= bill.getBillId() %>">

                                    Record Payment

                                </a>


                                <%
                                    }
                                %>


                            </div>


                        </td>


                    </tr>


                    <%
                        }
                    %>


                    </tbody>


                </table>


            </div>


            <%
                }
            %>


        </section>


    </main>


</div>


</body>

</html>