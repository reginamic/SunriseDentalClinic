<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="com.sunrisedental.web.model.BillDetailsViewModel" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.DecimalFormat" %>


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
%>


<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta
            name="viewport"
            content="width=device-width, initial-scale=1.0">

    <title>
        Record Payment | Sunrise Dental Clinic
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
            padding-top: 18px;
            border-top:
                    1px solid rgba(255,255,255,0.2);
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
            margin-bottom: 24px;
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
            padding: 4px 10px;
            border-radius: 14px;
            background: #e7f1f8;
            color: #153f63;
            font-size: 11px;
            font-weight: 700;
        }


        /* =====================================================
           CARD
           ===================================================== */

        .card {
            max-width: 850px;
            margin: 0 auto;
            background: white;
            border: 1px solid #e0e7ee;
            border-radius: 12px;
            box-shadow:
                    0 3px 14px rgba(0,0,0,0.06);
            overflow: hidden;
        }


        .card-header {
            background: #153f63;
            color: white;
            padding: 22px 26px;
        }


        .card-header h2 {
            margin: 0;
            font-size: 21px;
        }


        .card-header p {
            margin: 6px 0 0;
            color: #dceaf5;
            font-size: 13px;
        }


        .card-body {
            padding: 26px;
        }


        /* =====================================================
           ALERT
           ===================================================== */

        .alert {
            max-width: 850px;
            margin: 0 auto 18px;
            padding: 13px 16px;
            border-radius: 8px;
            font-size: 13px;
        }


        .alert-error {
            background: #fff0f0;
            color: #a02a2a;
            border: 1px solid #efc1c1;
        }


        .alert-warning {
            background: #fff7e8;
            color: #855500;
            border: 1px solid #efd6a4;
            margin-bottom: 22px;
        }


        /* =====================================================
           BILL
           ===================================================== */

        .bill-summary {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 20px;
            padding-bottom: 20px;
            margin-bottom: 22px;
            border-bottom: 1px solid #e4e9ef;
        }


        .label {
            display: block;
            color: #6b7280;
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
            margin-bottom: 4px;
        }


        .bill-number {
            color: #153f63;
            font-size: 23px;
            font-weight: 800;
        }


        .status {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 15px;
            background: #fff3dd;
            color: #9b5b00;
            font-size: 11px;
            font-weight: 800;
        }


        .info-grid {
            display: grid;
            grid-template-columns:
                    repeat(2, minmax(0, 1fr));
            gap: 14px 22px;
            margin-bottom: 24px;
        }


        .info-item {
            padding-bottom: 9px;
            border-bottom: 1px solid #edf1f5;
        }


        .info-value {
            font-size: 14px;
            font-weight: 600;
        }


        /* =====================================================
           PAYMENT AMOUNT
           ===================================================== */

        .payment-box {
            background: #f5f9fc;
            border: 1px solid #dce7ef;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 22px;
        }


        .payment-row {
            display: flex;
            justify-content: space-between;
            gap: 20px;
            padding: 6px 0;
            font-size: 13px;
        }


        .payment-row span:first-child {
            color: #6b7280;
        }


        .grand-total {
            margin-top: 8px;
            padding-top: 13px;
            border-top: 2px solid #153f63;
            color: #153f63;
            font-size: 22px;
            font-weight: 800;
        }


        /* =====================================================
           ACTIONS
           ===================================================== */

        .form-actions {
            display: flex;
            justify-content: space-between;
            gap: 12px;
            flex-wrap: wrap;
        }


        .btn {
            display: inline-block;
            padding: 11px 16px;
            border: none;
            border-radius: 7px;
            text-decoration: none;
            cursor: pointer;
            font-family: inherit;
            font-size: 13px;
            font-weight: 700;
        }


        .btn-secondary {
            background: #edf2f7;
            color: #374151;
        }


        .btn-success {
            background: #198754;
            color: white;
        }


        .btn-success:hover {
            background: #146c43;
        }


        /* =====================================================
           ERROR BOX
           ===================================================== */

        .error-card {
            max-width: 850px;
            margin: 0 auto;
            padding: 24px;
            background: white;
            border: 1px solid #efc1c1;
            border-radius: 10px;
            color: #a02a2a;
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


            .info-grid {
                grid-template-columns: 1fr;
            }


            .bill-summary {
                flex-direction: column;
            }
        }

    </style>

</head>


<body>


