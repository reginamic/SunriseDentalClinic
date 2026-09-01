<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.web.model.ReportViewModel" %>
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

    private String money(
            BigDecimal value) {

        if (value == null) {
            value = BigDecimal.ZERO;
        }

        return new DecimalFormat(
                "#,##0.00"
        ).format(value);
    }

    private String percent(
            BigDecimal value) {

        if (value == null) {
            value = BigDecimal.ZERO;
        }

        return new DecimalFormat(
                "0.0"
        ).format(value);
    }

    private double safeWidth(
            BigDecimal value) {

        if (value == null) {
            return 0;
        }

        double width =
                value.doubleValue();

        if (width < 0) {
            return 0;
        }

        if (width > 100) {
            return 100;
        }

        return width;
    }
%>

<%
    ReportViewModel report =
            (ReportViewModel)
                    request.getAttribute(
                            "report"
                    );

    String error =
            (String)
                    request.getAttribute(
                            "error"
                    );

    String fromDate =
            String.valueOf(
                    request.getAttribute(
                            "fromDate"
                    )
            );

    String toDate =
            String.valueOf(
                    request.getAttribute(
                            "toDate"
                    )
            );

    if ("null".equals(fromDate)) {
        fromDate = "";
    }

    if ("null".equals(toDate)) {
        toDate = "";
    }

    String fullName =
            String.valueOf(
                    session.getAttribute(
                            "fullName"
                    )
            );

    String username =
            String.valueOf(
                    session.getAttribute(
                            "username"
                    )
            );

    String role =
            String.valueOf(
                    session.getAttribute(
                            "role"
                    )
            );

    if ("null".equals(fullName)) {
        fullName = "System Administrator";
    }

    if ("null".equals(role)) {
        role = "ADMIN";
    }

    String avatarLetter = "A";

    if (username != null
            && !username.isBlank()
            && !"null".equals(username)) {

        avatarLetter =
                username.substring(
                        0,
                        1
                ).toUpperCase();
    }
