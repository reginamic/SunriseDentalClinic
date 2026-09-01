<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.math.BigDecimal"%>

<%@page import="com.sunrisedental.web.model.PatientViewModel"%>
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


    List<PatientViewModel> patients =
            (List<PatientViewModel>)
                    request.getAttribute(
                            "patients"
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


    if (patients == null) {
        patients = Collections.emptyList();
    }


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


    String formPatientId =
            (String)
                    request.getAttribute(
                            "formPatientId"
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


    String minimumDate =
            LocalDate.now()
                    .toString();
%>


<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Register Appointment | Sunrise Dental
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
            color: white;
            font-size: 13px;
            margin-top: 12px;
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
            margin-bottom: 24px;

            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 20px;
        }


        .page-header h1 {
            color: #1f3347;
            font-size: 27px;
            margin-bottom: 6px;
        }


        .page-header p {
            color: #718096;
            font-size: 14px;
        }


        /* =====================================================
           BUTTONS
           ===================================================== */

        .btn {
            display: inline-block;
            border: none;
            border-radius: 7px;
            padding: 10px 17px;
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
           MAIN GRID
           ===================================================== */

        .content-grid {
            display: grid;

            grid-template-columns:
                    minmax(0, 2fr)
                    minmax(280px, 1fr);

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
            color: #78899b;
            font-size: 12px;
        }


        /* =====================================================
           FORM
           ===================================================== */

        .form-body {
            padding: 23px;
        }


        .form-section {
            margin-bottom: 28px;
        }


        .form-section:last-child {
            margin-bottom: 0;
        }


        .section-heading {
            margin-bottom: 16px;
        }


        .section-heading h3 {
            font-size: 15px;
            color: #31465a;
            margin-bottom: 3px;
        }


        .section-heading p {
            font-size: 12px;
            color: #8997a5;
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
            font-size: 12px;
            font-weight: 600;
            color: #536679;
            margin-bottom: 6px;
        }


        .required {
            color: #bd4141;
        }


        .form-control {
            width: 100%;

            padding: 10px 11px;

            border:
                    1px solid #cbd6df;

            border-radius: 7px;

            color: #293b4d;

            background: white;

            outline: none;
        }


        .form-control:focus {
            border-color: #3c88a2;

            box-shadow:
                    0 0 0 2px
                    rgba(60,136,162,0.10);
        }


        textarea.form-control {
            resize: vertical;
            min-height: 110px;
            line-height: 1.5;
        }


        .helper-text {
            margin-top: 5px;
            font-size: 11px;
            color: #8795a4;
        }


        /* =====================================================
           FORM ACTIONS
           ===================================================== */

        .form-actions {
            border-top:
                    1px solid #e7ecf1;

            padding-top: 20px;

            display: flex;
            justify-content: flex-end;
            gap: 9px;
        }


        /* =====================================================
           SIDE INFORMATION
           ===================================================== */

        .summary-panel {
            position: sticky;
            top: 30px;
        }


        .summary-body {
            padding: 20px;
        }


        .summary-title {
            font-size: 15px;
            margin-bottom: 17px;
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
            color: #7b8998;
            font-size: 11px;
            text-transform: uppercase;
            letter-spacing: 0.4px;
            margin-bottom: 5px;
        }


        .summary-value {
            color: #2c4054;
            font-size: 14px;
            font-weight: 600;
        }


        .summary-muted {
            color: #8595a5;
            font-size: 12px;
            font-weight: 400;
        }


        .total-box {
            margin-top: 17px;

            background: #eef7fa;

            border:
                    1px solid #cee4ea;

            border-radius: 8px;

            padding: 16px;
        }


        .total-box .label {
            font-size: 11px;
            text-transform: uppercase;
            color: #608092;
            margin-bottom: 5px;
        }


        .total-box .amount {
            font-size: 24px;
            font-weight: 700;
            color: #176b87;
        }


        .info-box {
            margin-top: 18px;

            border-radius: 8px;

            padding: 14px;

            background: #fff9e8;

            border:
                    1px solid #eadcae;

            color: #736329;

            font-size: 12px;

            line-height: 1.5;
        }


        /* =====================================================
           DATA AVAILABILITY
           ===================================================== */

        .availability-status {
            display: grid;
            grid-template-columns:
                    repeat(3, 1fr);

            gap: 10px;

            margin-bottom: 22px;
        }


        .availability-item {
            background: #f8fafc;

            border:
                    1px solid #e1e7ed;

            border-radius: 7px;

            padding: 12px;
        }


        .availability-number {
            font-size: 20px;
            font-weight: 700;
            color: #176b87;
        }


        .availability-label {
            font-size: 10px;
            color: #7d8c9a;
            margin-top: 3px;
            text-transform: uppercase;
        }


        /* =====================================================
           RESPONSIVE
           ===================================================== */

        @media (max-width: 1000px) {

            .content-grid {
                grid-template-columns: 1fr;
            }


            .summary-panel {
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


            .availability-status {
                grid-template-columns: 1fr;
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


        <!-- PAGE HEADER -->

        <div class="page-header">

            <div>

                <h1>
                    Register New Appointment
                </h1>

                <p>
                    Create a patient appointment with an
                    available dentist and active treatment.
                </p>

            </div>


            <a class="btn btn-secondary"
               href="<%= contextPath %>/appointments">

                ← Back to Appointments

            </a>

        </div>


        <!-- =================================================
             ERROR MESSAGE
             ================================================= -->

        <% if (errorMessage != null
                && !errorMessage.isBlank()) { %>

            <div class="alert alert-error">

                <strong>
                    Appointment could not be registered.
                </strong>

                <br>

                <%= escapeHtml(errorMessage) %>

            </div>

        <% } %>


        <!-- =================================================
             MAIN GRID
             ================================================= -->

        <div class="content-grid">


            <!-- =============================================
                 FORM PANEL
                 ============================================= -->

            <section class="panel">

                <div class="panel-header">

                    <h2>
                        Appointment Information
                    </h2>

                    <p>
                        Fields marked with * are required.
                    </p>

                </div>


                <div class="form-body">


                    <!-- REFERENCE DATA STATUS -->

                    <div class="availability-status">

                        <div class="availability-item">

                            <div class="availability-number">

                                <%= patients.size() %>

                            </div>

                            <div class="availability-label">
                                Registered Patients
                            </div>

                        </div>


                        <div class="availability-item">

                            <div class="availability-number">

                                <%= dentists.size() %>

                            </div>

                            <div class="availability-label">
                                Active Dentists
                            </div>

                        </div>


                        <div class="availability-item">

                            <div class="availability-number">

                                <%= treatments.size() %>

                            </div>

                            <div class="availability-label">
                                Active Treatments
                            </div>

                        </div>

                    </div>


                    <form method="POST"
                          action="<%= contextPath %>/appointments/register"
                          id="appointmentForm">


                        <!-- =================================
                             PATIENT
                             ================================= -->

                        <div class="form-section">

                            <div class="section-heading">

                                <h3>
                                    1. Patient
                                </h3>

                                <p>
                                    Select the patient receiving treatment.
                                </p>

                            </div>


                            <div class="form-grid">

                                <div class="form-group full">

                                    <label class="form-label"
                                           for="patientId">

                                        Patient
                                        <span class="required">*</span>

                                    </label>


                                    <select
                                        class="form-control"
                                        id="patientId"
                                        name="patientId"
                                        required>

                                        <option value="">
                                            -- Select Patient --
                                        </option>


                                        <%
                                            for (PatientViewModel patient
                                                    : patients) {
                                        %>

                                            <option
                                                value="<%= patient.getPatientId() %>"

                                                data-name="<%= escapeHtml(
                                                        patient.getFullName()
                                                ) %>"

                                                data-code="<%= escapeHtml(
                                                        patient.getPatientCode()
                                                ) %>"

                                                data-contact="<%= escapeHtml(
                                                        patient.getContactNumber()
                                                ) %>"

                                                <%= selected(
                                                        formPatientId,
                                                        patient.getPatientId()
                                                ) %>>

                                                <%= escapeHtml(
                                                        patient.getPatientCode()
                                                ) %>

                                                —

                                                <%= escapeHtml(
                                                        patient.getFullName()
                                                ) %>

                                            </option>

                                        <%
                                            }
                                        %>

                                    </select>


                                    <div class="helper-text">

                                        Patient records are loaded
                                        from the Sunrise Dental REST API.

                                    </div>

                                </div>

                            </div>

                        </div>


                        <!-- =================================
                             DENTIST & TREATMENT
                             ================================= -->

                        <div class="form-section">

                            <div class="section-heading">

                                <h3>
                                    2. Dentist &amp; Treatment
                                </h3>

                                <p>
                                    Only active dentists and active
                                    treatments are available.
                                </p>

                            </div>


                            <div class="form-grid">


                                <!-- DENTIST -->

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

                                                data-code="<%= escapeHtml(
                                                        dentist.getDentistCode()
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
                                                        && !dentist.getSpecialization()
                                                                .isBlank()) { %>

                                                    (
                                                    <%= escapeHtml(
                                                            dentist.getSpecialization()
                                                    ) %>
                                                    )

                                                <% } %>

                                            </option>

                                        <%
                                            }
                                        %>

                                    </select>

                                </div>


                                <!-- TREATMENT -->

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
                                                        treatment.getTreatmentName()
                                                ) %>"

                                                data-code="<%= escapeHtml(
                                                        treatment.getTreatmentCode()
                                                ) %>"

                                                data-duration="<%= treatment.getEstimatedDurationMinutes() %>"

                                                data-charge="<%= money(
                                                        treatment.getStandardCharge()
                                                ) %>"

                                                <%= selected(
                                                        formTreatmentId,
                                                        treatment.getTreatmentId()
                                                ) %>>

                                                <%= escapeHtml(
                                                        treatment.getTreatmentCode()
                                                ) %>

                                                —

                                                <%= escapeHtml(
                                                        treatment.getTreatmentName()
                                                ) %>

                                                —

                                                <%= escapeHtml(
                                                        treatment.getDurationText()
                                                ) %>

                                                —

                                                LKR
                                                <%= money(
                                                        treatment.getStandardCharge()
                                                ) %>

                                            </option>

                                        <%
                                            }
                                        %>

                                    </select>


                                    <div class="helper-text">

                                        Displayed charge =
                                        treatment price +
                                        consultation fee.

                                    </div>

                                </div>

                            </div>

                        </div>


                        <!-- =================================
                             DATE & TIME
                             ================================= -->

                        <div class="form-section">

                            <div class="section-heading">

                                <h3>
                                    3. Schedule
                                </h3>

                                <p>
                                    Choose the required appointment date
                                    and starting time.
                                </p>

                            </div>


                            <div class="form-grid">


                                <!-- DATE -->

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
                                        min="<%= minimumDate %>"
                                        value="<%= escapeHtml(
                                                formAppointmentDate
                                        ) %>"
                                        required
                                    >

                                </div>


                                <!-- TIME -->

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


                                    <div class="helper-text">

                                        Dentist availability and
                                        treatment duration are validated
                                        when the appointment is saved.

                                    </div>

                                </div>

                            </div>

                        </div>


                        <!-- =================================
                             NOTES
                             ================================= -->

                        <div class="form-section">

                            <div class="section-heading">

                                <h3>
                                    4. Appointment Notes
                                </h3>

                                <p>
                                    Add any useful booking information.
                                </p>

                            </div>


                            <div class="form-grid">

                                <div class="form-group full">

                                    <label class="form-label"
                                           for="notes">

                                        Notes
                                    </label>


                                    <textarea
                                        class="form-control"
                                        id="notes"
                                        name="notes"
                                        maxlength="1000"
                                        placeholder="Example: Patient requested morning appointment."><%= escapeHtml(
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
                               href="<%= contextPath %>/appointments">

                                Cancel

                            </a>


                            <button
                                class="btn btn-primary"
                                type="submit"
                                <%= patients.isEmpty()
                                        || dentists.isEmpty()
                                        || treatments.isEmpty()
                                        ? "disabled"
                                        : "" %>>

                                Register Appointment

                            </button>

                        </div>

                    </form>

                </div>

            </section>


            <!-- =============================================
                 LIVE SUMMARY
                 ============================================= -->

            <aside class="panel summary-panel">

                <div class="panel-header">

                    <h2>
                        Appointment Summary
                    </h2>

                    <p>
                        Review the selection before saving.
                    </p>

                </div>


                <div class="summary-body">

                    <div class="summary-row">

                        <div class="summary-label">
                            Patient
                        </div>

                        <div class="summary-value"
                             id="summaryPatient">

                            Not selected

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
                            Appointment
                        </div>

                        <div class="summary-value"
                             id="summarySchedule">

                            Date and time not selected

                        </div>

                    </div>


                    <div class="total-box">

                        <div class="label">
                            Estimated Standard Charge
                        </div>

                        <div class="amount"
                             id="summaryCharge">

                            LKR 0.00

                        </div>

                    </div>


                    <div class="info-box">

                        <strong>
                            Double-booking protection
                        </strong>

                        <br>

                        Sunrise Dental checks the dentist's
                        existing appointments using the selected
                        treatment duration before the booking is
                        accepted.

                    </div>

                </div>

            </aside>

        </div>

    </main>

</div>


<script>

    const patientSelect =
            document.getElementById(
                    "patientId"
            );

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


    function getSelectedOption(select) {

        if (!select
                || select.selectedIndex < 0) {

            return null;
        }

        return select.options[
                select.selectedIndex
        ];
    }


    function updateSummary() {

        /* ==============================================
           PATIENT
           ============================================== */

        const patientOption =
                getSelectedOption(
                        patientSelect
                );


        if (patientOption
                && patientOption.value) {

            const patientCode =
                    patientOption.dataset.code || "";

            const patientName =
                    patientOption.dataset.name || "";


            document.getElementById(
                    "summaryPatient"
            ).textContent =
                    patientCode
                    + " — "
                    + patientName;

        } else {

            document.getElementById(
                    "summaryPatient"
            ).textContent =
                    "Not selected";
        }


        /* ==============================================
           DENTIST
           ============================================== */

        const dentistOption =
                getSelectedOption(
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


        /* ==============================================
           TREATMENT
           ============================================== */

        const treatmentOption =
                getSelectedOption(
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
                        charge
                        || "0.00"
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


        /* ==============================================
           DATE + TIME
           ============================================== */

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

        } else if (dateValue) {

            document.getElementById(
                    "summarySchedule"
            ).textContent =
                    dateValue
                    + " — time not selected";

        } else {

            document.getElementById(
                    "summarySchedule"
            ).textContent =
                    "Date and time not selected";
        }
    }


    patientSelect.addEventListener(
            "change",
            updateSummary
    );


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


    /*
     * Populate summary when the page reloads
     * after a validation error.
     */
    updateSummary();

</script>


</body>

</html>