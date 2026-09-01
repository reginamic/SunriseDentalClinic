<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="java.math.BigDecimal"%>

<%@page import="com.sunrisedental.web.model.AppointmentViewModel"%>
<%@page import="com.sunrisedental.web.model.DentistViewModel"%>
<%@page import="com.sunrisedental.web.model.TreatmentViewModel"%>


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


    private String selected(
            String currentValue,
            int id) {

        if (currentValue == null) {
            return "";
        }

        return currentValue.equals(
                String.valueOf(id)
        )
                ? "selected"
                : "";
    }


    private String money(
            BigDecimal value) {

        if (value == null) {
            return "0.00";
        }

        return value
                .setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                )
                .toPlainString();
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


    List<DentistViewModel> dentists =
            (List<DentistViewModel>)
                    request.getAttribute(
                            "dentists"
                    );


    List<TreatmentViewModel> treatments =
            (List<TreatmentViewModel>)
                    request.getAttribute(
                            "treatments"
                    );


    if (dentists == null) {
        dentists = Collections.emptyList();
    }


    if (treatments == null) {
        treatments = Collections.emptyList();
    }


    String errorMessage =
            (String)
                    request.getAttribute(
                            "errorMessage"
                    );


    String formDentistId =
            (String)
                    request.getAttribute(
                            "formDentistId"
                    );


    String formTreatmentId =
            (String)
                    request.getAttribute(
                            "formTreatmentId"
                    );


    String formAppointmentDate =
            (String)
                    request.getAttribute(
                            "formAppointmentDate"
                    );


    String formAppointmentTime =
            (String)
                    request.getAttribute(
                            "formAppointmentTime"
                    );


    String formNotes =
            (String)
                    request.getAttribute(
                            "formNotes"
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
        Reschedule Appointment | Sunrise Dental
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


        button,
        input,
        select,
        textarea {
            font: inherit;
        }


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
            align-items: center;
            gap: 20px;
            margin-bottom: 22px;
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


        /* =====================================================
           ALERT
           ===================================================== */

        .alert {
            margin-bottom: 20px;
            padding: 13px 16px;
            border-radius: 8px;
            font-size: 13px;
        }


        .alert-error {
            background: #fff0f0;
            color: #982f2f;
            border: 1px solid #efc1c1;
        }


        /* =====================================================
           PANELS
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

            overflow: hidden;
        }


        .panel-header {
            padding: 20px 22px;

            border-bottom:
                    1px solid #e6ebf0;
        }


        .panel-header h2 {
            font-size: 18px;
            margin-bottom: 5px;
        }


        .panel-header p {
            color: #7d8c9a;
            font-size: 12px;
        }


        .panel-body {
            padding: 22px;
        }


        /* =====================================================
           CURRENT APPOINTMENT
           ===================================================== */

        .appointment-summary {
            background: #f8fafc;

            border:
                    1px solid #e0e7ed;

            border-radius: 8px;

            padding: 16px;

            margin-bottom: 23px;
        }


        .summary-number {
            color: #176b87;
            font-size: 20px;
            font-weight: 700;
        }


        .summary-sub {
            color: #788899;
            font-size: 12px;
            margin-top: 4px;
        }


        /* =====================================================
           FORM
           ===================================================== */

        .form-section {
            margin-bottom: 26px;
        }


        .form-section:last-child {
            margin-bottom: 0;
        }


        .section-heading {
            margin-bottom: 14px;
        }


        .section-heading h3 {
            color: #304559;
            font-size: 15px;
            margin-bottom: 3px;
        }


        .section-heading p {
            color: #8493a2;
            font-size: 12px;
        }


        .form-grid {
            display: grid;
            grid-template-columns:
                    repeat(2, minmax(0, 1fr));
            gap: 17px;
        }


        .form-group.full {
            grid-column: span 2;
        }


        .form-label {
            display: block;
            color: #536679;
            font-size: 12px;
            font-weight: 600;
            margin-bottom: 6px;
        }


        .required {
            color: #be4141;
        }


        .form-control {
            width: 100%;
            padding: 10px 11px;

            border:
                    1px solid #cbd6df;

            border-radius: 7px;
            outline: none;

            background: white;
            color: #293b4d;
        }


        .form-control:focus {
            border-color: #3c88a2;

            box-shadow:
                    0 0 0 2px
                    rgba(60,136,162,0.10);
        }


        textarea.form-control {
            min-height: 110px;
            resize: vertical;
            line-height: 1.5;
        }


        .helper-text {
            margin-top: 5px;
            color: #8795a4;
            font-size: 11px;
        }


        .readonly-box {
            background: #f3f6f8;

            border:
                    1px solid #d9e1e7;

            border-radius: 7px;

            padding: 11px;

            color: #394d60;

            font-size: 13px;
        }


        .readonly-box strong {
            color: #263a4e;
        }


        .form-actions {
            border-top:
                    1px solid #e7ecf1;

            padding-top: 20px;

            display: flex;
            justify-content: flex-end;
            gap: 9px;
        }


        /* =====================================================
           RIGHT SUMMARY
           ===================================================== */

        .side-panel {
            position: sticky;
            top: 30px;
        }


        .summary-row {
            padding: 12px 0;

            border-bottom:
                    1px solid #edf1f4;
        }


        .summary-row:last-child {
            border-bottom: none;
        }


        .summary-label {
            color: #7e8e9e;
            text-transform: uppercase;
            font-size: 10px;
            letter-spacing: 0.4px;
            margin-bottom: 5px;
        }


        .summary-value {
            color: #2d4256;
            font-size: 14px;
            font-weight: 600;
        }


        .summary-muted {
            color: #8796a5;
            font-size: 12px;
            margin-top: 3px;
        }


        .warning-box {
            margin-top: 18px;

            padding: 14px;

            background: #fff8e5;

            border:
                    1px solid #eadbaa;

            border-radius: 8px;

            color: #746128;

            font-size: 12px;

            line-height: 1.55;
        }


        .memento-box {
            margin-top: 14px;

            padding: 14px;

            background: #eef7fa;

            border:
                    1px solid #cce2e9;

            border-radius: 8px;

            color: #456878;

            font-size: 12px;

            line-height: 1.55;
        }


        /* =====================================================
           ERROR STATE
           ===================================================== */

        .error-state {
            background: white;

            border:
                    1px solid #e1e8ef;

            border-radius: 10px;

            padding: 35px;

            max-width: 700px;

            text-align: center;
        }


        .error-state h2 {
            margin-bottom: 10px;
        }


        .error-state p {
            color: #758596;
            margin-bottom: 18px;
        }


        /* =====================================================
           RESPONSIVE
           ===================================================== */

        @media (max-width: 1000px) {

            .content-grid {
                grid-template-columns: 1fr;
            }


            .side-panel {
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
                align-items: flex-start;
            }


            .form-grid {
                grid-template-columns: 1fr;
            }


            .form-group.full {
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
                    Reschedule Appointment
                </h1>

                <p>
                    Change the dentist, treatment,
                    date or time of a scheduled appointment.
                </p>

            </div>


            <% if (appointment != null) { %>

                <a class="btn btn-secondary"
                   href="<%= contextPath %>/appointments/details?id=<%= appointment.getAppointmentId() %>">

                    ← Back to Appointment

                </a>

            <% } else { %>

                <a class="btn btn-secondary"
                   href="<%= contextPath %>/appointments">

                    ← Back to Appointments

                </a>

            <% } %>

        </div>


        <% if (errorMessage != null
                && !errorMessage.isBlank()) { %>

            <div class="alert alert-error">

                <strong>
                    Appointment could not be rescheduled.
                </strong>

                <br>

                <%= escapeHtml(errorMessage) %>

            </div>

        <% } %>


        <% if (appointment == null) { %>

            <div class="error-state">

                <h2>
                    Appointment unavailable
                </h2>

                <p>
                    The requested appointment could not
                    be loaded for rescheduling.
                </p>

                <a class="btn btn-primary"
                   href="<%= contextPath %>/appointments">

                    Return to Appointments

                </a>

            </div>

        <% } else { %>


        <div class="content-grid">


            <!-- =============================================
                 LEFT FORM
                 ============================================= -->

            <section class="panel">

                <div class="panel-header">

                    <h2>
                        Update Schedule
                    </h2>

                    <p>
                        The patient remains linked to the
                        original appointment.
                    </p>

                </div>


                <div class="panel-body">


                    <div class="appointment-summary">

                        <div class="summary-number">

                            <%= escapeHtml(
                                    appointment
                                            .getAppointmentNumber()
                            ) %>

                        </div>


                        <div class="summary-sub">

                            Current schedule:

                            <%= escapeHtml(
                                    appointment
                                            .getAppointmentDate()
                            ) %>

                            at

                            <%= escapeHtml(
                                    appointment
                                            .getAppointmentTime()
                            ) %>

                        </div>

                    </div>


                    <form method="POST"
                          action="<%= contextPath %>/appointments/reschedule">


                        <input
                            type="hidden"
                            name="appointmentId"
                            value="<%= appointment.getAppointmentId() %>"
                        >


                        <!-- =================================
                             PATIENT
                             ================================= -->

                        <div class="form-section">

                            <div class="section-heading">

                                <h3>
                                    1. Patient
                                </h3>

                                <p>
                                    Patient identity is preserved.
                                </p>

                            </div>


                            <div class="readonly-box">

                                <strong>

                                    <%= escapeHtml(
                                            appointment
                                                    .getPatientCode()
                                    ) %>

                                    —

                                    <%= escapeHtml(
                                            appointment
                                                    .getPatientName()
                                    ) %>

                                </strong>

                                <br>

                                <%= escapeHtml(
                                        appointment
                                                .getPatientContactNumber()
                                ) %>

                            </div>

                        </div>


                        <!-- =================================
                             DENTIST / TREATMENT
                             ================================= -->

                        <div class="form-section">

                            <div class="section-heading">

                                <h3>
                                    2. Dentist &amp; Treatment
                                </h3>

                                <p>
                                    Only active dentists and active
                                    treatments can be selected.
                                </p>

                            </div>


                            <div class="form-grid">


                                <div class="form-group">

                                    <label class="form-label"
                                           for="dentistId">

                                        Dentist
                                        <span class="required">*</span>

                                    </label>


                                    <select
                                        class="form-control"
                                        id="dentistId"
                                        name="dentistId"
                                        required>

                                        <option value="">
                                            -- Select Dentist --
                                        </option>


                                        <%
                                            for (DentistViewModel dentist
                                                    : dentists) {
                                        %>

                                            <option
                                                value="<%= dentist.getDentistId() %>"

                                                data-name="<%= escapeHtml(
                                                        dentist.getFullName()
                                                ) %>"

                                                data-specialization="<%= escapeHtml(
                                                        dentist.getSpecialization()
                                                ) %>"

                                                <%= selected(
                                                        formDentistId,
                                                        dentist.getDentistId()
                                                ) %>>

                                                <%= escapeHtml(
                                                        dentist.getDentistCode()
                                                ) %>

                                                —

                                                <%= escapeHtml(
                                                        dentist.getFullName()
                                                ) %>

                                                <% if (
                                                        dentist.getSpecialization()
                                                        != null
                                                        && !dentist
                                                                .getSpecialization()
                                                                .isBlank()) { %>

                                                    (
                                                    <%= escapeHtml(
                                                            dentist
                                                                    .getSpecialization()
                                                    ) %>
                                                    )

                                                <% } %>

                                            </option>

                                        <%
                                            }
                                        %>

                                    </select>

                                </div>


                                <div class="form-group">

                                    <label class="form-label"
                                           for="treatmentId">

                                        Treatment
                                        <span class="required">*</span>

                                    </label>


                                    <select
                                        class="form-control"
                                        id="treatmentId"
                                        name="treatmentId"
                                        required>

                                        <option value="">
                                            -- Select Treatment --
                                        </option>


                                        <%
                                            for (TreatmentViewModel treatment
                                                    : treatments) {
                                        %>

                                            <option
                                                value="<%= treatment.getTreatmentId() %>"

                                                data-name="<%= escapeHtml(
                                                        treatment
                                                                .getTreatmentName()
                                                ) %>"

                                                data-duration="<%= treatment
                                                        .getEstimatedDurationMinutes() %>"

                                                data-charge="<%= money(
                                                        treatment
                                                                .getStandardCharge()
                                                ) %>"

                                                <%= selected(
                                                        formTreatmentId,
                                                        treatment
                                                                .getTreatmentId()
                                                ) %>>

                                                <%= escapeHtml(
                                                        treatment
                                                                .getTreatmentCode()
                                                ) %>

                                                —

                                                <%= escapeHtml(
                                                        treatment
                                                                .getTreatmentName()
                                                ) %>

                                                —

                                                <%= escapeHtml(
                                                        treatment
                                                                .getDurationText()
                                                ) %>

                                            </option>

                                        <%
                                            }
                                        %>

                                    </select>

                                </div>

                            </div>

                        </div>


                        <!-- =================================
                             DATE / TIME
                             ================================= -->

                        <div class="form-section">

                            <div class="section-heading">

                                <h3>
                                    3. New Schedule
                                </h3>

                                <p>
                                    Duration-aware dentist conflict
                                    checking runs before saving.
                                </p>

                            </div>


                            <div class="form-grid">


                                <div class="form-group">

                                    <label class="form-label"
                                           for="appointmentDate">

                                        Appointment Date
                                        <span class="required">*</span>

                                    </label>


                                    <input
                                        class="form-control"
                                        type="date"
                                        id="appointmentDate"
                                        name="appointmentDate"
                                        value="<%= escapeHtml(
                                                formAppointmentDate
                                        ) %>"
                                        required
                                    >

                                </div>


                                <div class="form-group">

                                    <label class="form-label"
                                           for="appointmentTime">

                                        Start Time
                                        <span class="required">*</span>

                                    </label>


                                    <input
                                        class="form-control"
                                        type="time"
                                        id="appointmentTime"
                                        name="appointmentTime"
                                        value="<%= escapeHtml(
                                                formAppointmentTime
                                        ) %>"
                                        required
                                    >

                                </div>

                            </div>

                        </div>


                        <!-- =================================
                             NOTES
                             ================================= -->

                        <div class="form-section">

                            <div class="section-heading">

                                <h3>
                                    4. Notes
                                </h3>

                                <p>
                                    Update the appointment note if required.
                                </p>

                            </div>


                            <div class="form-grid">

                                <div class="form-group full">

                                    <textarea
                                        class="form-control"
                                        id="notes"
                                        name="notes"
                                        maxlength="1000"
                                        placeholder="Reason or notes for rescheduling."><%= escapeHtml(
                                                formNotes
                                        ) %></textarea>


                                    <div class="helper-text">

                                        Maximum 1000 characters.

                                    </div>

                                </div>

                            </div>

                        </div>


                        <!-- =================================
                             ACTIONS
                             ================================= -->

                        <div class="form-actions">

                            <a class="btn btn-secondary"
                               href="<%= contextPath %>/appointments/details?id=<%= appointment.getAppointmentId() %>">

                                Cancel

                            </a>


                            <button
                                class="btn btn-primary"
                                type="submit"
                                <%= dentists.isEmpty()
                                        || treatments.isEmpty()
                                        ? "disabled"
                                        : "" %>>

                                Save Reschedule

                            </button>

                        </div>

                    </form>

                </div>

            </section>


            <!-- =============================================
                 RIGHT SUMMARY
                 ============================================= -->

            <aside class="panel side-panel">

                <div class="panel-header">

                    <h2>
                        Reschedule Summary
                    </h2>

                    <p>
                        Review the proposed appointment state.
                    </p>

                </div>


                <div class="panel-body">


                    <div class="summary-row">

                        <div class="summary-label">
                            Patient
                        </div>

                        <div class="summary-value">

                            <%= escapeHtml(
                                    appointment
                                            .getPatientName()
                            ) %>

                        </div>

                        <div class="summary-muted">

                            <%= escapeHtml(
                                    appointment
                                            .getPatientCode()
                            ) %>

                        </div>

                    </div>


                    <div class="summary-row">

                        <div class="summary-label">
                            Dentist
                        </div>

                        <div class="summary-value"
                             id="summaryDentist">

                            Not selected

                        </div>

                        <div class="summary-muted"
                             id="summarySpecialization">

                        </div>

                    </div>


                    <div class="summary-row">

                        <div class="summary-label">
                            Treatment
                        </div>

                        <div class="summary-value"
                             id="summaryTreatment">

                            Not selected

                        </div>

                    </div>


                    <div class="summary-row">

                        <div class="summary-label">
                            Duration
                        </div>

                        <div class="summary-value"
                             id="summaryDuration">

                            —

                        </div>

                    </div>


                    <div class="summary-row">

                        <div class="summary-label">
                            Proposed Schedule
                        </div>

                        <div class="summary-value"
                             id="summarySchedule">

                            Date and time not selected

                        </div>

                    </div>


                    <div class="summary-row">

                        <div class="summary-label">
                            Estimated Charge
                        </div>

                        <div class="summary-value"
                             id="summaryCharge">

                            LKR 0.00

                        </div>

                    </div>


                    <div class="warning-box">

                        <strong>
                            Conflict protection
                        </strong>

                        <br>

                        The API re-checks the dentist's
                        schedule using treatment duration.
                        Overlapping appointments are rejected.

                    </div>


                    <div class="memento-box">

                        <strong>
                            Audit history
                        </strong>

                        <br>

                        When this change succeeds, the
                        previous appointment state is preserved
                        through the Memento history mechanism.

                    </div>

                </div>

            </aside>

        </div>

        <% } %>

    </main>

