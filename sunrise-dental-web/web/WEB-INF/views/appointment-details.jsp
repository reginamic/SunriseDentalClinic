<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@page import="java.util.Locale"%>
<%@page import="com.sunrisedental.web.model.AppointmentViewModel"%>


<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="com.sunrisedental.web.model.AppointmentHistoryViewModel"%>


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


    private String display(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "—";
        }

        return value;
    }


    private String money(
            Double value) {

        if (value == null) {
            return "0.00";
        }

        return String.format(
                Locale.US,
                "%,.2f",
                value
        );
    }
%>


<%
    String contextPath =
            request.getContextPath();


    AppointmentViewModel appointment =
            (AppointmentViewModel)
                    request.getAttribute(
                            "appointment"
                    );
    
    
    List<AppointmentHistoryViewModel> appointmentHistory =
        (List<AppointmentHistoryViewModel>)
                request.getAttribute(
                        "appointmentHistory"
                );


if (appointmentHistory == null) {

    appointmentHistory =
            Collections.emptyList();
}


    String errorMessage =
            (String)
                    request.getAttribute(
                            "errorMessage"
                    );


    String successMessage =
            (String)
                    session.getAttribute(
                            "successMessage"
                    );


    if (successMessage != null) {

        session.removeAttribute(
                "successMessage"
        );
    }


    String sessionError =
            (String)
                    session.getAttribute(
                            "errorMessage"
                    );


    if (errorMessage == null
            && sessionError != null) {

        errorMessage =
                sessionError;

        session.removeAttribute(
                "errorMessage"
        );
    }


    String loggedInName =
            (String)
                    session.getAttribute(
                            "fullName"
                    );


    String loggedInRole =
            (String)
                    session.getAttribute(
                            "role"
                    );


    boolean scheduled =
            appointment != null
            && appointment.isScheduled();


    boolean completed =
            appointment != null
            && appointment.isCompleted();


    boolean cancelled =
            appointment != null
            && appointment.isCancelled();


    String statusClass =
            "status-default";


    if (scheduled) {

        statusClass =
                "status-scheduled";

    } else if (completed) {

        statusClass =
                "status-completed";

    } else if (cancelled) {

        statusClass =
                "status-cancelled";
    }
%>