%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        Reports & Analytics | Sunrise Dental
    </title>

    <style>

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family:
                Arial,
                Helvetica,
                sans-serif;

            background:
                #f5f7fb;

            color:
                #1f2937;
        }

        .layout {
            display: flex;
            min-height: 100vh;
        }

        /* =====================================================
           SIDEBAR
           ===================================================== */

        .sidebar {
            width: 255px;
            min-height: 100vh;

            background:
                #17365d;

            color:
                white;

            display: flex;
            flex-direction: column;

            position: fixed;
            left: 0;
            top: 0;
            bottom: 0;

            z-index: 20;
        }

        .brand {
            padding: 28px 24px;
            border-bottom:
                1px solid rgba(
                    255,
                    255,
                    255,
                    0.12
                );
        }

        .brand-mark {
            width: 44px;
            height: 44px;

            border-radius: 12px;

            background:
                rgba(
                    255,
                    255,
                    255,
                    0.14
                );

            display: flex;
            align-items: center;
            justify-content: center;

            font-size: 24px;
            margin-bottom: 12px;
        }

        .brand h2 {
            font-size: 20px;
            margin-bottom: 4px;
        }

        .brand p {
            color:
                #cbd5e1;

            font-size: 12px;
            line-height: 1.5;
        }

        .navigation {
            padding: 18px 14px;

            flex: 1;
        }

        .navigation a {
            display: flex;
            align-items: center;
            gap: 12px;

            padding: 12px 14px;
            margin-bottom: 5px;

            color:
                #dbe7f5;

            text-decoration: none;

            border-radius: 8px;

            font-size: 14px;
            font-weight: 600;

            transition:
                background 0.2s,
                color 0.2s;
        }

        .navigation a:hover,
        .navigation a.active {

            background:
                rgba(
                    255,
                    255,
                    255,
                    0.13
                );

            color:
                white;
        }

        .nav-icon {
            width: 23px;
            text-align: center;
        }

        .sidebar-footer {
            padding: 18px 20px;

            border-top:
                1px solid rgba(
                    255,
                    255,
                    255,
                    0.12
                );
        }

        .sidebar-footer a {
            color:
                #dbe7f5;

            text-decoration: none;
            font-size: 14px;
        }

        /* =====================================================
           MAIN
           ===================================================== */

        .main {
            margin-left: 255px;
            width: calc(100% - 255px);
        }

        .topbar {
            height: 82px;

            background:
                white;

            border-bottom:
                1px solid #e5e7eb;

            display: flex;
            justify-content: space-between;
            align-items: center;

            padding:
                0 32px;

            position: sticky;
            top: 0;

            z-index: 10;
        }

        .page-title h1 {
            font-size: 22px;
            margin-bottom: 4px;
        }

        .page-title p {
            font-size: 13px;
            color:
                #6b7280;
        }

        .user-box {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .user-details {
            text-align: right;
        }

        .user-details strong {
            display: block;

            font-size: 14px;
        }

        .user-details span {
            display: block;

            margin-top: 3px;

            color:
                #64748b;

            font-size: 11px;
            font-weight: bold;
            letter-spacing: 0.5px;
        }

        .avatar {
            width: 40px;
            height: 40px;

            border-radius: 50%;

            display: flex;
            align-items: center;
            justify-content: center;

            background:
                #17365d;

            color:
                white;

            font-weight: bold;
        }

        .content {
            padding: 30px 32px 50px;
        }

        /* =====================================================
           PAGE HEADER
           ===================================================== */

        .report-heading {
            display: flex;

            justify-content:
                space-between;

            align-items:
                flex-start;

            gap: 20px;

            margin-bottom: 24px;
        }

        .report-heading h2 {
            font-size: 26px;
            margin-bottom: 7px;
        }

        .report-heading p {
            color:
                #6b7280;

            font-size: 14px;

            line-height: 1.6;
        }

        .print-button {
            border: 0;

            background:
                #17365d;

            color:
                white;

            padding:
                11px 18px;

            border-radius:
                8px;

            cursor: pointer;

            font-size:
                13px;

            font-weight:
                bold;
        }

        .print-button:hover {
            background:
                #244c7e;
        }

        /* =====================================================
           FILTER
           ===================================================== */

        .filter-panel {
            background:
                white;

            border:
                1px solid #e5e7eb;

            border-radius:
                12px;

            padding:
                20px;

            margin-bottom:
                24px;

            box-shadow:
                0 2px 8px rgba(
                    15,
                    23,
                    42,
                    0.04
                );
        }

        .filter-title {
            font-size:
                15px;

            font-weight:
                bold;

            margin-bottom:
                14px;
        }

        .filter-form {
            display:
                flex;

            align-items:
                end;

            gap:
                14px;

            flex-wrap:
                wrap;
        }

        .form-group {
            min-width:
                190px;
        }

        .form-group label {
            display:
                block;

            margin-bottom:
                7px;

            font-size:
                12px;

            color:
                #475569;

            font-weight:
                bold;
        }

        .form-control {
            width:
                100%;

            padding:
                10px 11px;

            border:
                1px solid #cbd5e1;

            border-radius:
                7px;

            background:
                white;

            font-size:
                13px;
        }

        .form-control:focus {
            outline:
                none;

            border-color:
                #17365d;

            box-shadow:
                0 0 0 3px rgba(
                    23,
                    54,
                    93,
                    0.08
                );
        }

        .generate-button {
            padding:
                11px 18px;

            border:
                0;

            border-radius:
                7px;

            background:
                #17365d;

            color:
                white;

            font-weight:
                bold;

            cursor:
                pointer;

            font-size:
                13px;
        }

        .generate-button:hover {
            background:
                #244c7e;
        }

        .period-note {
            margin-left:
                auto;

            padding:
                10px 14px;

            background:
                #f8fafc;

            border-radius:
                7px;

            color:
                #64748b;

            font-size:
                12px;
        }

        /* =====================================================
           ALERT
           ===================================================== */

        .alert-error {
            margin-bottom:
                22px;

            padding:
                13px 16px;

            border-radius:
                8px;

            border:
                1px solid #fecaca;

            background:
                #fef2f2;

            color:
                #991b1b;

            font-size:
                13px;
        }

        /* =====================================================
           SUMMARY CARDS
           ===================================================== */

        .section-title {
            margin:
                28px 0 14px;

            display:
                flex;

            justify-content:
                space-between;

            align-items:
                center;
        }

        .section-title h3 {
            font-size:
                17px;
        }

        .section-title span {
            color:
                #64748b;

            font-size:
                12px;
        }

        .summary-grid {
            display:
                grid;

            grid-template-columns:
                repeat(
                    4,
                    minmax(
                        0,
                        1fr
                    )
                );

            gap:
                16px;
        }

        .summary-card {
            background:
                white;

            border:
                1px solid #e5e7eb;

            border-radius:
                11px;

            padding:
                19px;

            box-shadow:
                0 2px 8px rgba(
                    15,
                    23,
                    42,
                    0.04
                );
        }

        .summary-label {
            color:
                #64748b;

            font-size:
                12px;

            font-weight:
                bold;

            margin-bottom:
                10px;
        }

        .summary-value {
            font-size:
                28px;

            font-weight:
                bold;

            color:
                #0f172a;
        }

        .summary-subtext {
            margin-top:
                7px;

            font-size:
                11px;

            color:
                #94a3b8;
        }

        .finance-value {
            font-size:
                22px;
        }

        /* =====================================================
           KPI
           ===================================================== */

        .kpi-grid {
            display:
                grid;

            grid-template-columns:
                repeat(
                    3,
                    minmax(
                        0,
                        1fr
                    )
                );

            gap:
                16px;
        }

        .kpi-card {
            background:
                white;

            border:
                1px solid #e5e7eb;

            border-radius:
                11px;

            padding:
                20px;
        }

        .kpi-top {
            display:
                flex;

            justify-content:
                space-between;

            gap:
                10px;

            margin-bottom:
                12px;
        }

        .kpi-name {
            font-size:
                13px;

            font-weight:
                bold;
        }

        .kpi-number {
            font-size:
                18px;

            font-weight:
                bold;

            color:
                #17365d;
        }

        .progress {
            width:
                100%;

            height:
                8px;

            border-radius:
                20px;

            background:
                #e8edf4;

            overflow:
                hidden;
        }

        .progress-bar {
            height:
                100%;

            border-radius:
                20px;

            background:
                #17365d;
        }

        .kpi-description {
            margin-top:
                10px;

            color:
                #64748b;

            font-size:
                11px;

            line-height:
                1.5;
        }

        /* =====================================================
           TABLE
           ===================================================== */

        .report-card {
            background:
                white;

            border:
                1px solid #e5e7eb;

            border-radius:
                11px;

            overflow:
                hidden;

            box-shadow:
                0 2px 8px rgba(
                    15,
                    23,
                    42,
                    0.04
                );

            margin-bottom:
                24px;
        }

        .report-card-header {
            padding:
                17px 20px;

            border-bottom:
                1px solid #e5e7eb;
        }

        .report-card-header h3 {
            font-size:
                16px;

            margin-bottom:
                5px;
        }

        .report-card-header p {
            font-size:
                12px;

            color:
                #64748b;
        }

        .table-wrapper {
            overflow-x:
                auto;
        }

        table {
            width:
                100%;

            border-collapse:
                collapse;
        }

        th {
            background:
                #f8fafc;

            color:
                #475569;

            text-align:
                left;

            padding:
                12px 16px;

            font-size:
                11px;

            text-transform:
                uppercase;

            letter-spacing:
                0.4px;

            border-bottom:
                1px solid #e5e7eb;
        }

        td {
            padding:
                14px 16px;

            border-bottom:
                1px solid #eef2f7;

            font-size:
                13px;

            vertical-align:
                middle;
        }

        tr:last-child td {
            border-bottom:
                none;
        }

        tbody tr:hover {
            background:
                #fafcff;
        }

        .name-cell strong {
            display:
                block;

            margin-bottom:
                4px;
        }

        .name-cell span {
            color:
                #64748b;

            font-size:
                11px;
        }

        .badge {
            display:
                inline-block;

            min-width:
                36px;

            padding:
                5px 8px;

            text-align:
                center;

            border-radius:
                20px;

            font-size:
                11px;

            font-weight:
                bold;

            background:
                #eef2f7;

            color:
                #334155;
        }

        .badge-completed {
            background:
                #ecfdf5;

            color:
                #047857;
        }

        .badge-cancelled {
            background:
                #fef2f2;

            color:
                #b91c1c;
        }

        .badge-scheduled {
            background:
                #eff6ff;

            color:
                #1d4ed8;
        }

        .money-cell {
            font-weight:
                bold;

            white-space:
                nowrap;
        }

        /* =====================================================
           EMPTY
           ===================================================== */

        .empty-state {
            padding:
                42px 20px;

            text-align:
                center;

            color:
                #64748b;

            font-size:
                13px;
        }

        .empty-state strong {
            display:
                block;

            color:
                #334155;

            font-size:
                15px;

            margin-bottom:
                7px;
        }

        /* =====================================================
           REPORT FOOTER
           ===================================================== */

        .report-footer {
            margin-top:
                26px;

            padding-top:
                18px;

            border-top:
                1px solid #e5e7eb;

            display:
                flex;

            justify-content:
                space-between;

            gap:
                20px;

            color:
                #64748b;

            font-size:
                11px;
        }

        /* =====================================================
           RESPONSIVE
           ===================================================== */

        @media (
            max-width: 1100px
        ) {

            .summary-grid {
                grid-template-columns:
                    repeat(
                        2,
                        1fr
                    );
            }

            .kpi-grid {
                grid-template-columns:
                    1fr;
            }
        }

        @media (
            max-width: 760px
        ) {

            .sidebar {
                position:
                    static;

                width:
                    100%;

                min-height:
                    auto;
            }

            .layout {
                display:
                    block;
            }

            .main {
                margin-left:
                    0;

                width:
                    100%;
            }

            .summary-grid {
                grid-template-columns:
                    1fr;
            }

            .report-heading {
                flex-direction:
                    column;
            }
        }

        /* =====================================================
           PRINT
           ===================================================== */

        @media print {

            @page {
                size:
                    A4 landscape;

                margin:
                    10mm;
            }

            body {
                background:
                    white;

                font-size:
                    10px;
            }

            .sidebar,
            .topbar,
            .filter-panel,
            .print-button {
                display:
                    none !important;
            }

            .main {
                margin:
                    0;

                width:
                    100%;
            }

            .content {
                padding:
                    0;
            }

            .report-heading {
                margin-bottom:
                    12px;
            }

            .summary-grid {
                grid-template-columns:
                    repeat(
                        4,
                        1fr
                    );

                gap:
                    7px;
            }

            .summary-card,
            .kpi-card,
            .report-card {
                box-shadow:
                    none;

                break-inside:
                    avoid;
            }

            .summary-card {
                padding:
                    10px;
            }

            .summary-value {
                font-size:
                    18px;
            }

            .finance-value {
                font-size:
                    15px;
            }

            .kpi-grid {
                grid-template-columns:
                    repeat(
                        3,
                        1fr
                    );

                gap:
                    7px;
            }

            .kpi-card {
                padding:
                    10px;
            }

            .section-title {
                margin:
                    14px 0 7px;
            }

            .report-card {
                margin-bottom:
                    12px;
            }

            th,
            td {
                padding:
                    6px 8px;

                font-size:
                    9px;
            }
        }

    </style>

</head>

<body>

<div class="layout">

    <!-- =====================================================
         SIDEBAR
         ===================================================== -->

    <aside class="sidebar">

        <div class="brand">

            <div class="brand-mark">
                &#10010;
            </div>

            <h2>
                Sunrise Dental
            </h2>

            <p>
                Clinic Management System
            </p>

        </div>

        <nav class="navigation">

            <a href="<%= request.getContextPath() %>/dashboard">

                <span class="nav-icon">
                    &#9632;
                </span>

                Dashboard

            </a>

            <a href="<%= request.getContextPath() %>/patients">

                <span class="nav-icon">
                    &#128100;
                </span>

                Patients

            </a>

            <a href="<%= request.getContextPath() %>/dentists">

                <span class="nav-icon">
                    &#9877;
                </span>

                Dentists

            </a>

            <a href="<%= request.getContextPath() %>/treatments">

                <span class="nav-icon">
                    &#10010;
                </span>

                Treatments

            </a>

            <a href="<%= request.getContextPath() %>/appointments">

                <span class="nav-icon">
                    &#128197;
                </span>

                Appointments

            </a>

            <a href="<%= request.getContextPath() %>/bills">

                <span class="nav-icon">
                    &#128179;
                </span>

                Billing

            </a>

            <a
                class="active"
                href="<%= request.getContextPath() %>/reports">

                <span class="nav-icon">
                    &#128202;
                </span>

                Reports

            </a>

        </nav>

        <div class="sidebar-footer">

            <a href="<%= request.getContextPath() %>/logout">
                Sign out
            </a>

        </div>

    </aside>

    <!-- =====================================================
         MAIN
         ===================================================== -->

    <main class="main">

        <header class="topbar">

            <div class="page-title">

                <h1>
                    Reports & Analytics
                </h1>

                <p>
                    Administrative decision-support dashboard
                </p>

            </div>

            <div class="user-box">

                <div class="user-details">

                    <strong>
                        <%= escapeHtml(fullName) %>
                    </strong>

                    <span>
                        <%= escapeHtml(role) %>
                    </span>

                </div>

                <div class="avatar">
                    <%= escapeHtml(avatarLetter) %>
                </div>

            </div>

        </header>

        <section class="content">

            <div class="report-heading">

                <div>

                    <h2>
                        Clinic Performance Report
                    </h2>

                    <p>
                        Review appointment activity,
                        financial performance,
                        dentist workload and treatment demand
                        for the selected reporting period.
                    </p>

                </div>

                <% if (report != null) { %>

                    <button
                        type="button"
                        class="print-button"
                        onclick="window.print();">

                        Print Management Report

                    </button>

                <% } %>

            </div>

            <!-- =================================================
                 DATE FILTER
                 ================================================= -->

            <div class="filter-panel">

                <div class="filter-title">
                    Reporting Period
                </div>

                <form
                    class="filter-form"
                    method="get"
                    action="<%= request.getContextPath() %>/reports">

                    <div class="form-group">

                        <label for="from">
                            From Date
                        </label>

                        <input
                            id="from"
                            name="from"
                            type="date"
                            class="form-control"
                            value="<%= escapeHtml(fromDate) %>"
                            required>

                    </div>

                    <div class="form-group">

                        <label for="to">
                            To Date
                        </label>

                        <input
                            id="to"
                            name="to"
                            type="date"
                            class="form-control"
                            value="<%= escapeHtml(toDate) %>"
                            required>

                    </div>

                    <button
                        type="submit"
                        class="generate-button">

                        Generate Report

                    </button>

                    <% if (report != null) { %>

                        <div class="period-note">

                            Report:
                            <strong>
                                <%= escapeHtml(report.getFromDate()) %>
                            </strong>

                            to

                            <strong>
                                <%= escapeHtml(report.getToDate()) %>
                            </strong>

                        </div>

                    <% } %>

                </form>

            </div>

            <% if (error != null
                    && !error.isBlank()) { %>

                <div class="alert-error">

                    <strong>
                        Report could not be generated.
                    </strong>

                    <%= escapeHtml(error) %>

                </div>

            <% } %>


            <% if (report != null) { %>

                <!-- =============================================
                     APPOINTMENT SUMMARY
                     ============================================= -->

                <div class="section-title">

                    <h3>
                        Appointment Activity
                    </h3>

                    <span>
                        Status distribution
                    </span>

                </div>

                <div class="summary-grid">

                    <div class="summary-card">

                        <div class="summary-label">
                            Total Appointments
                        </div>

                        <div class="summary-value">
                            <%= report.getTotalAppointments() %>
                        </div>

                        <div class="summary-subtext">
                            All appointments in selected period
                        </div>

                    </div>

                    <div class="summary-card">

                        <div class="summary-label">
                            Scheduled
                        </div>

                        <div class="summary-value">
                            <%= report.getScheduledAppointments() %>
                        </div>

                        <div class="summary-subtext">
                            Upcoming or awaiting completion
                        </div>

                    </div>

                    <div class="summary-card">

                        <div class="summary-label">
                            Completed
                        </div>

                        <div class="summary-value">
                            <%= report.getCompletedAppointments() %>
                        </div>

                        <div class="summary-subtext">
                            Successfully completed visits
                        </div>

                    </div>

                    <div class="summary-card">

                        <div class="summary-label">
                            Cancelled
                        </div>

                        <div class="summary-value">
                            <%= report.getCancelledAppointments() %>
                        </div>

                        <div class="summary-subtext">
                            Cancelled clinic appointments
                        </div>

                    </div>

                </div>

                <!-- =============================================
                     FINANCIAL SUMMARY
                     ============================================= -->

                <div class="section-title">

                    <h3>
                        Financial Performance
                    </h3>

                    <span>
                        LKR
                    </span>

                </div>

                <div class="summary-grid">

                    <div class="summary-card">

                        <div class="summary-label">
                            Total Billed
                        </div>

                        <div class="summary-value finance-value">

                            Rs.
                            <%= money(
                                    report.getTotalBilled()
                            ) %>

                        </div>

                        <div class="summary-subtext">
                            Total patient billing value
                        </div>

                    </div>

                    <div class="summary-card">

                        <div class="summary-label">
                            Total Collected
                        </div>

                        <div class="summary-value finance-value">

                            Rs.
                            <%= money(
                                    report.getTotalPaid()
                            ) %>

                        </div>

                        <div class="summary-subtext">
                            Value of bills recorded as paid
                        </div>

                    </div>

                    <div class="summary-card">

                        <div class="summary-label">
                            Outstanding
                        </div>

                        <div class="summary-value finance-value">

                            Rs.
                            <%= money(
                                    report.getOutstandingAmount()
                            ) %>

                        </div>

                        <div class="summary-subtext">
                            Current unpaid billing value
                        </div>

                    </div>

                    <div class="summary-card">

                        <div class="summary-label">
                            Collection Rate
                        </div>

                        <div class="summary-value finance-value">

                            <%= percent(
                                    report.getCollectionRate()
                            ) %>%

                        </div>

                        <div class="summary-subtext">
                            Paid amount as percentage of billed value
                        </div>

                    </div>

                </div>

                <!-- =============================================
                     MANAGEMENT KPIs
                     ============================================= -->

                <div class="section-title">

                    <h3>
                        Management Indicators
                    </h3>

                    <span>
                        Decision-support KPIs
                    </span>

                </div>

                <div class="kpi-grid">

                    <div class="kpi-card">

                        <div class="kpi-top">

                            <div class="kpi-name">
                                Completion Rate
                            </div>

                            <div class="kpi-number">

                                <%= percent(
                                        report.getCompletionRate()
                                ) %>%

                            </div>

                        </div>

                        <div class="progress">

                            <div
                                class="progress-bar"
                                style="width:
                                    <%= safeWidth(
                                            report.getCompletionRate()
                                    ) %>%;">
                            </div>

                        </div>

                        <div class="kpi-description">
                            Percentage of appointments that
                            reached the completed state.
                        </div>

                    </div>

                    <div class="kpi-card">

                        <div class="kpi-top">

                            <div class="kpi-name">
                                Cancellation Rate
                            </div>

                            <div class="kpi-number">

                                <%= percent(
                                        report.getCancellationRate()
                                ) %>%

                            </div>

                        </div>

                        <div class="progress">

                            <div
                                class="progress-bar"
                                style="width:
                                    <%= safeWidth(
                                            report.getCancellationRate()
                                    ) %>%;">
                            </div>

                        </div>

                        <div class="kpi-description">
                            Percentage of appointments cancelled
                            during the reporting period.
                        </div>

                    </div>

                    <div class="kpi-card">

                        <div class="kpi-top">

                            <div class="kpi-name">
                                Revenue Collection
                            </div>

                            <div class="kpi-number">

                                <%= percent(
                                        report.getCollectionRate()
                                ) %>%

                            </div>

                        </div>

                        <div class="progress">

                            <div
                                class="progress-bar"
                                style="width:
                                    <%= safeWidth(
                                            report.getCollectionRate()
                                    ) %>%;">
                            </div>

                        </div>

                        <div class="kpi-description">
                            Measures how much of the billed value
                            has been successfully collected.
                        </div>

                    </div>

                </div>

                <!-- =============================================
                     DENTIST WORKLOAD
                     ============================================= -->

                <div class="section-title">

                    <h3>
                        Dentist Workload
                    </h3>

                    <span>
                        Appointment distribution by dentist
                    </span>

                </div>

                <div class="report-card">

                    <div class="report-card-header">

                        <h3>
                            Dentist Performance Overview
                        </h3>

                        <p>
                            Supports workload monitoring and
                            appointment resource planning.
                        </p>

                    </div>

                    <% if (report.getDentistWorkload()
                            .isEmpty()) { %>

                        <div class="empty-state">

                            <strong>
                                No dentist activity
                            </strong>

                            No appointments were found for
                            this reporting period.

                        </div>

                    <% } else { %>

                        <div class="table-wrapper">

                            <table>

                                <thead>

                                <tr>

                                    <th>
                                        Dentist
                                    </th>

                                    <th>
                                        Total
                                    </th>

                                    <th>
                                        Scheduled
                                    </th>

                                    <th>
                                        Completed
                                    </th>

                                    <th>
                                        Cancelled
                                    </th>

                                    <th>
                                        Completion Rate
                                    </th>

                                </tr>

                                </thead>

                                <tbody>

                                <%
                                    for (
                                        ReportViewModel.DentistWorkload item
                                        : report.getDentistWorkload()
                                    ) {
                                %>

                                    <tr>

                                        <td class="name-cell">

                                            <strong>
                                                <%= escapeHtml(
                                                        item.getDentistName()
                                                ) %>
                                            </strong>

                                            <span>
                                                <%= escapeHtml(
                                                        item.getSpecialization()
                                                ) %>
                                            </span>

                                        </td>

                                        <td>

                                            <span class="badge">
                                                <%= item.getTotalAppointments() %>
                                            </span>

                                        </td>

                                        <td>

                                            <span class="badge badge-scheduled">
                                                <%= item.getScheduledAppointments() %>
                                            </span>

                                        </td>

                                        <td>

                                            <span class="badge badge-completed">
                                                <%= item.getCompletedAppointments() %>
                                            </span>

                                        </td>

                                        <td>

                                            <span class="badge badge-cancelled">
                                                <%= item.getCancelledAppointments() %>
                                            </span>

                                        </td>

                                        <td>

                                            <strong>
                                                <%= percent(
                                                        item.getCompletionRate()
                                                ) %>%
                                            </strong>

                                        </td>

                                    </tr>

                                <% } %>

                                </tbody>

                            </table>

                        </div>

                    <% } %>

                </div>

                <!-- =============================================
                     TREATMENT DEMAND
                     ============================================= -->

                <div class="section-title">

                    <h3>
                        Treatment Demand
                    </h3>

                    <span>
                        Popularity and billing contribution
                    </span>

                </div>

                <div class="report-card">

                    <div class="report-card-header">

                        <h3>
                            Treatment Demand Analysis
                        </h3>

                        <p>
                            Identifies frequently requested
                            treatments and their associated
                            billed value.
                        </p>

                    </div>

                    <% if (report.getTreatmentDemand()
                            .isEmpty()) { %>

                        <div class="empty-state">

                            <strong>
                                No treatment activity
                            </strong>

                            No non-cancelled treatment
                            appointments were found in this period.

                        </div>

                    <% } else { %>

                        <div class="table-wrapper">

                            <table>

                                <thead>

                                <tr>

                                    <th>
                                        Treatment Code
                                    </th>

                                    <th>
                                        Treatment
                                    </th>

                                    <th>
                                        Appointment Demand
                                    </th>

                                    <th>
                                        Billed Amount
                                    </th>

                                </tr>

                                </thead>

                                <tbody>

                                <%
                                    for (
                                        ReportViewModel.TreatmentDemand item
                                        : report.getTreatmentDemand()
                                    ) {
                                %>

                                    <tr>

                                        <td>
                                            <strong>
                                                <%= escapeHtml(
                                                        item.getTreatmentCode()
                                                ) %>
                                            </strong>
                                        </td>

                                        <td>
                                            <%= escapeHtml(
                                                    item.getTreatmentName()
                                            ) %>
                                        </td>

                                        <td>

                                            <span class="badge">
                                                <%= item.getAppointmentCount() %>
                                            </span>

                                        </td>

                                        <td class="money-cell">

                                            Rs.
                                            <%= money(
                                                    item.getBilledAmount()
                                            ) %>

                                        </td>

                                    </tr>

                                <% } %>

                                </tbody>

                            </table>

                        </div>

                    <% } %>

                </div>

                <div class="report-footer">

                    <div>

                        Sunrise Dental Clinic Management System
                        — Administrative Report

                    </div>

                    <div>

                        Period:
                        <%= escapeHtml(
                                report.getFromDate()
                        ) %>

                        to

                        <%= escapeHtml(
                                report.getToDate()
                        ) %>

                    </div>

                </div>

            <% } %>

        </section>

    </main>

</div>

<script>

    const fromInput =
            document.getElementById(
                    "from"
            );

    const toInput =
            document.getElementById(
                    "to"
            );

    if (fromInput && toInput) {

        fromInput.addEventListener(
                "change",
                function () {

                    if (fromInput.value) {
                        toInput.min =
                                fromInput.value;
                    }

                }
        );

        toInput.addEventListener(
                "change",
                function () {

                    if (toInput.value) {
                        fromInput.max =
                                toInput.value;
                    }

                }
        );
    }

</script>

</body>

</html>