<div class="page-container">


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


    <main class="main-content">


        <div class="topbar">


            <div class="page-title">

                <h1>Record Payment</h1>

                <p>
                    Confirm payment before changing the bill status.
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


        <%
            if (!errorMessage.isBlank()) {
        %>

        <div class="alert alert-error">
            <%= escapeHtml(errorMessage) %>
        </div>

        <%
            }
        %>


        <%
            if (bill == null) {
        %>


        <div class="error-card">

            <strong>
                Unable to load billing information.
            </strong>

            <p>
                Please return to Billing Management
                and try again.
            </p>

            <a
                    class="btn btn-secondary"
                    href="<%= contextPath %>/bills">
                Back to Billing
            </a>

        </div>


        <%
            } else {
        %>


        <section class="card">


            <div class="card-header">

                <h2>Payment Confirmation</h2>

                <p>
                    Review the bill carefully before recording
                    payment.
                </p>

            </div>


            <div class="card-body">


                <div class="alert alert-warning">

                    This action will mark the bill as
                    <strong>PAID</strong>.
                    Confirm only after the clinic has actually
                    received the payment.

                </div>


                <div class="bill-summary">


                    <div>

                        <span class="label">
                            Bill Number
                        </span>

                        <span class="bill-number">
                            <%= escapeHtml(
                                    bill.getBillNumber()
                            ) %>
                        </span>

                    </div>


                    <span class="status">
                        <%= escapeHtml(
                                bill.getPaymentStatus()
                        ) %>
                    </span>


                </div>


                <div class="info-grid">


                    <div class="info-item">

                        <span class="label">
                            Appointment
                        </span>

                        <span class="info-value">
                            <%= displayValue(
                                    bill.getAppointmentNumber()
                            ) %>
                        </span>

                    </div>


                    <div class="info-item">

                        <span class="label">
                            Patient
                        </span>

                        <span class="info-value">
                            <%= displayValue(
                                    bill.getPatientName()
                            ) %>
                        </span>

                    </div>


                    <div class="info-item">

                        <span class="label">
                            Patient Code
                        </span>

                        <span class="info-value">
                            <%= displayValue(
                                    bill.getPatientCode()
                            ) %>
                        </span>

                    </div>


                    <div class="info-item">

                        <span class="label">
                            Treatment
                        </span>

                        <span class="info-value">
                            <%= displayValue(
                                    bill.getTreatmentName()
                            ) %>
                        </span>

                    </div>


                    <div class="info-item">

                        <span class="label">
                            Dentist
                        </span>

                        <span class="info-value">
                            <%= displayValue(
                                    bill.getDentistName()
                            ) %>
                        </span>

                    </div>


                    <div class="info-item">

                        <span class="label">
                            Appointment Date
                        </span>

                        <span class="info-value">
                            <%= displayValue(
                                    bill.getAppointmentDate()
                            ) %>
                        </span>

                    </div>


                </div>


                <div class="payment-box">


                    <div class="payment-row">

                        <span>Subtotal</span>

                        <span>
                            Rs.
                            <%= formatMoney(
                                    bill.getSubtotal()
                            ) %>
                        </span>

                    </div>


                    <div class="payment-row">

                        <span>Additional Charges</span>

                        <span>
                            + Rs.
                            <%= formatMoney(
                                    bill.getAdditionalCharges()
                            ) %>
                        </span>

                    </div>


                    <div class="payment-row">

                        <span>Discount</span>

                        <span>
                            - Rs.
                            <%= formatMoney(
                                    bill.getDiscountAmount()
                            ) %>
                        </span>

                    </div>


                    <div class="payment-row grand-total">

                        <span>Amount to Receive</span>

                        <span>
                            Rs.
                            <%= formatMoney(
                                    bill.getTotalAmount()
                            ) %>
                        </span>

                    </div>


                </div>


                <form
                        method="post"
                        action="<%= contextPath %>/bill-payment"
                        onsubmit="return confirmPayment();">


                    <!--
                        Only billId is submitted.

                        paymentStatus is NOT user-editable.
                        The servlet always sends PAID to
                        the trusted Billing API.
                    -->
                    <input
                            type="hidden"
                            name="billId"
                            value="<%= bill.getBillId() %>">


                    <div class="form-actions">


                        <a
                                class="btn btn-secondary"
                                href="<%= contextPath %>/bill-details?billId=<%= bill.getBillId() %>">

                            Cancel

                        </a>


                        <button
                                type="submit"
                                class="btn btn-success">

                            Confirm Payment — Rs.
                            <%= formatMoney(
                                    bill.getTotalAmount()
                            ) %>

                        </button>


                    </div>


                </form>


            </div>


        </section>


        <%
            }
        %>


    </main>


</div>


<script>

    function confirmPayment() {

        return confirm(
                "Confirm that payment has been received and mark this bill as PAID?"
        );
    }

</script>


</body>

</html>