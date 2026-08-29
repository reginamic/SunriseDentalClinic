<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.sunrisedental.web.model.AppointmentViewModel"%>

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


    private String display(String value) {

        if (value == null
                || value.isBlank()) {

            return "—";
        }

        return value;
    }
%>

<%
    AppointmentViewModel appointment =
            (AppointmentViewModel)
                    request.getAttribute(
                            "appointment"
                    );

    String statusAction =
            (String)
                    request.getAttribute(
                            "statusAction"
                    );

    String errorMessage =
            (String)
                    request.getAttribute(
                            "errorMessage"
                    );

    boolean cancelAction =
            "cancel".equalsIgnoreCase(
                    statusAction
            );

    boolean completeAction =
            "complete".equalsIgnoreCase(
                    statusAction
            );

    String pageTitle =
            cancelAction
                    ? "Cancel Appointment"
                    : "Complete Appointment";

    String actionDescription =
            cancelAction
                    ? "Confirm cancellation of this scheduled appointment."
                    : "Confirm that the dental appointment has been completed.";

    String buttonText =
            cancelAction
                    ? "Confirm Cancellation"
                    : "Mark as Completed";
%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        <%= pageTitle %> | Sunrise Dental
    </title>


    <style>

        * {
            box-sizing: border-box;
        }


        body {
            margin: 0;
            font-family:
                Arial,
                Helvetica,
                sans-serif;

            background: #f4f7fa;
            color: #22364a;
        }


        /* ==========================================
           PAGE LAYOUT
           ========================================== */

        .layout {
            min-height: 100vh;

            display: grid;

            grid-template-columns:
                285px 1fr;
        }


        /* ==========================================
           SIDEBAR
           ========================================== */

        .sidebar {
            background: #173c55;
            color: #ffffff;

            padding: 30px 20px;

            min-height: 100vh;
        }


        .brand {
            padding:
                4px 12px
                28px 12px;

            border-bottom:
                1px solid rgba(
                    255,
                    255,
                    255,
                    0.12
                );
        }


        .brand h2 {
            margin: 0;

            font-size: 25px;
            font-weight: 700;
        }


        .brand p {
            margin:
                10px 0 0;

            font-size: 13px;
            color: #b7d3e4;
        }


        .navigation {
            margin-top: 26px;
        }


        .nav-heading {
            margin:
                20px 12px
                10px;

            color: #83acc5;

            font-size: 11px;

            letter-spacing:
                1px;

            text-transform:
                uppercase;
        }


        .navigation a {
            display: block;

            padding:
                13px 14px;

            margin-bottom:
                5px;

            border-radius:
                8px;

            color: #ffffff;

            text-decoration: none;

            font-size: 14px;
        }


        .navigation a:hover {
            background:
                rgba(
                    255,
                    255,
                    255,
                    0.08
                );
        }


        .navigation a.active {
            background: #2b7c99;
            font-weight: 700;
        }


        .sidebar-footer {
            margin-top: 38px;

            padding:
                22px 12px
                0;

            border-top:
                1px solid rgba(
                    255,
                    255,
                    255,
                    0.12
                );
        }


        .user-name {
            font-size: 14px;
            font-weight: 700;
        }


        .user-role {
            margin-top: 5px;

            color: #9bc5dd;

            font-size: 12px;
        }


        .sign-out {
            display: inline-block;

            margin-top: 17px;

            color: #ffffff;

            text-decoration: none;

            font-size: 13px;
        }


        /* ==========================================
           CONTENT
           ========================================== */

        .main {
            padding:
                42px 45px;
        }


        .page-container {
            width: 100%;
            max-width: 900px;

            margin: 0 auto;
        }


        .back-link {
            display: inline-block;

            margin-bottom: 18px;

            color: #287492;

            text-decoration: none;

            font-size: 13px;
            font-weight: 600;
        }


        .page-header {
            margin-bottom: 23px;
        }


        .page-header h1 {
            margin: 0;

            color: #20374c;

            font-size: 27px;
        }


        .page-header p {
            margin:
                8px 0 0;

            color: #718397;

            font-size: 13px;
        }


        /* ==========================================
           ALERTS
           ========================================== */

        .alert {
            margin-bottom: 20px;

            padding:
                14px 16px;

            border-radius:
                8px;

            font-size: 13px;

            line-height: 1.5;
        }


        .alert-error {
            background: #fff1f1;

            border:
                1px solid #efc7c7;

            color: #a43c3c;
        }


        /* ==========================================
           CONFIRMATION CARD
           ========================================== */

        .card {
            background: #ffffff;

            border:
                1px solid #dce5ec;

            border-radius:
                12px;

            overflow: hidden;

            box-shadow:
                0 3px 12px
                rgba(
                    31,
                    50,
                    70,
                    0.05
                );
        }


        .card-header {
            padding:
                22px 24px;

            border-bottom:
                1px solid #e5ebef;
        }


        .card-header h2 {
            margin: 0;

            font-size: 18px;

            color: #263e52;
        }


        .card-header p {
            margin:
                7px 0 0;

            color: #7b8d9f;

            font-size: 12px;
        }


        .card-body {
            padding: 24px;
        }


        /* ==========================================
           WARNING BOX
           ========================================== */

        .warning-box {
            margin-bottom: 24px;

            padding:
                17px 18px;

            border-radius:
                8px;

            line-height: 1.55;

            font-size: 13px;
        }


        .warning-cancel {
            background: #fff4f4;

            border:
                1px solid #efcccc;

            color: #8f3c3c;
        }


        .warning-complete {
            background: #edf8f3;

            border:
                1px solid #cae6d8;

            color: #356b54;
        }


        .warning-title {
            display: block;

            margin-bottom: 5px;

            font-weight: 700;
        }


        /* ==========================================
           APPOINTMENT SUMMARY
           ========================================== */

        .summary {
            margin-bottom: 25px;

            border:
                1px solid #dfe7ed;

            border-radius:
                9px;

            overflow: hidden;
        }


        .summary-heading {
            padding:
                12px 16px;

            background: #f7f9fb;

            border-bottom:
                1px solid #e4e9ee;

            color: #556b7e;

            font-size: 11px;

            font-weight: 700;

            text-transform:
                uppercase;

            letter-spacing:
                0.5px;
        }


        .summary-grid {
            display: grid;

            grid-template-columns:
                repeat(
                    2,
                    minmax(
                        0,
                        1fr
                    )
                );

            gap: 0;
        }


        .summary-item {
            padding:
                16px;

            border-bottom:
                1px solid #edf1f4;
        }


        .summary-item:nth-child(odd) {
            border-right:
                1px solid #edf1f4;
        }


        .summary-label {
            margin-bottom: 5px;

            color: #8a9aaa;

            font-size: 10px;

            font-weight: 700;

            text-transform:
                uppercase;

            letter-spacing:
                0.4px;
        }


        .summary-value {
            color: #253b4f;

            font-size: 14px;

            font-weight: 600;

            word-break:
                break-word;
        }


        /* ==========================================
           FORM
           ========================================== */

        .form-group {
            margin-bottom: 22px;
        }


        .form-label {
            display: block;

            margin-bottom: 7px;

            color: #334d62;

            font-size: 12px;

            font-weight: 700;
        }


        .required {
            color: #bd4545;
        }


        textarea {
            width: 100%;
            min-height: 110px;

            resize: vertical;

            padding:
                12px 13px;

            border:
                1px solid #cfdbe4;

            border-radius:
                7px;

            outline: none;

            font-family: inherit;

            font-size: 13px;

            color: #314a5e;

            background: #ffffff;
        }


        textarea:focus {
            border-color: #2b7c99;

            box-shadow:
                0 0 0 3px
                rgba(
                    43,
                    124,
                    153,
                    0.10
                );
        }


        .form-help {
            display: block;

            margin-top: 7px;

            color: #8393a3;

            font-size: 11px;
        }


        /* ==========================================
           ACTIONS
           ========================================== */

        .actions {
            display: flex;

            justify-content: flex-end;

            gap: 10px;

            margin-top: 24px;

            padding-top: 20px;

            border-top:
                1px solid #e8edf1;
        }


        .button {
            min-width: 145px;

            padding:
                11px 18px;

            border-radius:
                7px;

            font-family: inherit;

            font-size: 12px;

            font-weight: 700;

            text-align: center;

            text-decoration: none;

            cursor: pointer;
        }


        .button-secondary {
            background: #ffffff;

            border:
                1px solid #cbd7e0;

            color: #42596d;
        }


        .button-secondary:hover {
            background: #f6f8fa;
        }


        .button-complete {
            border: none;

            background: #287a5b;

            color: #ffffff;
        }


        .button-complete:hover {
            background: #21674d;
        }


        .button-cancel {
            border: none;

            background: #b84545;

            color: #ffffff;
        }


        .button-cancel:hover {
            background: #9e3939;
        }


        /* ==========================================
           RESPONSIVE
           ========================================== */

        @media (
            max-width: 850px
        ) {

            .layout {
                grid-template-columns:
                    1fr;
            }


            .sidebar {
                display: none;
            }


            .main {
                padding:
                    24px;
            }
        }


        @media (
            max-width: 600px
        ) {

            .summary-grid {
                grid-template-columns:
                    1fr;
            }


            .summary-item:nth-child(odd) {
                border-right: none;
            }


            .actions {
                flex-direction:
                    column-reverse;
            }


            .button {
                width: 100%;
            }
        }

    </style>