<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Appointment Details | Sunrise Dental
    </title>


    <style>

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }


        body {
            font-family:
                    "Segoe UI",
                    Arial,
                    sans-serif;

            background: #f4f7fb;
            color: #26384b;
            min-height: 100vh;
        }


        a {
            text-decoration: none;
        }


        button {
            font: inherit;
        }


        /* =====================================================
           LAYOUT
           ===================================================== */

        .app-layout {
            display: flex;
            min-height: 100vh;
        }


        /* =====================================================
           SIDEBAR
           ===================================================== */

        .sidebar {
            width: 255px;
            background: #15324b;
            color: white;
            padding: 26px 18px;

            position: fixed;
            top: 0;
            bottom: 0;
            left: 0;

            overflow-y: auto;
        }


        .brand {
            padding: 0 10px 25px 10px;

            border-bottom:
                    1px solid rgba(255,255,255,0.12);

            margin-bottom: 22px;
        }


        .brand h2 {
            font-size: 22px;
            margin-bottom: 5px;
        }


        .brand span {
            color: #b7c8d6;
            font-size: 12px;
        }


        .nav-title {
            color: #829caf;
            text-transform: uppercase;
            font-size: 11px;
            letter-spacing: 1.2px;
            margin: 20px 12px 9px 12px;
        }


        .nav-link {
            display: block;
            color: #d9e4ec;
            padding: 11px 13px;
            border-radius: 8px;
            margin-bottom: 5px;
            font-size: 14px;
        }


        .nav-link:hover {
            background: rgba(255,255,255,0.08);
            color: white;
        }


        .nav-link.active {
            background: #246b86;
            color: white;
            font-weight: 600;
        }


        .sidebar-footer {
            margin-top: 28px;
            padding: 15px 12px;

            border-top:
                    1px solid rgba(255,255,255,0.12);
        }


        .user-name {
            font-size: 14px;
            font-weight: 600;
        }


        .user-role {
            color: #a9bdcc;
            font-size: 11px;
            margin-top: 4px;
        }


        .logout-link {
            display: inline-block;
            margin-top: 12px;
            color: white;
            font-size: 13px;
        }


        /* =====================================================
           MAIN
           ===================================================== */

        .main-content {
            margin-left: 255px;
            width: calc(100% - 255px);
            padding: 30px;
        }


        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 20px;
            margin-bottom: 23px;
        }


        .page-header h1 {
            font-size: 27px;
            color: #1f3347;
            margin-bottom: 6px;
        }


        .page-header p {
            color: #748598;
            font-size: 14px;
        }


        /* =====================================================
           BUTTONS
           ===================================================== */

        .btn {
            display: inline-block;
            border: none;
            border-radius: 7px;
            padding: 10px 16px;
            cursor: pointer;
            font-size: 13px;
            font-weight: 600;
        }


        .btn-primary {
            background: #176b87;
            color: white;
        }


        .btn-primary:hover {
            background: #115a72;
        }


        .btn-secondary {
            background: #e7edf3;
            color: #34485c;
        }


        .btn-secondary:hover {
            background: #d9e2eb;
        }


        .btn-success {
            background: #24845a;
            color: white;
        }


        .btn-success:hover {
            background: #1c6b49;
        }


        .btn-danger {
            background: #bd4545;
            color: white;
        }


        .btn-danger:hover {
            background: #a23838;
        }


        .btn-outline {
            background: white;
            color: #176b87;
            border: 1px solid #b7cbd4;
        }


        .btn-outline:hover {
            background: #f1f7f9;
        }


        .btn-disabled {
            background: #e4e8ec;
            color: #9aa6b1;
            cursor: not-allowed;
        }


        /* =====================================================
           ALERTS
           ===================================================== */

        .alert {
            margin-bottom: 20px;
            padding: 13px 16px;
            border-radius: 8px;
            font-size: 13px;
        }


        .alert-success {
            background: #e7f6ed;
            border: 1px solid #b9e1c9;
            color: #21653f;
        }


        .alert-error {
            background: #fff0f0;
            border: 1px solid #efc0c0;
            color: #982f2f;
        }


        /* =====================================================
           ERROR PAGE
           ===================================================== */

        .error-panel {
            max-width: 700px;
            margin: 40px auto;

            background: white;

            border:
                    1px solid #e1e8ef;

            border-radius: 10px;

            padding: 35px;

            text-align: center;
        }


        .error-panel h2 {
            margin-bottom: 10px;
        }


        .error-panel p {
            color: #758596;
            margin-bottom: 20px;
        }


        /* =====================================================
           APPOINTMENT HERO
           ===================================================== */

        .appointment-hero {
            background: white;

            border:
                    1px solid #e1e8ef;

            border-radius: 11px;

            padding: 22px;

            margin-bottom: 22px;

            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 20px;

            box-shadow:
                    0 2px 8px rgba(31,51,71,0.04);
        }


        .appointment-number-label {
            color: #7a8999;
            font-size: 11px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 5px;
        }


        .appointment-number {
            font-size: 25px;
            font-weight: 700;
            color: #176b87;
        }


        .appointment-meta {
            margin-top: 7px;
            color: #748597;
            font-size: 13px;
        }


        /* =====================================================
           STATUS
           ===================================================== */

        .status-badge {
            display: inline-block;
            border-radius: 20px;
            padding: 7px 13px;
            font-size: 11px;
            font-weight: 700;
            letter-spacing: 0.4px;
        }


        .status-scheduled {
            background: #fff3d5;
            color: #8d6410;
        }


        .status-completed {
            background: #dff4e8;
            color: #216b47;
        }


        .status-cancelled {
            background: #fde4e4;
            color: #a53a3a;
        }


        .status-default {
            background: #e8edf2;
            color: #596978;
        }


        /* =====================================================
           CONTENT GRID
           ===================================================== */

        .content-grid {
            display: grid;

            grid-template-columns:
                    minmax(0, 2fr)
                    minmax(300px, 1fr);

            gap: 22px;

            align-items: start;
        }


        .panel {
            background: white;

            border:
                    1px solid #e1e8ef;

            border-radius: 11px;

            box-shadow:
                    0 2px 8px rgba(31,51,71,0.04);

            margin-bottom: 22px;

            overflow: hidden;
        }


        .panel-header {
            padding: 18px 21px;

            border-bottom:
                    1px solid #e6ebf0;
        }


        .panel-header h2 {
            font-size: 17px;
            color: #2a3d50;
            margin-bottom: 4px;
        }


        .panel-header p {
            font-size: 12px;
            color: #8493a2;
        }


        .panel-body {
            padding: 21px;
        }


        /* =====================================================
           DETAIL GRID
           ===================================================== */

        .detail-grid {
            display: grid;
            grid-template-columns:
                    repeat(2, minmax(0, 1fr));
            gap: 20px;
        }


        .detail-item {
            min-width: 0;
        }


        .detail-item.full {
            grid-column: span 2;
        }


        .detail-label {
            color: #8190a0;
            font-size: 10px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 5px;
        }


        .detail-value {
            color: #293d51;
            font-size: 14px;
            font-weight: 600;
            word-wrap: break-word;
        }


        .detail-sub {
            margin-top: 3px;
            color: #8595a5;
            font-size: 12px;
            font-weight: 400;
        }


        /* =====================================================
           CHARGE SUMMARY
           ===================================================== */

        .charge-row {
            display: flex;
            justify-content: space-between;
            gap: 12px;

            padding: 11px 0;

            border-bottom:
                    1px solid #edf1f4;

            font-size: 13px;
        }


        .charge-row:last-of-type {
            border-bottom: none;
        }


        .charge-label {
            color: #657789;
        }


        .charge-value {
            font-weight: 600;
            color: #293d51;
        }


        .total-charge {
            background: #eef7fa;

            border:
                    1px solid #cee4ea;

            border-radius: 8px;

            padding: 15px;

            margin-top: 17px;
        }


        .total-charge-label {
            color: #668292;
            font-size: 10px;
            text-transform: uppercase;
            margin-bottom: 5px;
        }


        .total-charge-value {
            color: #176b87;
            font-size: 25px;
            font-weight: 700;
        }


        /* =====================================================
           NOTES
           ===================================================== */

        .notes-box {
            background: #f8fafc;

            border:
                    1px solid #e2e8ee;

            border-radius: 7px;

            padding: 15px;

            color: #435669;

            font-size: 13px;

            line-height: 1.6;

            white-space: pre-wrap;
        }


        /* =====================================================
           ACTION PANEL
           ===================================================== */

        .action-panel {
            position: sticky;
            top: 30px;
        }


        .action-list {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }


        .action-list .btn {
            text-align: center;
            width: 100%;
        }


        .action-description {
            color: #7a8999;
            font-size: 11px;
            line-height: 1.5;
            margin-top: 5px;
        }


        .final-status-box {
            background: #f6f8fa;

            border:
                    1px solid #e1e7ec;

            border-radius: 8px;

            padding: 14px;

            font-size: 12px;

            color: #667789;

            line-height: 1.5;
        }


        /* =====================================================
           HISTORY PLACEHOLDER
           ===================================================== */

        .history-box {
            border-left:
                    3px solid #176b87;

            background: #f8fbfc;

            padding: 14px 16px;

            border-radius: 5px;

            color: #53697c;

            font-size: 12px;

            line-height: 1.6;
        }


        /* =====================================================
           RESPONSIVE
           ===================================================== */

        @media (max-width: 1000px) {

            .content-grid {
                grid-template-columns: 1fr;
            }


            .action-panel {
                position: static;
            }
        }


        @media (max-width: 760px) {

            .sidebar {
                display: none;
            }


            .main-content {
                margin-left: 0;
                width: 100%;
                padding: 18px;
            }


            .page-header {
                flex-direction: column;
            }


            .appointment-hero {
                flex-direction: column;
                align-items: flex-start;
            }


            .detail-grid {
                grid-template-columns: 1fr;
            }


            .detail-item.full {
                grid-column: span 1;
            }
        }

    </style>

