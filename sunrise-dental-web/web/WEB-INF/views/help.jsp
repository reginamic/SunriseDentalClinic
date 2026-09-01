<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String contextPath =
            request.getContextPath();

    String fullName =
            session.getAttribute("fullName") != null
                    ? String.valueOf(
                            session.getAttribute("fullName")
                    )
                    : "Staff Member";

    String username =
            session.getAttribute("username") != null
                    ? String.valueOf(
                            session.getAttribute("username")
                    )
                    : "";

    String role =
            session.getAttribute("role") != null
                    ? String.valueOf(
                            session.getAttribute("role")
                    )
                    : "";

    boolean isAdmin =
            "ADMIN".equalsIgnoreCase(role);
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Help & User Guide | Sunrise Dental Clinic
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
                #f4f6f9;

            color:
                #1f2937;

            line-height:
                1.6;
        }

        .layout {
            display: flex;
            min-height: 100vh;
        }

        /*
         * ========================================================
         * SIDEBAR
         * ========================================================
         */

        .sidebar {
            width: 255px;

            background:
                #152238;

            color:
                #ffffff;

            position: fixed;

            top: 0;
            bottom: 0;
            left: 0;

            overflow-y: auto;

            padding-bottom: 30px;
        }

        .brand {
            padding:
                26px
                22px;

            border-bottom:
                1px solid
                rgba(
                    255,
                    255,
                    255,
                    0.10
                );
        }

        .brand h2 {
            font-size: 20px;

            margin-bottom: 4px;
        }

        .brand p {
            font-size: 12px;

            color:
                #c7d2e0;
        }

        .nav {
            padding:
                20px
                12px;
        }

        .nav-label {
            color:
                #8292a8;

            font-size:
                11px;

            font-weight:
                bold;

            letter-spacing:
                1px;

            padding:
                8px
                12px;

            text-transform:
                uppercase;
        }

        .nav a {
            display: block;

            text-decoration: none;

            color:
                #d8e0ea;

            padding:
                11px
                14px;

            margin-bottom:
                4px;

            border-radius:
                7px;

            font-size:
                14px;
        }

        .nav a:hover {
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

        .nav a.active {
            background:
                #ffffff;

            color:
                #152238;

            font-weight:
                bold;
        }

        /*
         * ========================================================
         * MAIN
         * ========================================================
         */

        .main {
            flex: 1;

            margin-left:
                255px;

            padding:
                32px;
        }

        .topbar {
            display: flex;

            align-items:
                center;

            justify-content:
                space-between;

            margin-bottom:
                26px;
        }

        .page-title h1 {
            color:
                #152238;

            font-size:
                28px;

            margin-bottom:
                4px;
        }

        .page-title p {
            color:
                #6b7280;

            font-size:
                14px;
        }

        .user-box {
            text-align:
                right;
        }

        .user-name {
            font-weight:
                bold;

            color:
                #152238;
        }

        .role-badge {
            display:
                inline-block;

            margin-top:
                4px;

            padding:
                3px
                9px;

            border-radius:
                20px;

            background:
                #e8edf4;

            color:
                #334155;

            font-size:
                11px;

            font-weight:
                bold;
        }

        /*
         * ========================================================
         * HERO
         * ========================================================
         */

        .hero {
            background:
                #ffffff;

            border:
                1px solid
                #e5e7eb;

            border-radius:
                12px;

            padding:
                28px;

            margin-bottom:
                24px;

            box-shadow:
                0 2px 8px
                rgba(
                    15,
                    23,
                    42,
                    0.04
                );
        }

        .hero h2 {
            color:
                #152238;

            font-size:
                22px;

            margin-bottom:
                9px;
        }

        .hero p {
            color:
                #5f6b7a;

            max-width:
                900px;
        }

        /*
         * ========================================================
         * QUICK WORKFLOW
         * ========================================================
         */

        .workflow {
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
                14px;

            margin-top:
                22px;
        }

        .workflow-step {
            background:
                #f8fafc;

            border:
                1px solid
                #e4e9f0;

            border-radius:
                9px;

            padding:
                15px;
        }

        .workflow-number {
            width:
                30px;

            height:
                30px;

            border-radius:
                50%;

            background:
                #152238;

            color:
                #ffffff;

            display:
                flex;

            align-items:
                center;

            justify-content:
                center;

            font-weight:
                bold;

            font-size:
                13px;

            margin-bottom:
                9px;
        }

        .workflow-step strong {
            display:
                block;

            color:
                #152238;

            margin-bottom:
                3px;

            font-size:
                14px;
        }

        .workflow-step span {
            color:
                #64748b;

            font-size:
                12px;
        }

        /*
         * ========================================================
         * SECTION
         * ========================================================
         */

        .section-title {
            color:
                #152238;

            font-size:
                19px;

            margin:
                28px
                0
                15px;
        }

        .help-grid {
            display:
                grid;

            grid-template-columns:
                repeat(
                    2,
                    minmax(
                        0,
                        1fr
                    )
                );

            gap:
                18px;
        }

        .help-card {
            background:
                #ffffff;

            border:
                1px solid
                #e5e7eb;

            border-radius:
                10px;

            padding:
                21px;

            box-shadow:
                0 2px 8px
                rgba(
                    15,
                    23,
                    42,
                    0.035
                );
        }

        .help-card h3 {
            color:
                #152238;

            font-size:
                17px;

            margin-bottom:
                9px;
        }

        .help-card p {
            color:
                #64748b;

            font-size:
                13px;

            margin-bottom:
                10px;
        }

        .help-card ul,
        .help-card ol {
            padding-left:
                20px;

            color:
                #4b5563;

            font-size:
                13px;
        }

        .help-card li {
            margin-bottom:
                6px;
        }

        .help-card a {
            color:
                #254d7a;

            font-weight:
                bold;

            text-decoration:
                none;
        }

        .help-card a:hover {
            text-decoration:
                underline;
        }

        /*
         * ========================================================
         * SECURITY NOTICE
         * ========================================================
         */

        .security-box {
            background:
                #fff;

            border:
                1px solid
                #dbe3ec;

            border-left:
                5px solid
                #152238;

            border-radius:
                9px;

            padding:
                20px;

            margin-top:
                18px;
        }

        .security-box h3 {
            color:
                #152238;

            margin-bottom:
                8px;

            font-size:
                16px;
        }

        .security-box ul {
            padding-left:
                20px;

            color:
                #4b5563;

            font-size:
                13px;
        }

        /*
         * ========================================================
         * TROUBLESHOOTING
         * ========================================================
         */

        .table-wrapper {
            overflow-x:
                auto;

            background:
                #ffffff;

            border:
                1px solid
                #e5e7eb;

            border-radius:
                10px;
        }

        table {
            width:
                100%;

            border-collapse:
                collapse;
        }

        th,
        td {
            text-align:
                left;

            padding:
                14px
                16px;

            border-bottom:
                1px solid
                #edf0f4;

            font-size:
                13px;

            vertical-align:
                top;
        }

        th {
            background:
                #f8fafc;

            color:
                #152238;

            font-size:
                12px;

            text-transform:
                uppercase;

            letter-spacing:
                0.4px;
        }

        tr:last-child td {
            border-bottom:
                none;
        }

        /*
         * ========================================================
         * ROLE INFORMATION
         * ========================================================
         */

        .role-panel {
            background:
                #152238;

            color:
                #ffffff;

            border-radius:
                10px;

            padding:
                22px;

            margin-top:
                24px;
        }

        .role-panel h3 {
            margin-bottom:
                8px;
        }

        .role-panel p {
            color:
                #d7dfeb;

            font-size:
                13px;
        }

        /*
         * ========================================================
         * FOOTER
         * ========================================================
         */

        .footer {
            color:
                #8a94a3;

            font-size:
                12px;

            margin-top:
                32px;

            padding-top:
                18px;

            border-top:
                1px solid
                #e3e7ed;
        }

        /*
         * ========================================================
         * RESPONSIVE
         * ========================================================
         */

        @media (
            max-width: 1000px
        ) {

            .workflow {
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
            max-width: 800px
        ) {

            .sidebar {
                width:
                    210px;
            }

            .main {
                margin-left:
                    210px;

                padding:
                    22px;
            }

            .help-grid {
                grid-template-columns:
                    1fr;
            }
        }

        @media (
            max-width: 650px
        ) {

            .sidebar {
                position:
                    static;

                width:
                    100%;
            }

            .layout {
                display:
                    block;
            }

            .main {
                margin-left:
                    0;
            }

            .topbar {
                align-items:
                    flex-start;

                gap:
                    15px;

                flex-direction:
                    column;
            }

            .user-box {
                text-align:
                    left;
            }

            .workflow {
                grid-template-columns:
                    1fr;
            }
        }

    </style>

</head>

<body>

<div class="layout">

    <!-- ======================================================
         SIDEBAR
         ====================================================== -->
    <aside class="sidebar">

        <div class="brand">

            <h2>
                Sunrise Dental
            </h2>

            <p>
                Clinic Management System
            </p>

        </div>

        <nav class="nav">

            <div class="nav-label">
                Main Menu
            </div>

            <a href="<%= contextPath %>/dashboard">
                Dashboard
            </a>

            <a href="<%= contextPath %>/patients">
                Patients
            </a>

            <a href="<%= contextPath %>/dentists">
                Dentists
            </a>

            <a href="<%= contextPath %>/treatments">
                Treatments
            </a>

            <a href="<%= contextPath %>/appointments">
                Appointments
            </a>

            <a href="<%= contextPath %>/bills">
                Billing
            </a>

            <% if (isAdmin) { %>

                <a href="<%= contextPath %>/reports">
                    Reports
                </a>

            <% } %>

            <div class="nav-label"
                 style="margin-top: 18px;">
                Support
            </div>

            <a class="active"
               href="<%= contextPath %>/help">
                Help & User Guide
            </a>

            <a href="<%= contextPath %>/logout">
                Logout
            </a>

        </nav>

    </aside>

    <!-- ======================================================
         MAIN CONTENT
         ====================================================== -->
    <main class="main">

        <div class="topbar">

            <div class="page-title">

                <h1>
                    Help & User Guide
                </h1>

                <p>
                    Guidance for using the Sunrise Dental Clinic
                    management system safely and correctly.
                </p>

            </div>

            <div class="user-box">

                <div class="user-name">
                    <%= fullName %>
                </div>

                <span class="role-badge">
                    <%= role %>
                </span>

            </div>

        </div>

        <!-- ==================================================
             INTRODUCTION
             ================================================== -->
        <section class="hero">

            <h2>
                Welcome to Sunrise Dental Support
            </h2>

            <p>
                This guide explains the standard clinic workflow,
                appointment handling, billing, patient management,
                system security and common troubleshooting steps.
                Access to individual functions depends on the role
                assigned to the authenticated staff account.
            </p>

            <div class="workflow">

                <div class="workflow-step">

                    <div class="workflow-number">
                        1
                    </div>

                    <strong>
                        Register Patient
                    </strong>

                    <span>
                        Find an existing patient or create
                        a new patient record.
                    </span>

                </div>

                <div class="workflow-step">

                    <div class="workflow-number">
                        2
                    </div>

                    <strong>
                        Book Appointment
                    </strong>

                    <span>
                        Select the dentist, treatment,
                        date and available time.
                    </span>

                </div>

                <div class="workflow-step">

                    <div class="workflow-number">
                        3
                    </div>

                    <strong>
                        Complete Treatment
                    </strong>

                    <span>
                        Update the appointment after the
                        clinical visit is completed.
                    </span>

                </div>

                <div class="workflow-step">

                    <div class="workflow-number">
                        4
                    </div>

                    <strong>
                        Generate Bill
                    </strong>

                    <span>
                        Create, review, receive payment
                        and print the patient bill.
                    </span>

                </div>

            </div>

        </section>

        <!-- ==================================================
             FUNCTION GUIDANCE
             ================================================== -->

        <h2 class="section-title">
            System Functions
        </h2>

        <section class="help-grid">

            <article class="help-card">

                <h3>
                    Patient Management
                </h3>

                <p>
                    Use the Patients module to maintain accurate
                    patient information before appointments are
                    registered.
                </p>

                <ul>
                    <li>
                        Search existing patient records before
                        registering a new patient.
                    </li>

                    <li>
                        Check name, address and contact information.
                    </li>

                    <li>
                        Correct patient details when necessary.
                    </li>

                    <li>
                        Keep clinical records instead of deleting
                        historical patient information.
                    </li>
                </ul>

                <p style="margin-top: 12px;">

                    <a href="<%= contextPath %>/patients">
                        Open Patient Management →
                    </a>

                </p>

            </article>

            <article class="help-card">

                <h3>
                    Appointment Management
                </h3>

                <p>
                    Appointment registration validates patients,
                    dentists, treatments, dates, times and dentist
                    availability.
                </p>

                <ol>
                    <li>
                        Select the correct patient.
                    </li>

                    <li>
                        Select an active dentist.
                    </li>

                    <li>
                        Select the required treatment.
                    </li>

                    <li>
                        Enter the appointment date and time.
                    </li>

                    <li>
                        Submit the booking.
                    </li>

                    <li>
                        If a scheduling conflict is reported,
                        choose another available time.
                    </li>
                </ol>

                <p style="margin-top: 12px;">

                    <a href="<%= contextPath %>/appointments">
                        Open Appointments →
                    </a>

                </p>

            </article>

            <article class="help-card">

                <h3>
                    Rescheduling & History
                </h3>

                <p>
                    When an appointment is rescheduled, the
                    previous appointment state is retained in
                    appointment history.
                </p>

                <ul>
                    <li>
                        Open the required appointment.
                    </li>

                    <li>
                        Select the reschedule option.
                    </li>

                    <li>
                        Enter the new date and time.
                    </li>

                    <li>
                        Confirm the updated appointment.
                    </li>

                    <li>
                        Review history when previous scheduling
                        information is required.
                    </li>
                </ul>

            </article>

            <article class="help-card">

                <h3>
                    Billing & Receipts
                </h3>

                <p>
                    Bills are generated only for completed
                    appointments and preserve the financial
                    values used at the time of billing.
                </p>

                <ul>
                    <li>
                        Confirm that the appointment is completed.
                    </li>

                    <li>
                        Generate the patient bill.
                    </li>

                    <li>
                        Review treatment and consultation charges.
                    </li>

                    <li>
                        Record payment when received.
                    </li>

                    <li>
                        Print the bill or receipt for the patient.
                    </li>
                </ul>

                <p style="margin-top: 12px;">

                    <a href="<%= contextPath %>/billing">
                        Open Billing →
                    </a>

                </p>

            </article>

            <article class="help-card">

                <h3>
                    Dentist & Treatment Information
                </h3>

                <p>
                    The dentist and treatment modules provide
                    scheduling and pricing information used during
                    appointment registration and billing.
                </p>

                <ul>
                    <li>
                        Reception staff can view available dentists
                        and treatments.
                    </li>

                    <li>
                        Inactive dentists or treatments should not
                        be used for new appointments.
                    </li>

                    <% if (isAdmin) { %>

                        <li>
                            Administrators can maintain dentist
                            availability and treatment information.
                        </li>

                    <% } %>

                </ul>

            </article>

            <% if (isAdmin) { %>

                <article class="help-card">

                    <h3>
                        Reports & Analytics
                    </h3>

                    <p>
                        Administrative reports provide appointment,
                        financial, dentist workload and treatment
                        demand information.
                    </p>

                    <ul>
                        <li>
                            Select a valid date range.
                        </li>

                        <li>
                            Review appointment status totals.
                        </li>

                        <li>
                            Review billed, paid and outstanding
                            amounts.
                        </li>

                        <li>
                            Review dentist workload and treatment
                            demand.
                        </li>

                        <li>
                            Print reports when documentary evidence
                            is required.
                        </li>
                    </ul>

                    <p style="margin-top: 12px;">

                        <a href="<%= contextPath %>/reports">
                            Open Reports →
                        </a>

                    </p>

                </article>

            <% } %>

        </section>

        <!-- ==================================================
             SECURITY
             ================================================== -->

        <h2 class="section-title">
            Security & Safe Use
        </h2>

        <section class="security-box">

            <h3>
                Staff Security Responsibilities
            </h3>

            <ul>

                <li>
                    Use only your assigned staff account.
                </li>

                <li>
                    Never share account passwords with another
                    staff member.
                </li>

                <li>
                    Do not leave an authenticated clinic session
                    unattended.
                </li>

                <li>
                    Use Logout when leaving the system.
                </li>

                <li>
                    Never enter passwords into patient notes,
                    appointment notes or other clinical fields.
                </li>

                <li>
                    Administrative functions are restricted by
                    staff role.
                </li>

                <li>
                    Security-sensitive events may be recorded in
                    the system audit trail.
                </li>

            </ul>

        </section>

        <!-- ==================================================
             TROUBLESHOOTING
             ================================================== -->

        <h2 class="section-title">
            Troubleshooting
        </h2>

        <section class="table-wrapper">

            <table>

                <thead>

                    <tr>

                        <th>
                            Problem
                        </th>

                        <th>
                            Possible Reason
                        </th>

                        <th>
                            Recommended Action
                        </th>

                    </tr>

                </thead>

                <tbody>

                    <tr>

                        <td>
                            Unable to sign in
                        </td>

                        <td>
                            Incorrect username/password,
                            inactive account or unavailable service.
                        </td>

                        <td>
                            Re-enter credentials carefully.
                            If the problem continues, contact
                            the system administrator.
                        </td>

                    </tr>

                    <tr>

                        <td>
                            Appointment cannot be registered
                        </td>

                        <td>
                            Dentist is already occupied,
                            treatment/dentist is inactive,
                            or entered information is invalid.
                        </td>

                        <td>
                            Review the validation message and
                            select another valid appointment time.
                        </td>

                    </tr>

                    <tr>

                        <td>
                            Bill cannot be generated
                        </td>

                        <td>
                            Appointment has not been completed
                            or a bill already exists.
                        </td>

                        <td>
                            Review appointment status and existing
                            billing information before retrying.
                        </td>

                    </tr>

                    <tr>

                        <td>
                            Reports page unavailable
                        </td>

                        <td>
                            The signed-in user does not have
                            ADMIN permission.
                        </td>

                        <td>
                            Administrative reports must be accessed
                            using an authorized ADMIN account.
                        </td>

                    </tr>

                    <tr>

                        <td>
                            Session returns to Login page
                        </td>

                        <td>
                            The session may have expired after
                            inactivity.
                        </td>

                        <td>
                            Sign in again using the authorized
                            staff account.
                        </td>

                    </tr>

                    <tr>

                        <td>
                            Page or API is temporarily unavailable
                        </td>

                        <td>
                            Application server or database service
                            may not be running.
                        </td>

                        <td>
                            Verify the clinic services are running
                            and retry the operation.
                        </td>

                    </tr>

                </tbody>

            </table>

        </section>

        <!-- ==================================================
             ROLE-SPECIFIC MESSAGE
             ================================================== -->

        <section class="role-panel">

            <% if (isAdmin) { %>

                <h3>
                    Administrator Guidance
                </h3>

                <p>
                    You are signed in with administrative access.
                    In addition to normal clinic operations, your
                    role can access restricted management and
                    reporting functions. Use administrative
                    privileges only for authorized clinic tasks.
                </p>

            <% } else { %>

                <h3>
                    Receptionist Guidance
                </h3>

                <p>
                    You are signed in with receptionist access.
                    Your main responsibilities include patient
                    registration, appointment handling, billing
                    and viewing dentist/treatment information.
                    Administrative reporting and configuration
                    functions remain restricted.
                </p>

            <% } %>

        </section>

        <footer class="footer">

            Sunrise Dental Clinic Management System
            &nbsp;|&nbsp;
            Help & User Guide
            &nbsp;|&nbsp;
            Signed in as
            <%= username %>

        </footer>

    </main>

</div>

</body>

</html>