</head>


<body>

<div class="layout">


    <!-- ==========================================
         SIDEBAR
         ========================================== -->

    <aside class="sidebar">

        <div class="brand">

            <h2>
                Sunrise Dental
            </h2>

            <p>
                Clinic Management System
            </p>

        </div>


        <nav class="navigation">

            <div class="nav-heading">
                Main
            </div>


            <a
                href="<%= request.getContextPath() %>/dashboard">

                Dashboard

            </a>


            <div class="nav-heading">
                Clinic Management
            </div>


            <a
                href="<%= request.getContextPath() %>/patients">

                Patients

            </a>


            <a
                href="<%= request.getContextPath() %>/dentists">

                Dentists

            </a>


            <a
                href="<%= request.getContextPath() %>/treatments">

                Treatments

            </a>


            <a
                class="active"
                href="<%= request.getContextPath() %>/appointments">

                Appointments

            </a>


            <a
                href="<%= request.getContextPath() %>/bills">

                Billing

            </a>

        </nav>


        <div class="sidebar-footer">

            <div class="user-name">

                <%= escapeHtml(
                        String.valueOf(
                                session.getAttribute(
                                        "fullName"
                                )
                        )
                ) %>

            </div>


            <div class="user-role">

                <%= escapeHtml(
                        String.valueOf(
                                session.getAttribute(
                                        "role"
                                )
                        )
                ) %>

            </div>


            <a
                class="sign-out"
                href="<%= request.getContextPath() %>/logout">

                Sign Out

            </a>

        </div>

    </aside>


    <!-- ==========================================
         MAIN CONTENT
         ========================================== -->

    <main class="main">

        <div class="page-container">


            <% if (appointment != null) { %>

                <a
                    class="back-link"
                    href="<%= request.getContextPath() %>/appointments/details?id=<%= appointment.getAppointmentId() %>">

                    &larr; Back to Appointment Details

                </a>

            <% } else { %>

                <a
                    class="back-link"
                    href="<%= request.getContextPath() %>/appointments">

                    &larr; Back to Appointments

                </a>

            <% } %>


            <div class="page-header">

                <h1>
                    <%= pageTitle %>
                </h1>

                <p>
                    <%= actionDescription %>
                </p>

            </div>


            <!-- ERROR -->

            <% if (errorMessage != null
                    && !errorMessage.isBlank()) { %>

                <div class="alert alert-error">

                    <strong>
                        Unable to continue:
                    </strong>

                    <%= escapeHtml(
                            errorMessage
                    ) %>

                </div>

            <% } %>


            <% if (appointment != null
                    && (cancelAction
                        || completeAction)) { %>


                <div class="card">


                    <div class="card-header">

                        <h2>

                            <%= cancelAction
                                    ? "Confirm Appointment Cancellation"
                                    : "Confirm Treatment Completion" %>

                        </h2>


                        <p>

                            Review the appointment information
                            before confirming this status change.

                        </p>

                    </div>


                    <div class="card-body">


                        <!-- WARNING -->

                        <div
                            class="warning-box
                            <%= cancelAction
                                    ? "warning-cancel"
                                    : "warning-complete" %>">


                            <span class="warning-title">

                                <%= cancelAction
                                        ? "Cancellation is a final appointment state."
                                        : "Completion is a final appointment state." %>

                            </span>


                            <% if (cancelAction) { %>

                                The appointment will remain stored
                                as part of the clinic's historical
                                records and will not be physically
                                deleted.

                            <% } else { %>

                                After confirmation, the appointment
                                will be marked as completed and
                                preserved as part of the patient's
                                appointment history.

                            <% } %>

                        </div>


                        <!-- APPOINTMENT SUMMARY -->

                        <div class="summary">


                            <div class="summary-heading">

                                Appointment Being Changed

                            </div>


                            <div class="summary-grid">


                                <div class="summary-item">

                                    <div class="summary-label">
                                        Appointment Number
                                    </div>

                                    <div class="summary-value">

                                        <%= escapeHtml(
                                                display(
                                                        appointment
                                                                .getAppointmentNumber()
                                                )
                                        ) %>

                                    </div>

                                </div>


                                <div class="summary-item">

                                    <div class="summary-label">
                                        Current Status
                                    </div>

                                    <div class="summary-value">
                                        SCHEDULED
                                    </div>

                                </div>


                                <div class="summary-item">

                                    <div class="summary-label">
                                        Appointment Date
                                    </div>

                                    <div class="summary-value">

                                        <%= escapeHtml(
                                                display(
                                                        appointment
                                                                .getAppointmentDate()
                                                )
                                        ) %>

                                    </div>

                                </div>


                                <div class="summary-item">

                                    <div class="summary-label">
                                        Appointment Time
                                    </div>

                                    <div class="summary-value">

                                        <%= escapeHtml(
                                                display(
                                                        appointment
                                                                .getAppointmentTime()
                                                )
                                        ) %>

                                    </div>

                                </div>


                            </div>

                        </div>


                        <!-- ==================================
                             CONFIRMATION FORM
                             ================================== -->

                        <form
                            method="post"
                            action="<%= request.getContextPath() %>/appointments/status">


                            <input
                                type="hidden"
                                name="appointmentId"
                                value="<%= appointment.getAppointmentId() %>">


                            <input
                                type="hidden"
                                name="action"
                                value="<%= escapeHtml(statusAction) %>">


                            <% if (cancelAction) { %>


                                <div class="form-group">


                                    <label
                                        for="cancellationReason"
                                        class="form-label">

                                        Cancellation Reason

                                        <span class="required">
                                            *
                                        </span>

                                    </label>


                                    <textarea
                                        id="cancellationReason"
                                        name="cancellationReason"
                                        maxlength="500"
                                        minlength="3"
                                        placeholder="Enter the reason the appointment is being cancelled..."
                                        required><%= escapeHtml(
                                                request.getParameter(
                                                        "cancellationReason"
                                                )
                                        ) %></textarea>


                                    <span class="form-help">

                                        Required for the clinic audit
                                        record. Maximum 500 characters.

                                    </span>

                                </div>


                            <% } else { %>


                                <div class="warning-box warning-complete">

                                    Confirm only when the scheduled
                                    dental visit has actually been
                                    completed.

                                    The status will change from

                                    <strong>
                                        SCHEDULED
                                    </strong>

                                    to

                                    <strong>
                                        COMPLETED
                                    </strong>.

                                </div>


                            <% } %>


                            <div class="actions">


                                <a
                                    class="button button-secondary"
                                    href="<%= request.getContextPath() %>/appointments/details?id=<%= appointment.getAppointmentId() %>">

                                    Go Back

                                </a>


                                <button
                                    type="submit"
                                    class="button
                                    <%= cancelAction
                                            ? "button-cancel"
                                            : "button-complete" %>">

                                    <%= buttonText %>

                                </button>


                            </div>

                        </form>

                    </div>

                </div>


            <% } else if (errorMessage == null) { %>


                <div class="alert alert-error">

                    Appointment information is unavailable.

                </div>


            <% } %>

        </div>

    </main>

</div>

</body>

</html>