</head>


<body>

<div class="app-layout">


    <!-- =====================================================
         SIDEBAR
         ===================================================== -->

    <aside class="sidebar">

        <div class="brand">

            <h2>
                Sunrise Dental
            </h2>

            <span>
                Clinic Management System
            </span>

        </div>


        <div class="nav-title">
            Main
        </div>


        <a class="nav-link"
           href="<%= contextPath %>/dashboard">

            Dashboard

        </a>


        <div class="nav-title">
            Clinic Management
        </div>


        <a class="nav-link"
           href="<%= contextPath %>/patients">

            Patients

        </a>


        <a class="nav-link"
           href="<%= contextPath %>/dentists">

            Dentists

        </a>


        <a class="nav-link"
           href="<%= contextPath %>/treatments">

            Treatments

        </a>


        <a class="nav-link active"
           href="<%= contextPath %>/appointments">

            Appointments

        </a>


        <a class="nav-link"
           href="<%= contextPath %>/bills">

            Billing

        </a>


        <div class="sidebar-footer">

            <div class="user-name">

                <%= escapeHtml(
                        loggedInName == null
                                ? "Signed-in User"
                                : loggedInName
                ) %>

            </div>


            <div class="user-role">

                <%= escapeHtml(
                        loggedInRole == null
                                ? ""
                                : loggedInRole
                ) %>

            </div>


            <a class="logout-link"
               href="<%= contextPath %>/logout">

                Sign Out

            </a>

        </div>

    </aside>


    <!-- =====================================================
         MAIN
         ===================================================== -->

    <main class="main-content">


        <div class="page-header">

            <div>

                <h1>
                    Appointment Details
                </h1>

                <p>
                    View complete patient, dentist,
                    treatment and scheduling information.
                </p>

            </div>


            <a class="btn btn-secondary"
               href="<%= contextPath %>/appointments">

                ← Back to Appointments

            </a>

        </div>


        <!-- =================================================
             MESSAGES
             ================================================= -->

        <% if (successMessage != null
                && !successMessage.isBlank()) { %>

            <div class="alert alert-success">

                <%= escapeHtml(successMessage) %>

            </div>

        <% } %>


        <% if (errorMessage != null
                && !errorMessage.isBlank()) { %>

            <div class="alert alert-error">

                <%= escapeHtml(errorMessage) %>

            </div>

        <% } %>


        <!-- =================================================
             APPOINTMENT NOT AVAILABLE
             ================================================= -->

        <% if (appointment == null) { %>

            <div class="error-panel">

                <h2>
                    Appointment unavailable
                </h2>

                <p>
                    The requested appointment could not
                    be displayed.
                </p>

                <a class="btn btn-primary"
                   href="<%= contextPath %>/appointments">

                    Return to Appointments

                </a>

            </div>

        <% } else { %>


        <!-- =================================================
             APPOINTMENT HERO
             ================================================= -->

        <section class="appointment-hero">

            <div>

                <div class="appointment-number-label">
                    Appointment Number
                </div>


                <div class="appointment-number">

                    <%= escapeHtml(
                            appointment.getAppointmentNumber()
                    ) %>

                </div>


                <div class="appointment-meta">

                    Appointment ID:
                    <%= appointment.getAppointmentId() %>

                    &nbsp; • &nbsp;

                    <%= escapeHtml(
                            display(
                                    appointment.getAppointmentDate()
                            )
                    ) %>

                    at

                    <%= escapeHtml(
                            display(
                                    appointment.getAppointmentTime()
                            )
                    ) %>

                </div>

            </div>


            <span class="status-badge <%= statusClass %>">

                <%= escapeHtml(
                        display(
                                appointment.getStatus()
                        )
                ) %>

            </span>

        </section>


        <!-- =================================================
             CONTENT GRID
             ================================================= -->

        <div class="content-grid">


            <!-- =============================================
                 LEFT CONTENT
                 ============================================= -->

            <div>


                <!-- PATIENT -->

                <section class="panel">

                    <div class="panel-header">

                        <h2>
                            Patient Information
                        </h2>

                        <p>
                            Registered patient details linked
                            to this appointment.
                        </p>

                    </div>


                    <div class="panel-body">

                        <div class="detail-grid">


                            <div class="detail-item">

                                <div class="detail-label">
                                    Patient Code
                                </div>

                                <div class="detail-value">

                                    <%= escapeHtml(
                                            display(
                                                    appointment
                                                            .getPatientCode()
                                            )
                                    ) %>

                                </div>

                            </div>


                            <div class="detail-item">

                                <div class="detail-label">
                                    Patient Name
                                </div>

                                <div class="detail-value">

                                    <%= escapeHtml(
                                            display(
                                                    appointment
                                                            .getPatientName()
                                            )
                                    ) %>

                                </div>

                            </div>


                            <div class="detail-item">

                                <div class="detail-label">
                                    Contact Number
                                </div>

                                <div class="detail-value">

                                    <%= escapeHtml(
                                            display(
                                                    appointment
                                                            .getPatientContactNumber()
                                            )
                                    ) %>

                                </div>

                            </div>


                            <div class="detail-item">

                                <div class="detail-label">
                                    Email
                                </div>

                                <div class="detail-value">

                                    <%= escapeHtml(
                                            display(
                                                    appointment
                                                            .getPatientEmail()
                                            )
                                    ) %>

                                </div>

                            </div>


                            <div class="detail-item full">

                                <div class="detail-label">
                                    Address
                                </div>

                                <div class="detail-value">

                                    <%= escapeHtml(
                                            display(
                                                    appointment
                                                            .getPatientAddress()
                                            )
                                    ) %>

                                </div>

                            </div>

                        </div>

                    </div>

                </section>


                <!-- DENTIST + TREATMENT -->

                <section class="panel">

                    <div class="panel-header">

                        <h2>
                            Dentist &amp; Treatment
                        </h2>

                        <p>
                            Clinical service assigned to
                            this appointment.
                        </p>

                    </div>


                    <div class="panel-body">

                        <div class="detail-grid">


                            <div class="detail-item">

                                <div class="detail-label">
                                    Dentist
                                </div>

                                <div class="detail-value">

                                    <%= escapeHtml(
                                            display(
                                                    appointment
                                                            .getDentistName()
                                            )
                                    ) %>

                                    <div class="detail-sub">

                                        <%= escapeHtml(
                                                display(
                                                        appointment
                                                                .getDentistCode()
                                                )
                                        ) %>

                                    </div>

                                </div>

                            </div>


                            <div class="detail-item">

                                <div class="detail-label">
                                    Specialization
                                </div>

                                <div class="detail-value">

                                    <%= escapeHtml(
                                            display(
                                                    appointment
                                                            .getDentistSpecialization()
                                            )
                                    ) %>

                                </div>

                            </div>


                            <div class="detail-item">

                                <div class="detail-label">
                                    Treatment
                                </div>

                                <div class="detail-value">

                                    <%= escapeHtml(
                                            display(
                                                    appointment
                                                            .getTreatmentName()
                                            )
                                    ) %>

                                    <div class="detail-sub">

                                        <%= escapeHtml(
                                                display(
                                                        appointment
                                                                .getTreatmentCode()
                                                )
                                        ) %>

                                    </div>

                                </div>

                            </div>


                            <div class="detail-item">

                                <div class="detail-label">
                                    Duration
                                </div>

                                <div class="detail-value">

                                    <% if (
                                            appointment
                                                    .getEstimatedDurationMinutes()
                                            != null) { %>

                                        <%= appointment
                                                .getEstimatedDurationMinutes() %>
                                        minutes

                                    <% } else { %>

                                        —

                                    <% } %>

                                </div>

                            </div>

                        </div>

                    </div>

                </section>


                <!-- SCHEDULE -->

                <section class="panel">

                    <div class="panel-header">

                        <h2>
                            Appointment Schedule
                        </h2>

                        <p>
                            Scheduled start and calculated
                            treatment end time.
                        </p>

                    </div>


                    <div class="panel-body">

                        <div class="detail-grid">


                            <div class="detail-item">

                                <div class="detail-label">
                                    Appointment Date
                                </div>

                                <div class="detail-value">

                                    <%= escapeHtml(
                                            display(
                                                    appointment
                                                            .getAppointmentDate()
                                            )
                                    ) %>

                                </div>

                            </div>


                            <div class="detail-item">

                                <div class="detail-label">
                                    Start Time
                                </div>

                                <div class="detail-value">

                                    <%= escapeHtml(
                                            display(
                                                    appointment
                                                            .getAppointmentTime()
                                            )
                                    ) %>

                                </div>

                            </div>


                            <div class="detail-item">

                                <div class="detail-label">
                                    Estimated End Time
                                </div>

                                <div class="detail-value">

                                    <%= escapeHtml(
                                            display(
                                                    appointment
                                                            .getEstimatedEndTime()
                                            )
                                    ) %>

                                </div>

                            </div>


                            <div class="detail-item">

                                <div class="detail-label">
                                    Current Status
                                </div>

                                <div class="detail-value">

                                    <%= escapeHtml(
                                            display(
                                                    appointment
                                                            .getStatus()
                                            )
                                    ) %>

                                </div>

                            </div>

                        </div>

                    </div>

                </section>


                <!-- NOTES -->

                <section class="panel">

                    <div class="panel-header">

                        <h2>
                            Appointment Notes
                        </h2>

                        <p>
                            Booking or clinical scheduling
                            information recorded for this visit.
                        </p>

                    </div>


                    <div class="panel-body">

                        <div class="notes-box"><%= escapeHtml(
                                display(
                                        appointment.getNotes()
                                )
                        ) %></div>

                    </div>

                </section>


                <!-- HISTORY -->

           <!-- =================================================
     APPOINTMENT HISTORY
     ================================================= -->

<section class="panel">

    <div class="panel-header">

        <h2>
            Appointment History
        </h2>

        <p>
            Previous appointment states preserved
            through the Memento design pattern.
        </p>

    </div>


    <div class="panel-body">


        <% if (appointmentHistory.isEmpty()) { %>

            <div class="history-box">

                <strong>
                    No previous states recorded.
                </strong>

                <br><br>

                This appointment has not yet been
                rescheduled, updated or cancelled.

                Once a managed change occurs, the
                previous appointment state will be
                preserved here automatically.

            </div>


        <% } else { %>


            <div style="
                    margin-bottom: 16px;
                    padding: 13px 15px;
                    background: #eef7fa;
                    border: 1px solid #cce2e9;
                    border-radius: 8px;
                    color: #456878;
                    font-size: 12px;
                    line-height: 1.5;">

                <strong>
                    Audit trail:
                </strong>

                <%= appointmentHistory.size() %>

                previous appointment
                <%= appointmentHistory.size() == 1
                        ? "state"
                        : "states" %>
                preserved.

                The newest historical record is shown first.

            </div>


            <%
                for (AppointmentHistoryViewModel history
                        : appointmentHistory) {

                    String changedAtDisplay =
                            history.getChangedAt();


                    if (changedAtDisplay != null) {

                        changedAtDisplay =
                                changedAtDisplay
                                        .replace(
                                                "T",
                                                " "
                                        );


                        if (changedAtDisplay.length()
                                > 19) {

                            changedAtDisplay =
                                    changedAtDisplay
                                            .substring(
                                                    0,
                                                    19
                                            );
                        }
                    }


                    boolean historyCancelled =
                            history.isCancellation();


                    String historyAccent =
                            historyCancelled
                                    ? "#bd4545"
                                    : "#176b87";


                    String historyBadgeBackground =
                            historyCancelled
                                    ? "#fde4e4"
                                    : "#e4f2f6";


                    String historyBadgeText =
                            historyCancelled
                                    ? "#a53a3a"
                                    : "#176b87";
            %>


            <div style="
                    margin-bottom: 16px;
                    border: 1px solid #dfe7ed;
                    border-left: 4px solid <%= historyAccent %>;
                    border-radius: 8px;
                    background: #ffffff;
                    overflow: hidden;">


                <!-- HISTORY HEADER -->

                <div style="
                        padding: 14px 16px;
                        background: #f8fafc;
                        border-bottom: 1px solid #e5ebf0;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        gap: 15px;
                        flex-wrap: wrap;">


                    <div>

                        <div style="
                                font-size: 11px;
                                color: #8493a2;
                                text-transform: uppercase;
                                letter-spacing: 0.4px;
                                margin-bottom: 4px;">

                            Previous State
                            #<%= history.getHistoryId() %>

                        </div>


                        <div style="
                                font-size: 14px;
                                color: #2d4256;
                                font-weight: 700;">

                            <%= escapeHtml(
                                    history
                                            .getDisplayChangeType()
                            ) %>

                        </div>

                    </div>


                    <span style="
                            display: inline-block;
                            padding: 6px 10px;
                            border-radius: 20px;
                            background: <%= historyBadgeBackground %>;
                            color: <%= historyBadgeText %>;
                            font-size: 10px;
                            font-weight: 700;
                            letter-spacing: 0.4px;">

                        <%= escapeHtml(
                                history.getChangeType()
                        ) %>

                    </span>

                </div>


                <!-- HISTORY DETAILS -->

                <div style="
                        padding: 16px;
                        display: grid;
                        grid-template-columns:
                                repeat(2, minmax(0, 1fr));
                        gap: 17px;">


                    <!-- DATE -->

                    <div>

                        <div class="detail-label">
                            Previous Date
                        </div>

                        <div class="detail-value">

                            <%= escapeHtml(
                                    display(
                                            history
                                                    .getAppointmentDate()
                                    )
                            ) %>

                        </div>

                    </div>


                    <!-- TIME -->

                    <div>

                        <div class="detail-label">
                            Previous Time
                        </div>

                        <div class="detail-value">

                            <%= escapeHtml(
                                    display(
                                            history
                                                    .getAppointmentTime()
                                    )
                            ) %>

                        </div>

                    </div>


                    <!-- DENTIST -->

                    <div>

                        <div class="detail-label">
                            Previous Dentist
                        </div>

                        <div class="detail-value">

                            <%= escapeHtml(
                                    history
                                            .getDisplayDentist()
                            ) %>

                        </div>

                    </div>


                    <!-- TREATMENT -->

                    <div>

                        <div class="detail-label">
                            Previous Treatment
                        </div>

                        <div class="detail-value">

                            <%= escapeHtml(
                                    history
                                            .getDisplayTreatment()
                            ) %>

                        </div>

                    </div>


                    <!-- STATUS -->

                    <div>

                        <div class="detail-label">
                            Previous Status
                        </div>

                        <div class="detail-value">

                            <%= escapeHtml(
                                    display(
                                            history.getStatus()
                                    )
                            ) %>

                        </div>

                    </div>


                    <!-- CHANGED BY -->

                    <div>

                        <div class="detail-label">
                            Changed By
                        </div>

                        <div class="detail-value">

                            <%= escapeHtml(
                                    history
                                            .getDisplayChangedBy()
                            ) %>

                        </div>

                    </div>


                    <!-- CHANGED AT -->

                    <div style="
                            grid-column: span 2;">

                        <div class="detail-label">
                            Change Recorded At
                        </div>

                        <div class="detail-value">

                            <%= escapeHtml(
                                    display(
                                            changedAtDisplay
                                    )
                            ) %>

                        </div>

                    </div>


                    <!-- NOTES -->

                    <div style="
                            grid-column: span 2;">

                        <div class="detail-label">
                            Previous Notes
                        </div>

                        <div style="
                                margin-top: 5px;
                                background: #f7f9fb;
                                border: 1px solid #e1e7ec;
                                border-radius: 6px;
                                padding: 11px;
                                color: #536679;
                                font-size: 12px;
                                line-height: 1.5;">

                            <%= escapeHtml(
                                    display(
                                            history.getNotes()
                                    )
                            ) %>

                        </div>

                    </div>

                </div>

            </div>


            <%
                }
            %>


        <% } %>

    </div>

</section>

            <!-- =============================================
                 RIGHT CONTENT
                 ============================================= -->

            <aside>


                <!-- CHARGES -->

                <section class="panel">

                    <div class="panel-header">

                        <h2>
                            Estimated Charges
                        </h2>

                        <p>
                            Standard treatment and consultation
                            charges for this appointment.
                        </p>

                    </div>


                    <div class="panel-body">

                        <div class="charge-row">

                            <span class="charge-label">
                                Treatment
                            </span>

                            <span class="charge-value">

                                LKR
                                <%= money(
                                        appointment
                                                .getTreatmentPrice()
                                ) %>

                            </span>

                        </div>


                        <div class="charge-row">

                            <span class="charge-label">
                                Consultation
                            </span>

                            <span class="charge-value">

                                LKR
                                <%= money(
                                        appointment
                                                .getConsultationFee()
                                ) %>

                            </span>

                        </div>


                        <div class="total-charge">

                            <div class="total-charge-label">
                                Estimated Total
                            </div>


                            <div class="total-charge-value">

                                LKR
                                <%= money(
                                        appointment
                                                .getEstimatedTotalCost()
                                ) %>

                            </div>

                        </div>

                    </div>

                </section>


                <!-- ACTIONS -->

                <section class="panel action-panel">

                    <div class="panel-header">

                        <h2>
                            Manage Appointment
                        </h2>

                        <p>
                            Available actions depend on the
                            appointment's current status.
                        </p>

                    </div>


                    <div class="panel-body">


                        <% if (scheduled) { %>

                            <div class="action-list">


                                <!-- RESCHEDULE -->

                                <a class="btn btn-primary"
                                   href="<%= contextPath %>/appointments/reschedule?id=<%= appointment.getAppointmentId() %>">

                                    Reschedule Appointment

                                </a>


                                <div class="action-description">

                                    Change the dentist, treatment,
                                    date or time. Conflict validation
                                    will run again before saving.

                                </div>


                                <!-- COMPLETE -->

                                <a class="btn btn-success"
                                   href="<%= contextPath %>/appointments/status?id=<%= appointment.getAppointmentId() %>&action=complete">

                                    Mark as Completed

                                </a>


                                <div class="action-description">

                                    Mark the patient's visit as
                                    completed after treatment.

                                </div>


                                <!-- CANCEL -->

                                <a class="btn btn-danger"
                                   href="<%= contextPath %>/appointments/status?id=<%= appointment.getAppointmentId() %>&action=cancel">

                                    Cancel Appointment

                                </a>


                                <div class="action-description">

                                    Cancellation preserves the
                                    appointment and its history;
                                    no physical deletion occurs.

                                </div>


                                <!-- BILLING -->

                                <a class="btn btn-outline"
                                   href="<%= contextPath %>/bills/create?appointmentId=<%= appointment.getAppointmentId() %>">

                                    Create Bill

                                </a>

                            </div>


                        <% } else { %>


                            <div class="final-status-box">

                                <% if (completed) { %>

                                    This appointment is
                                    <strong>completed</strong>.

                                    Its scheduling state is now
                                    final and cannot be returned
                                    to another appointment status.

                                <% } else if (cancelled) { %>

                                    This appointment is
                                    <strong>cancelled</strong>.

                                    It remains preserved as part
                                    of the clinic's historical
                                    appointment record.

                                <% } else { %>

                                    No management actions are
                                    currently available for this
                                    appointment.

                                <% } %>

                            </div>


                            <% if (completed) { %>

                                <div style="margin-top: 12px;">

                                    <a class="btn btn-outline"
                                       style="width: 100%; text-align: center;"
                                       href="<%= contextPath %>/bills/create?appointmentId=<%= appointment.getAppointmentId() %>">

                                        Create / View Bill

                                    </a>

                                </div>

                            <% } %>


                        <% } %>

                    </div>

                </section>

            </aside>

        </div>

        <% } %>

    </main>

</div>

</body>

</html>