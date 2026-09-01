<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.web.model.DashboardSummaryViewModel" %>
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

    private String money(BigDecimal value) {

        if (value == null) {
            value = BigDecimal.ZERO;
        }

        return new DecimalFormat(
                "#,##0.00"
        ).format(value);
    }
%>

<%
    DashboardSummaryViewModel summary =
            (DashboardSummaryViewModel)
                    request.getAttribute(
                            "dashboardSummary"
                    );

    if (summary == null) {
        summary =
                new DashboardSummaryViewModel();
    }

    boolean serviceAvailable =
            Boolean.TRUE.equals(
                    request.getAttribute(
                            "dashboardServiceAvailable"
                    )
            );

    String dashboardError =
            (String)
                    request.getAttribute(
                            "dashboardError"
                    );

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
        fullName = "Authorized Staff";
    }

    if ("null".equals(username)) {
        username = "";
    }

    if ("null".equals(role)) {
        role = "";
    }

    String avatarLetter = "U";

    if (!username.isBlank()) {

        avatarLetter =
                username.substring(
                        0,
                        1
                ).toUpperCase();
    }

    boolean isAdmin =
            "ADMIN".equalsIgnoreCase(
                    role
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
        Dashboard | Sunrise Dental Clinic
    </title>

    <style>

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family:
                "Segoe UI",
                Arial,
                sans-serif;

            background:
                #f4f7fb;

            color:
                #1e293b;

            min-height:
                100vh;
        }

        .layout {
            min-height:
                100vh;

            display:
                grid;

            grid-template-columns:
                250px 1fr;
        }

        /* =====================================================
           SIDEBAR
           ===================================================== */

        .sidebar {
            background:
                #102a43;

            color:
                #ffffff;

            padding:
                28px 18px;

            display:
                flex;

            flex-direction:
                column;

            min-height:
                100vh;
        }

        .brand {
            padding:
                0 10px 28px;

            border-bottom:
                1px solid
                rgba(
                    255,
                    255,
                    255,
                    0.10
                );
        }

        .brand-mark {
            width:
                46px;

            height:
                46px;

            border-radius:
                12px;

            background:
                rgba(
                    255,
                    255,
                    255,
                    0.12
                );

            display:
                flex;

            align-items:
                center;

            justify-content:
                center;

            font-size:
                23px;

            margin-bottom:
                14px;
        }

        .brand h2 {
            font-size:
                18px;

            line-height:
                1.3;
        }

        .brand p {
            margin-top:
                5px;

            color:
                #a9c1d8;

            font-size:
                11px;
        }

        .navigation {
            margin-top:
                28px;

            display:
                grid;

            gap:
                7px;
        }

        .navigation a {
            text-decoration:
                none;

            color:
                #cbdbea;

            padding:
                12px 13px;

            border-radius:
                8px;

            font-size:
                13px;

            font-weight:
                500;

            display:
                flex;

            align-items:
                center;

            gap:
                11px;

            transition:
                background 0.2s,
                color 0.2s;
        }

        .navigation a:hover {
            background:
                rgba(
                    255,
                    255,
                    255,
                    0.08
                );

            color:
                #ffffff;
        }

        .navigation a.active {
            background:
                #1f5f99;

            color:
                #ffffff;
        }

        .nav-icon {
            width:
                20px;

            text-align:
                center;
        }

        .sidebar-footer {
            margin-top:
                auto;

            padding:
                20px 10px 0;

            border-top:
                1px solid
                rgba(
                    255,
                    255,
                    255,
                    0.10
                );
        }

        .sidebar-footer a {
            color:
                #cbdbea;

            text-decoration:
                none;

            font-size:
                12px;
        }

        .sidebar-footer a:hover {
            color:
                #ffffff;
        }

        /* =====================================================
           MAIN
           ===================================================== */

        .main {
            min-width:
                0;
        }

        .topbar {
            height:
                74px;

            background:
                #ffffff;

            border-bottom:
                1px solid
                #e2e8f0;

            display:
                flex;

            align-items:
                center;

            justify-content:
                space-between;

            padding:
                0 32px;
        }

        .page-title h1 {
            color:
                #102a43;

            font-size:
                21px;

            font-weight:
                700;
        }

        .page-title p {
            margin-top:
                3px;

            color:
                #64748b;

            font-size:
                12px;
        }

        .user-box {
            display:
                flex;

            align-items:
                center;

            gap:
                12px;
        }

        .avatar {
            width:
                40px;

            height:
                40px;

            border-radius:
                50%;

            background:
                #e6eef8;

            color:
                #153e75;

            display:
                flex;

            align-items:
                center;

            justify-content:
                center;

            font-weight:
                700;

            font-size:
                14px;
        }

        .user-details {
            text-align:
                right;
        }

        .user-details strong {
            display:
                block;

            color:
                #1e293b;

            font-size:
                13px;
        }

        .user-details span {
            color:
                #64748b;

            font-size:
                11px;
        }

        /* =====================================================
           CONTENT
           ===================================================== */

        .content {
            padding:
                32px;
        }

        .welcome-panel {
            background:
                linear-gradient(
                    135deg,
                    #153e75,
                    #1f5f99
                );

            color:
                #ffffff;

            border-radius:
                14px;

            padding:
                30px;

            display:
                flex;

            justify-content:
                space-between;

            align-items:
                center;

            margin-bottom:
                24px;

            box-shadow:
                0 12px 28px
                rgba(
                    21,
                    62,
                    117,
                    0.14
                );
        }

        .welcome-panel h2 {
            font-size:
                23px;

            margin-bottom:
                7px;
        }

        .welcome-panel p {
            color:
                #d8e8f7;

            font-size:
                13px;

            line-height:
                1.6;
        }

        .welcome-icon {
            font-size:
                54px;

            opacity:
                0.22;
        }

        /* =====================================================
           ALERT
           ===================================================== */

        .service-alert {
            background:
                #fff7ed;

            border:
                1px solid
                #fed7aa;

            color:
                #9a3412;

            border-radius:
                10px;

            padding:
                13px 16px;

            margin-bottom:
                24px;

            font-size:
                12px;

            line-height:
                1.5;
        }

        /* =====================================================
           SECTION HEADING
           ===================================================== */

        .section-heading {
            display:
                flex;

            justify-content:
                space-between;

            align-items:
                end;

            gap:
                20px;

            margin-bottom:
                15px;
        }

        .section-heading h2 {
            color:
                #102a43;

            font-size:
                17px;
        }

        .section-heading p {
            color:
                #64748b;

            font-size:
                12px;

            margin-top:
                4px;
        }

        .facade-label {
            color:
                #64748b;

            font-size:
                11px;

            background:
                #ffffff;

            border:
                1px solid
                #e2e8f0;

            border-radius:
                20px;

            padding:
                7px 11px;
        }

        /* =====================================================
           LIVE STATISTICS
           ===================================================== */

        .stats-grid {
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

            margin-bottom:
                30px;
        }

        .stat-card {
            background:
                #ffffff;

            border:
                1px solid
                #e2e8f0;

            border-radius:
                12px;

            padding:
                19px;

            box-shadow:
                0 2px 7px
                rgba(
                    15,
                    23,
                    42,
                    0.03
                );
        }

        .stat-top {
            display:
                flex;

            justify-content:
                space-between;

            align-items:
                flex-start;

            gap:
                12px;

            margin-bottom:
                12px;
        }

        .stat-label {
            color:
                #64748b;

            font-size:
                11px;

            font-weight:
                600;

            line-height:
                1.4;
        }

        .stat-icon {
            width:
                35px;

            height:
                35px;

            border-radius:
                9px;

            background:
                #edf4fb;

            color:
                #1f5f99;

            display:
                flex;

            align-items:
                center;

            justify-content:
                center;

            font-size:
                16px;

            flex-shrink:
                0;
        }

        .stat-value {
            color:
                #102a43;

            font-size:
                26px;

            font-weight:
                700;

            line-height:
                1.15;
        }

        .stat-value.money {
            font-size:
                20px;
        }

        .stat-subtext {
            color:
                #94a3b8;

            font-size:
                10px;

            margin-top:
                7px;

            line-height:
                1.45;
        }

        /* =====================================================
           MODULES
           ===================================================== */

        .module-grid {
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
                18px;

            margin-bottom:
                30px;
        }

        .module-card {
            background:
                #ffffff;

            border:
                1px solid
                #e2e8f0;

            border-radius:
                12px;

            padding:
                22px;

            text-decoration:
                none;

            color:
                inherit;

            transition:
                transform 0.2s,
                box-shadow 0.2s,
                border-color 0.2s;
        }

        .module-card:hover {
            transform:
                translateY(-3px);

            box-shadow:
                0 10px 24px
                rgba(
                    15,
                    23,
                    42,
                    0.08
                );

            border-color:
                #b8cce0;
        }

        .module-icon {
            width:
                43px;

            height:
                43px;

            border-radius:
                10px;

            background:
                #edf4fb;

            color:
                #1f5f99;

            display:
                flex;

            align-items:
                center;

            justify-content:
                center;

            font-size:
                20px;

            margin-bottom:
                17px;
        }

        .module-card h3 {
            color:
                #102a43;

            font-size:
                14px;

            margin-bottom:
                7px;
        }

        .module-card p {
            color:
                #64748b;

            font-size:
                11px;

            line-height:
                1.55;
        }

        /* =====================================================
           BOTTOM PANELS
           ===================================================== */

        .bottom-grid {
            display:
                grid;

            grid-template-columns:
                1.5fr 1fr;

            gap:
                18px;
        }

        .panel {
            background:
                #ffffff;

            border:
                1px solid
                #e2e8f0;

            border-radius:
                12px;

            padding:
                22px;
        }

        .panel h3 {
            color:
                #102a43;

            font-size:
                15px;

            margin-bottom:
                16px;
        }

        .workflow {
            display:
                grid;

            gap:
                13px;
        }

        .workflow-item {
            display:
                flex;

            align-items:
                flex-start;

            gap:
                11px;

            padding-bottom:
                13px;

            border-bottom:
                1px solid
                #edf2f7;
        }

        .workflow-item:last-child {
            border-bottom:
                none;

            padding-bottom:
                0;
        }

        .workflow-number {
            width:
                27px;

            height:
                27px;

            border-radius:
                50%;

            background:
                #edf4fb;

            color:
                #1f5f99;

            display:
                flex;

            align-items:
                center;

            justify-content:
                center;

            font-size:
                11px;

            font-weight:
                700;

            flex-shrink:
                0;
        }

        .workflow-text strong {
            display:
                block;

            color:
                #334155;

            font-size:
                12px;

            margin-bottom:
                3px;
        }

        .workflow-text span {
            color:
                #64748b;

            font-size:
                11px;
        }

        .system-info {
            display:
                grid;

            gap:
                14px;
        }

        .info-row {
            display:
                flex;

            justify-content:
                space-between;

            gap:
                15px;

            font-size:
                12px;
        }

        .info-row span {
            color:
                #64748b;
        }

        .info-row strong {
            color:
                #334155;

            text-align:
                right;
        }

        .online {
            color:
                #15803d !important;
        }

        .offline {
            color:
                #b91c1c !important;
        }

        /* =====================================================
           RESPONSIVE
           ===================================================== */

        @media (
            max-width: 1180px
        ) {

            .stats-grid {
                grid-template-columns:
                    repeat(
                        2,
                        minmax(
                            0,
                            1fr
                        )
                    );
            }

            .module-grid {
                grid-template-columns:
                    repeat(
                        2,
                        minmax(
                            0,
                            1fr
                        )
                    );
            }
        }

        @media (
            max-width: 850px
        ) {

            .layout {
                grid-template-columns:
                    1fr;
            }

            .sidebar {
                display:
                    none;
            }

            .bottom-grid {
                grid-template-columns:
                    1fr;
            }
        }

        @media (
            max-width: 600px
        ) {

            .topbar {
                padding:
                    0 18px;
            }

            .content {
                padding:
                    20px;
            }

            .stats-grid,
            .module-grid {
                grid-template-columns:
                    1fr;
            }

            .welcome-panel {
                padding:
                    24px;
            }

            .welcome-icon {
                display:
                    none;
            }

            .user-details {
                display:
                    none;
            }

            .section-heading {
                align-items:
                    flex-start;

                flex-direction:
                    column;
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

            <a
                class="active"
                href="<%= request.getContextPath() %>/dashboard">

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

            <% if (isAdmin) { %>

                <a href="<%= request.getContextPath() %>/reports">

                    <span class="nav-icon">
                        &#128202;
                    </span>

                    Reports

                </a>

                <div class="nav-section-title">
    Administration
</div>

<a class="nav-item"
   href="<%= request.getContextPath() %>/users">

    <span class="nav-icon">
        &#128101;
    </span>

    <span>
        Staff Users
    </span>

</a>

            <% } %>



<a class="nav-item"
   href="<%= request.getContextPath() %>/help">

    <span class="nav-icon">
        &#10067;
    </span>

    <span>
        Help &amp; User Guide
    </span>

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
                    Dashboard
                </h1>

                <p>
                    Sunrise Dental Clinic operations
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

            <!-- =================================================
                 WELCOME
                 ================================================= -->

            <div class="welcome-panel">

                <div>

                    <h2>
                        Welcome,
                        <%= escapeHtml(fullName) %>
                    </h2>

                    <p>
                        Manage patient records,
                        appointments, treatments and billing
                        from one secure distributed workspace.
                    </p>

                </div>

                <div class="welcome-icon">
                    &#10010;
                </div>

            </div>

            <% if (!serviceAvailable) { %>

                <div class="service-alert">

                    <strong>
                        Live dashboard information is unavailable.
                    </strong>

                    <%= escapeHtml(
                            dashboardError
                    ) %>

                </div>

            <% } %>

            <!-- =================================================
                 LIVE CLINIC SUMMARY
                 ================================================= -->

            <div class="section-heading">

                <div>

                    <h2>
                        Live Clinic Overview
                    </h2>

                    <p>
                        Aggregated operational information
                        across the clinic subsystems.
                    </p>

                </div>

                <div class="facade-label">
                    Dashboard Facade
                </div>

            </div>

            <div class="stats-grid">

                <!-- PATIENTS -->

                <div class="stat-card">

                    <div class="stat-top">

                        <div class="stat-label">
                            Registered Patients
                        </div>

                        <div class="stat-icon">
                            &#128100;
                        </div>

                    </div>

                    <div class="stat-value">
                        <%= summary.getTotalPatients() %>
                    </div>

                    <div class="stat-subtext">
                        Patient records currently available
                    </div>

                </div>

                <!-- DENTISTS -->

                <div class="stat-card">

                    <div class="stat-top">

                        <div class="stat-label">
                            Active Dentists
                        </div>

                        <div class="stat-icon">
                            &#9877;
                        </div>

                    </div>

                    <div class="stat-value">
                        <%= summary.getActiveDentists() %>
                    </div>

                    <div class="stat-subtext">

                        <%= summary.getActiveDentists() %>
                        active of
                        <%= summary.getTotalDentists() %>
                        registered dentists

                    </div>

                </div>

                <!-- TREATMENTS -->

                <div class="stat-card">

                    <div class="stat-top">

                        <div class="stat-label">
                            Active Treatments
                        </div>

                        <div class="stat-icon">
                            &#10010;
                        </div>

                    </div>

                    <div class="stat-value">
                        <%= summary.getActiveTreatments() %>
                    </div>

                    <div class="stat-subtext">

                        <%= summary.getActiveTreatments() %>
                        active of
                        <%= summary.getTotalTreatments() %>
                        treatment types

                    </div>

                </div>

                <!-- TOTAL APPOINTMENTS -->

                <div class="stat-card">

                    <div class="stat-top">

                        <div class="stat-label">
                            Total Appointments
                        </div>

                        <div class="stat-icon">
                            &#128197;
                        </div>

                    </div>

                    <div class="stat-value">
                        <%= summary.getTotalAppointments() %>
                    </div>

                    <div class="stat-subtext">
                        All appointment records
                    </div>

                </div>

                <!-- SCHEDULED -->

                <div class="stat-card">

                    <div class="stat-top">

                        <div class="stat-label">
                            Scheduled Appointments
                        </div>

                        <div class="stat-icon">
                            &#9200;
                        </div>

                    </div>

                    <div class="stat-value">
                        <%= summary.getScheduledAppointments() %>
                    </div>

                    <div class="stat-subtext">
                        Appointments awaiting completion
                    </div>

                </div>

                <!-- COMPLETED -->

                <div class="stat-card">

                    <div class="stat-top">

                        <div class="stat-label">
                            Completed Appointments
                        </div>

                        <div class="stat-icon">
                            &#10003;
                        </div>

                    </div>

                    <div class="stat-value">
                        <%= summary.getCompletedAppointments() %>
                    </div>

                    <div class="stat-subtext">

                        Cancelled:
                        <%= summary.getCancelledAppointments() %>

                    </div>

                </div>

                <!-- TOTAL BILLED -->

                <div class="stat-card">

                    <div class="stat-top">

                        <div class="stat-label">
                            Total Billed
                        </div>

                        <div class="stat-icon">
                            &#128179;
                        </div>

                    </div>

                    <div class="stat-value money">

                        Rs.
                        <%= money(
                                summary.getTotalBilled()
                        ) %>

                    </div>

                    <div class="stat-subtext">

                        <%= summary.getPaidBills() %>
                        paid bill(s) of
                        <%= summary.getTotalBills() %>

                    </div>

                </div>

                <!-- OUTSTANDING -->

                <div class="stat-card">

                    <div class="stat-top">

                        <div class="stat-label">
                            Outstanding Amount
                        </div>

                        <div class="stat-icon">
                            &#9888;
                        </div>

                    </div>

                    <div class="stat-value money">

                        Rs.
                        <%= money(
                                summary.getOutstandingAmount()
                        ) %>

                    </div>

                    <div class="stat-subtext">

                        Unpaid bills:
                        <%= summary.getUnpaidBills() %>

                    </div>

                </div>

            </div>

            <!-- =================================================
                 MODULES
                 ================================================= -->

            <div class="section-heading">

                <div>

                    <h2>
                        Clinic Management
                    </h2>

                    <p>
                        Select a module to continue.
                    </p>

                </div>

            </div>

            <div class="module-grid">

                <a
                    class="module-card"
                    href="<%= request.getContextPath() %>/patients">

                    <div class="module-icon">
                        &#128100;
                    </div>

                    <h3>
                        Patients
                    </h3>

                    <p>
                        Register, search and maintain
                        patient information.
                    </p>

                </a>

                <a
                    class="module-card"
                    href="<%= request.getContextPath() %>/appointments">

                    <div class="module-icon">
                        &#128197;
                    </div>

                    <h3>
                        Appointments
                    </h3>

                    <p>
                        Register, reschedule, complete
                        and review appointment history.
                    </p>

                </a>

                <a
                    class="module-card"
                    href="<%= request.getContextPath() %>/treatments">

                    <div class="module-icon">
                        &#10010;
                    </div>

                    <h3>
                        Treatments
                    </h3>

                    <p>
                        Review treatment information,
                        prices and consultation fees.
                    </p>

                </a>

                <a
                    class="module-card"
                    href="<%= request.getContextPath() %>/bills">

                    <div class="module-icon">
                        &#128179;
                    </div>

                    <h3>
                        Billing
                    </h3>

                    <p>
                        Generate patient bills,
                        record payments and print receipts.
                    </p>

                </a>

                <% if (isAdmin) { %>

                    <a
                        class="module-card"
                        href="<%= request.getContextPath() %>/reports">

                        <div class="module-icon">
                            &#128202;
                        </div>

                        <h3>
                            Reports & Analytics
                        </h3>

                        <p>
                            Review clinic performance,
                            revenue, workload and treatment demand.
                        </p>

                    </a>

                <% } %>

            </div>

            <!-- =================================================
                 WORKFLOW + SYSTEM INFORMATION
                 ================================================= -->

            <div class="bottom-grid">

                <div class="panel">

                    <h3>
                        Standard Clinic Workflow
                    </h3>

                    <div class="workflow">

                        <div class="workflow-item">

                            <div class="workflow-number">
                                1
                            </div>

                            <div class="workflow-text">

                                <strong>
                                    Register Patient
                                </strong>

                                <span>
                                    Capture and maintain
                                    patient information securely.
                                </span>

                            </div>

                        </div>

                        <div class="workflow-item">

                            <div class="workflow-number">
                                2
                            </div>

                            <div class="workflow-text">

                                <strong>
                                    Schedule Appointment
                                </strong>

                                <span>
                                    Select patient, dentist,
                                    treatment, date and available time.
                                </span>

                            </div>

                        </div>

                        <div class="workflow-item">

                            <div class="workflow-number">
                                3
                            </div>

                            <div class="workflow-text">

                                <strong>
                                    Complete Treatment
                                </strong>

                                <span>
                                    Complete the clinical appointment
                                    before billing is permitted.
                                </span>

                            </div>

                        </div>

                        <div class="workflow-item">

                            <div class="workflow-number">
                                4
                            </div>

                            <div class="workflow-text">

                                <strong>
                                    Generate Patient Bill
                                </strong>

                                <span>
                                    Calculate treatment,
                                    consultation and adjustment charges.
                                </span>

                            </div>

                        </div>

                    </div>

                </div>

                <div class="panel">

                    <h3>
                        System Information
                    </h3>

                    <div class="system-info">

                        <div class="info-row">

                            <span>
                                Username
                            </span>

                            <strong>
                                <%= escapeHtml(username) %>
                            </strong>

                        </div>

                        <div class="info-row">

                            <span>
                                Role
                            </span>

                            <strong>
                                <%= escapeHtml(role) %>
                            </strong>

                        </div>

                        <div class="info-row">

                            <span>
                                Authentication
                            </span>

                            <strong class="online">
                                Active
                            </strong>

                        </div>

                        <div class="info-row">

                            <span>
                                API Aggregation
                            </span>

                            <% if (serviceAvailable) { %>

                                <strong class="online">
                                    Connected
                                </strong>

                            <% } else { %>

                                <strong class="offline">
                                    Unavailable
                                </strong>

                            <% } %>

                        </div>

                        <div class="info-row">

                            <span>
                                Dashboard Pattern
                            </span>

                            <strong>
                                Facade
                            </strong>

                        </div>

                        <div class="info-row">

                            <span>
                                Total Paid
                            </span>

                            <strong>

                                Rs.
                                <%= money(
                                        summary.getTotalPaid()
                                ) %>

                            </strong>

                        </div>

                    </div>

                </div>

            </div>

        </section>

    </main>

</div>

</body>

</html>