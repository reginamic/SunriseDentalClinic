<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="com.sunrisedental.web.model.AppointmentViewModel"%>

<%!
    /*
     * Basic HTML escaping for values received from the API.
     */
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

    private String displayValue(
            String value,
            String fallback) {

        if (value == null
                || value.trim().isEmpty()) {

            return fallback;
        }

        return value;
    }
%>

<%
    String contextPath =
            request.getContextPath();

    List<AppointmentViewModel> appointments =
            (List<AppointmentViewModel>)
                    request.getAttribute("appointments");

    if (appointments == null) {
        appointments = Collections.emptyList();
    }

    long totalAppointments =
            request.getAttribute("totalAppointments") == null
                    ? 0
                    : ((Number)
                    request.getAttribute(
                            "totalAppointments"
                    )).longValue();

    long scheduledAppointments =
            request.getAttribute("scheduledAppointments") == null
                    ? 0
                    : ((Number)
                    request.getAttribute(
                            "scheduledAppointments"
                    )).longValue();

    long completedAppointments =
            request.getAttribute("completedAppointments") == null
                    ? 0
                    : ((Number)
                    request.getAttribute(
                            "completedAppointments"
                    )).longValue();

    long cancelledAppointments =
            request.getAttribute("cancelledAppointments") == null
                    ? 0
                    : ((Number)
                    request.getAttribute(
                            "cancelledAppointments"
                    )).longValue();

    String errorMessage =
            (String)
                    request.getAttribute(
                            "errorMessage"
                    );

    String successMessage =
            (String)
                    request.getAttribute(
                            "successMessage"
                    );

    /*
     * Flash messages may later be placed in session
     * after successful create/update/cancel operations.
     */
    if (successMessage == null) {

        successMessage =
                (String)
                        session.getAttribute(
                                "successMessage"
                        );

        if (successMessage != null) {

            session.removeAttribute(
                    "successMessage"
            );
        }
    }

    if (errorMessage == null) {

        errorMessage =
                (String)
                        session.getAttribute(
                                "errorMessage"
                        );

        if (errorMessage != null) {

            session.removeAttribute(
                    "errorMessage"
            );
        }
    }

    String selectedAppointmentNumber =
            (String)
                    request.getAttribute(
                            "selectedAppointmentNumber"
                    );

    String selectedAppointmentDate =
            (String)
                    request.getAttribute(
                            "selectedAppointmentDate"
                    );

    String selectedPatientId =
            (String)
                    request.getAttribute(
                            "selectedPatientId"
                    );

    String selectedStatus =
            (String)
                    request.getAttribute(
                            "selectedStatus"
                    );

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
%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Appointment Management | Sunrise Dental
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
            color: #253348;
            min-height: 100vh;
        }

        a {
            text-decoration: none;
        }

        button,
        input,
        select {
            font: inherit;
        }

        /* =====================================================
           APPLICATION LAYOUT
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
            line-height: 1.2;
            margin-bottom: 5px;
        }

        .brand span {
            color: #b6c8d8;
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

        .sidebar-footer .user-name {
            font-size: 14px;
            font-weight: 600;
        }

        .sidebar-footer .user-role {
            font-size: 11px;
            color: #a9bdcc;
            margin-top: 4px;
        }

        .logout-link {
            display: inline-block;
            margin-top: 12px;
            color: #ffffff;
            font-size: 13px;
        }

        /* =====================================================
           MAIN CONTENT
           ===================================================== */

        .main-content {
            margin-left: 255px;
            width: calc(100% - 255px);
            padding: 30px;
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 20px;
            margin-bottom: 24px;
        }

        .page-header h1 {
            font-size: 27px;
            color: #1e3044;
            margin-bottom: 5px;
        }

        .page-header p {
            color: #718096;
            font-size: 14px;
        }

        .btn {
            border: none;
            border-radius: 7px;
            padding: 10px 16px;
            cursor: pointer;
            display: inline-block;
            font-size: 13px;
            font-weight: 600;
        }

        .btn-primary {
            background: #176b87;
            color: white;
        }

        .btn-primary:hover {
            background: #115972;
        }

        .btn-secondary {
            background: #e7edf3;
            color: #34485c;
        }

        .btn-secondary:hover {
            background: #d9e2eb;
        }

        .btn-outline {
            background: white;
            color: #176b87;
            border: 1px solid #b7cbd4;
        }

        .btn-outline:hover {
            background: #f2f8fa;
        }

        /* =====================================================
           MESSAGE BOXES
           ===================================================== */

        .alert {
            padding: 13px 16px;
            border-radius: 7px;
            margin-bottom: 20px;
            font-size: 13px;
        }

        .alert-success {
            background: #e8f7ef;
            color: #17623c;
            border: 1px solid #b8e3ca;
        }

        .alert-error {
            background: #fff0f0;
            color: #9d2c2c;
            border: 1px solid #efc0c0;
        }

        /* =====================================================
           STATISTICS
           ===================================================== */

        .stats-grid {
            display: grid;
            grid-template-columns:
                    repeat(4, minmax(160px, 1fr));
            gap: 16px;
            margin-bottom: 23px;
        }

        .stat-card {
            background: white;
            border: 1px solid #e3e9ef;
            border-radius: 10px;
            padding: 19px;
            box-shadow:
                    0 2px 7px rgba(29,48,68,0.04);
        }

        .stat-label {
            color: #708096;
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .stat-number {
            margin-top: 8px;
            font-size: 29px;
            font-weight: 700;
            color: #20364b;
        }

        .stat-card.total {
            border-top: 3px solid #176b87;
        }

        .stat-card.scheduled {
            border-top: 3px solid #d59b23;
        }

        .stat-card.completed {
            border-top: 3px solid #27905d;
        }

        .stat-card.cancelled {
            border-top: 3px solid #c84f4f;
        }

        /* =====================================================
           SEARCH PANEL
           ===================================================== */

        .panel {
            background: white;
            border: 1px solid #e1e8ef;
            border-radius: 10px;
            box-shadow:
                    0 2px 7px rgba(29,48,68,0.04);
        }

        .search-panel {
            padding: 19px;
            margin-bottom: 23px;
        }

        .panel-heading {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 16px;
        }

        .panel-heading h2 {
            font-size: 17px;
            color: #263a4e;
        }

        .filter-grid {
            display: grid;
            grid-template-columns:
                    1.3fr
                    1fr
                    1fr
                    1fr
                    auto;
            gap: 12px;
            align-items: end;
        }

        .form-group label {
            display: block;
            font-size: 12px;
            color: #5f7082;
            margin-bottom: 6px;
            font-weight: 600;
        }

        .form-control {
            width: 100%;
            border: 1px solid #ccd6df;
            border-radius: 6px;
            padding: 9px 10px;
            background: white;
            color: #2a3948;
            outline: none;
        }

        .form-control:focus {
            border-color: #408ba4;
            box-shadow:
                    0 0 0 2px rgba(64,139,164,0.10);
        }

        .filter-actions {
            display: flex;
            gap: 8px;
        }

        /* =====================================================
           APPOINTMENT TABLE
           ===================================================== */

        .table-panel {
            overflow: hidden;
        }

        .table-header {
            padding: 18px 20px;
            border-bottom: 1px solid #e5ebf0;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .table-header h2 {
            font-size: 17px;
        }

        .record-count {
            color: #76879a;
            font-size: 12px;
        }

        .table-wrapper {
            width: 100%;
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 950px;
        }

        th {
            text-align: left;
            background: #f8fafc;
            color: #66788a;
            font-size: 11px;
            text-transform: uppercase;
            letter-spacing: 0.4px;
            padding: 12px 15px;
            border-bottom: 1px solid #e0e7ed;
        }

        td {
            padding: 14px 15px;
            border-bottom: 1px solid #edf1f5;
            font-size: 13px;
            vertical-align: middle;
        }

        tbody tr:hover {
            background: #fafcfd;
        }

        .appointment-number {
            color: #176b87;
            font-weight: 700;
        }

        .primary-text {
            font-weight: 600;
            color: #283b4e;
        }

        .secondary-text {
            color: #8392a2;
            font-size: 11px;
            margin-top: 3px;
        }

        /* =====================================================
           STATUS BADGES
           ===================================================== */

        .status-badge {
            display: inline-block;
            padding: 5px 9px;
            border-radius: 20px;
            font-size: 10px;
            font-weight: 700;
            letter-spacing: 0.3px;
        }

        .status-scheduled {
            background: #fff4d9;
            color: #8a6110;
        }

        .status-completed {
            background: #dff5e9;
            color: #1f6c46;
        }

        .status-cancelled {
            background: #fde5e5;
            color: #a63838;
        }

        .status-default {
            background: #e8edf2;
            color: #566675;
        }

        /* =====================================================
           EMPTY STATE
           ===================================================== */

        .empty-state {
            text-align: center;
            padding: 48px 20px;
            color: #778799;
        }

        .empty-state h3 {
            color: #405264;
            margin-bottom: 8px;
        }

        .empty-state p {
            font-size: 13px;
            margin-bottom: 17px;
        }

        /* =====================================================
           RESPONSIVE
           ===================================================== */

        @media (max-width: 1100px) {

            .stats-grid {
                grid-template-columns:
                        repeat(2, 1fr);
            }

            .filter-grid {
                grid-template-columns:
                        repeat(2, 1fr);
            }

            .filter-actions {
                grid-column: span 2;
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
                align-items: flex-start;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .filter-grid {
                grid-template-columns: 1fr;
            }

            .filter-actions {
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
                        displayValue(
                                loggedInName,
                                "Signed-in User"
                        )
                ) %>

            </div>

            <div class="user-role">

                <%= escapeHtml(
                        displayValue(
                                loggedInRole,
                                ""
                        )
                ) %>

            </div>

            <a class="logout-link"
               href="<%= contextPath %>/logout">
                Sign Out
            </a>

        </div>

    </aside>


    <!-- =====================================================
         MAIN CONTENT
         ===================================================== -->

    <main class="main-content">

        <!-- PAGE HEADER -->

        <div class="page-header">

            <div>

                <h1>
                    Appointment Management
                </h1>

                <p>
                    Schedule, search and manage
                    Sunrise Dental patient appointments.
                </p>

            </div>

            <!-- Route implemented in Register Appointment step -->

            <a class="btn btn-primary"
               href="<%= contextPath %>/appointments/register">
                + Register Appointment
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
             STATISTICS
             ================================================= -->

        <section class="stats-grid">

            <div class="stat-card total">

                <div class="stat-label">
                    Total Appointments
                </div>

                <div class="stat-number">
                    <%= totalAppointments %>
                </div>

            </div>


            <div class="stat-card scheduled">

                <div class="stat-label">
                    Scheduled
                </div>

                <div class="stat-number">
                    <%= scheduledAppointments %>
                </div>

            </div>


            <div class="stat-card completed">

                <div class="stat-label">
                    Completed
                </div>

                <div class="stat-number">
                    <%= completedAppointments %>
                </div>

            </div>


            <div class="stat-card cancelled">

                <div class="stat-label">
                    Cancelled
                </div>

                <div class="stat-number">
                    <%= cancelledAppointments %>
                </div>

            </div>

        </section>


        <!-- =================================================
             SEARCH / FILTER PANEL
             ================================================= -->

        <section class="panel search-panel">

            <div class="panel-heading">

                <h2>
                    Search &amp; Filter
                </h2>

            </div>


            <form method="GET"
                  action="<%= contextPath %>/appointments">

                <div class="filter-grid">


                    <!-- Appointment Number -->

                    <div class="form-group">

                        <label for="appointmentNumber">
                            Appointment Number
                        </label>

                        <input
                            class="form-control"
                            type="text"
                            id="appointmentNumber"
                            name="appointmentNumber"
                            maxlength="30"
                            placeholder="e.g. APT-0001"
                            value="<%= escapeHtml(
                                    selectedAppointmentNumber
                            ) %>"
                        >

                    </div>


                    <!-- Date -->

                    <div class="form-group">

                        <label for="appointmentDate">
                            Appointment Date
                        </label>

                        <input
                            class="form-control"
                            type="date"
                            id="appointmentDate"
                            name="appointmentDate"
                            value="<%= escapeHtml(
                                    selectedAppointmentDate
                            ) %>"
                        >

                    </div>


                    <!-- Patient -->

                    <div class="form-group">

                        <label for="patientId">
                            Patient ID
                        </label>

                        <input
                            class="form-control"
                            type="number"
                            min="1"
                            id="patientId"
                            name="patientId"
                            placeholder="Patient ID"
                            value="<%= escapeHtml(
                                    selectedPatientId
                            ) %>"
                        >

                    </div>


                    <!-- Status -->

                    <div class="form-group">

                        <label for="status">
                            Status
                        </label>

                        <select
                            class="form-control"
                            id="status"
                            name="status">

                            <option value="">
                                All Statuses
                            </option>

                            <option
                                value="SCHEDULED"
                                <%= "SCHEDULED".equalsIgnoreCase(
                                        selectedStatus
                                )
                                        ? "selected"
                                        : "" %>>
                                Scheduled
                            </option>

                            <option
                                value="COMPLETED"
                                <%= "COMPLETED".equalsIgnoreCase(
                                        selectedStatus
                                )
                                        ? "selected"
                                        : "" %>>
                                Completed
                            </option>

                            <option
                                value="CANCELLED"
                                <%= "CANCELLED".equalsIgnoreCase(
                                        selectedStatus
                                )
                                        ? "selected"
                                        : "" %>>
                                Cancelled
                            </option>

                        </select>

                    </div>


                    <!-- Search buttons -->

                    <div class="filter-actions">

                        <button
                            class="btn btn-primary"
                            type="submit">
                            Search
                        </button>

                        <a class="btn btn-secondary"
                           href="<%= contextPath %>/appointments">
                            Clear
                        </a>

                    </div>

                </div>

            </form>

        </section>


        <!-- =================================================
             APPOINTMENTS TABLE
             ================================================= -->

        <section class="panel table-panel">

            <div class="table-header">

                <h2>
                    Appointments
                </h2>

                <div class="record-count">

                    <%= appointments.size() %>
                    record<%= appointments.size() == 1
                            ? ""
                            : "s" %>

                </div>

            </div>


            <% if (appointments.isEmpty()) { %>

                <div class="empty-state">

                    <h3>
                        No appointments found
                    </h3>

                    <p>
                        Try changing the search criteria
                        or register a new appointment.
                    </p>

                    <a class="btn btn-primary"
                       href="<%= contextPath %>/appointments/register">
                        Register Appointment
                    </a>

                </div>

            <% } else { %>

                <div class="table-wrapper">

                    <table>

                        <thead>

                            <tr>

                                <th>
                                    Appointment
                                </th>

                                <th>
                                    Patient
                                </th>

                                <th>
                                    Dentist
                                </th>

                                <th>
                                    Treatment
                                </th>

                                <th>
                                    Date
                                </th>

                                <th>
                                    Time
                                </th>

                                <th>
                                    Status
                                </th>

                                <th>
                                    Action
                                </th>

                            </tr>

                        </thead>


                        <tbody>

                        <%
                            for (AppointmentViewModel appointment
                                    : appointments) {

                                String status =
                                        appointment.getStatus();

                                String statusClass =
                                        "status-default";

                                if (appointment.isScheduled()) {

                                    statusClass =
                                            "status-scheduled";

                                } else if (
                                        appointment.isCompleted()) {

                                    statusClass =
                                            "status-completed";

                                } else if (
                                        appointment.isCancelled()) {

                                    statusClass =
                                            "status-cancelled";
                                }


                                String patientDisplay =
                                        appointment.getPatientName();

                                if (patientDisplay == null
                                        || patientDisplay.isBlank()) {

                                    patientDisplay =
                                            "Patient #"
                                            + appointment
                                                    .getPatientId();
                                }


                                String dentistDisplay =
                                        appointment.getDentistName();

                                if (dentistDisplay == null
                                        || dentistDisplay.isBlank()) {

                                    dentistDisplay =
                                            "Dentist #"
                                            + appointment
                                                    .getDentistId();
                                }


                                String treatmentDisplay =
                                        appointment.getTreatmentName();

                                if (treatmentDisplay == null
                                        || treatmentDisplay.isBlank()) {

                                    treatmentDisplay =
                                            "Treatment #"
                                            + appointment
                                                    .getTreatmentId();
                                }
                        %>

                            <tr>

                                <!-- Appointment Number -->

                                <td>

                                    <div class="appointment-number">

                                        <%= escapeHtml(
                                                appointment
                                                        .getAppointmentNumber()
                                        ) %>

                                    </div>

                                    <div class="secondary-text">

                                        ID:
                                        <%= appointment
                                                .getAppointmentId() %>

                                    </div>

                                </td>


                                <!-- Patient -->

                                <td>

                                    <div class="primary-text">

                                        <%= escapeHtml(
                                                patientDisplay
                                        ) %>

                                    </div>

                                    <% if (
                                            appointment
                                                    .getPatientCode()
                                            != null) { %>

                                        <div class="secondary-text">

                                            <%= escapeHtml(
                                                    appointment
                                                            .getPatientCode()
                                            ) %>

                                        </div>

                                    <% } %>

                                </td>


                                <!-- Dentist -->

                                <td>

                                    <div class="primary-text">

                                        <%= escapeHtml(
                                                dentistDisplay
                                        ) %>

                                    </div>

                                    <% if (
                                            appointment
                                                    .getDentistSpecialization()
                                            != null) { %>

                                        <div class="secondary-text">

                                            <%= escapeHtml(
                                                    appointment
                                                            .getDentistSpecialization()
                                            ) %>

                                        </div>

                                    <% } %>

                                </td>


                                <!-- Treatment -->

                                <td>

                                    <div class="primary-text">

                                        <%= escapeHtml(
                                                treatmentDisplay
                                        ) %>

                                    </div>

                                    <% if (
                                            appointment
                                                    .getEstimatedDurationMinutes()
                                            != null) { %>

                                        <div class="secondary-text">

                                            <%= appointment
                                                    .getEstimatedDurationMinutes() %>
                                            minutes

                                        </div>

                                    <% } %>

                                </td>


                                <!-- Date -->

                                <td>

                                    <%= escapeHtml(
                                            appointment
                                                    .getAppointmentDate()
                                    ) %>

                                </td>


                                <!-- Time -->

                                <td>

                                    <div>

                                        <%= escapeHtml(
                                                appointment
                                                        .getAppointmentTime()
                                        ) %>

                                    </div>

                                    <% if (
                                            appointment
                                                    .getEstimatedEndTime()
                                            != null) { %>

                                        <div class="secondary-text">

                                            Until
                                            <%= escapeHtml(
                                                    appointment
                                                            .getEstimatedEndTime()
                                            ) %>

                                        </div>

                                    <% } %>

                                </td>


                                <!-- Status -->

                                <td>

                                    <span class="status-badge
                                            <%= statusClass %>">

                                        <%= escapeHtml(
                                                displayValue(
                                                        status,
                                                        "UNKNOWN"
                                                )
                                        ) %>

                                    </span>

                                </td>


                                <!-- Action -->

                                <td>

                                    <a
                                        class="btn btn-outline"
                                        href="<%= contextPath %>/appointments/details?id=<%= appointment.getAppointmentId() %>">

                                        View / Manage

                                    </a>

                                </td>

                            </tr>

                        <%
                            }
                        %>

                        </tbody>

                    </table>

                </div>

            <% } %>

        </section>

    </main>

</div>

</body>

</html>