</div>


<script>

    const dentistSelect =
            document.getElementById(
                    "dentistId"
            );

    const treatmentSelect =
            document.getElementById(
                    "treatmentId"
            );

    const appointmentDate =
            document.getElementById(
                    "appointmentDate"
            );

    const appointmentTime =
            document.getElementById(
                    "appointmentTime"
            );


    function selectedOption(select) {

        if (!select
                || select.selectedIndex < 0) {

            return null;
        }

        return select.options[
                select.selectedIndex
        ];
    }


    function updateSummary() {

        const dentistOption =
                selectedOption(
                        dentistSelect
                );


        if (dentistOption
                && dentistOption.value) {

            document.getElementById(
                    "summaryDentist"
            ).textContent =
                    dentistOption.dataset.name
                    || "Selected dentist";


            document.getElementById(
                    "summarySpecialization"
            ).textContent =
                    dentistOption.dataset.specialization
                    || "";

        } else {

            document.getElementById(
                    "summaryDentist"
            ).textContent =
                    "Not selected";


            document.getElementById(
                    "summarySpecialization"
            ).textContent =
                    "";
        }


        const treatmentOption =
                selectedOption(
                        treatmentSelect
                );


        if (treatmentOption
                && treatmentOption.value) {

            document.getElementById(
                    "summaryTreatment"
            ).textContent =
                    treatmentOption.dataset.name
                    || "Selected treatment";


            const duration =
                    treatmentOption.dataset.duration;


            document.getElementById(
                    "summaryDuration"
            ).textContent =
                    duration
                            ? duration + " minutes"
                            : "—";


            const charge =
                    treatmentOption.dataset.charge;


            document.getElementById(
                    "summaryCharge"
            ).textContent =
                    "LKR "
                    + (
                        charge || "0.00"
                    );

        } else {

            document.getElementById(
                    "summaryTreatment"
            ).textContent =
                    "Not selected";


            document.getElementById(
                    "summaryDuration"
            ).textContent =
                    "—";


            document.getElementById(
                    "summaryCharge"
            ).textContent =
                    "LKR 0.00";
        }


        const dateValue =
                appointmentDate.value;

        const timeValue =
                appointmentTime.value;


        if (dateValue
                && timeValue) {

            document.getElementById(
                    "summarySchedule"
            ).textContent =
                    dateValue
                    + " at "
                    + timeValue;

        } else {

            document.getElementById(
                    "summarySchedule"
            ).textContent =
                    "Date and time not selected";
        }
    }


    dentistSelect.addEventListener(
            "change",
            updateSummary
    );


    treatmentSelect.addEventListener(
            "change",
            updateSummary
    );


    appointmentDate.addEventListener(
            "change",
            updateSummary
    );


    appointmentTime.addEventListener(
            "change",
            updateSummary
    );


    updateSummary();

</script>


</body>

</html>