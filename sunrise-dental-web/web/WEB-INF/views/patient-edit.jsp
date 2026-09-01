<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.sunrisedental.web.model.PatientViewModel"%>

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

    private String safeValue(Object value) {

        return value == null
                ? ""
                : String.valueOf(value);
    }
%>

<%
    PatientViewModel patient =
            (PatientViewModel)
            request.getAttribute("patient");

    if (patient == null) {

        response.sendRedirect(
                request.getContextPath()
                + "/patients"
        );

        return;
    }

    String fullName =
            safeValue(
                    session.getAttribute("fullName")
            );

    String role =
            safeValue(
                    session.getAttribute("role")
            );

    String username =
            safeValue(
                    session.getAttribute("username")
            );

    String avatarLetter = "U";

    if (!username.isBlank()) {

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

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Edit Patient | Sunrise Dental Clinic
    </title>

    <style>

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            min-height: 100vh;

            font-family:
                "Segoe UI",
                Arial,
                sans-serif;

            background: #f4f7fb;
            color: #1e293b;
        }

        .layout {
            min-height: 100vh;

            display: grid;
            grid-template-columns: 250px 1fr;
        }

        /* SIDEBAR */

        .sidebar {
            min-height: 100vh;

            background: #102a43;
            color: #ffffff;

            padding: 28px 18px;

            display: flex;
            flex-direction: column;
        }

        .brand {
            padding: 0 10px 28px;

            border-bottom:
                1px solid
                rgba(255, 255, 255, 0.10);
        }

        .brand-mark {
            width: 46px;
            height: 46px;

            border-radius: 12px;

            background:
                rgba(255, 255, 255, 0.12);

            display: flex;
            align-items: center;
            justify-content: center;

            font-size: 23px;

            margin-bottom: 14px;
        }

        .brand h2 {
            font-size: 18px;
        }

        .brand p {
            color: #a9c1d8;

            margin-top: 5px;

            font-size: 11px;
        }

        .navigation {
            margin-top: 28px;

            display: grid;
            gap: 7px;
        }

        .navigation a {
            text-decoration: none;

            color: #cbdbea;

            padding: 12px 13px;

            border-radius: 8px;

            font-size: 13px;
            font-weight: 500;

            display: flex;
            align-items: center;
            gap: 11px;
        }

        .navigation a:hover {
            background:
                rgba(255, 255, 255, 0.08);

            color: #ffffff;
        }

        .navigation a.active {
            background: #1f5f99;
            color: #ffffff;
        }

        .nav-icon {
            width: 20px;
            text-align: center;
        }

        .sidebar-footer {
            margin-top: auto;

            padding: 20px 10px 0;

            border-top:
                1px solid
                rgba(255, 255, 255, 0.10);
        }

        .sidebar-footer a {
            color: #cbdbea;

            font-size: 12px;
            text-decoration: none;
        }

        /* MAIN */

        .main {
            min-width: 0;
        }

        .topbar {
            height: 74px;

            background: #ffffff;

            border-bottom:
                1px solid #e2e8f0;

            display: flex;
            align-items: center;
            justify-content: space-between;

            padding: 0 32px;
        }

        .page-title h1 {
            color: #102a43;

            font-size: 21px;
        }

        .page-title p {
            color: #64748b;

            font-size: 12px;

            margin-top: 3px;
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

            font-size: 13px;
        }

        .user-details span {
            color: #64748b;

            font-size: 11px;
        }

        .avatar {
            width: 40px;
            height: 40px;

            border-radius: 50%;

            background: #e6eef8;
            color: #153e75;

            display: flex;
            align-items: center;
            justify-content: center;

            font-size: 14px;
            font-weight: 700;
        }

        /* CONTENT */

        .content {
            padding: 32px;

            max-width: 1100px;
        }

        .back-link {
            display: inline-flex;
            align-items: center;
            gap: 6px;

            color: #475569;

            text-decoration: none;

            font-size: 12px;
            font-weight: 600;

            margin-bottom: 20px;
        }

        .back-link:hover {
            color: #153e75;
        }

        .page-header {
            margin-bottom: 24px;
        }

        .page-header h2 {
            color: #102a43;

            font-size: 25px;

            margin-bottom: 6px;
        }

        .page-header p {
            color: #64748b;

            font-size: 13px;
            line-height: 1.6;
        }

        /* PATIENT IDENTITY */

        .patient-summary {
            background:
                linear-gradient(
                    135deg,
                    #153e75,
                    #1f5f99
                );

            color: #ffffff;

            border-radius: 12px;

            padding: 20px 24px;

            margin-bottom: 22px;

            display: flex;
            align-items: center;
            justify-content: space-between;

            gap: 20px;
        }

        .patient-summary h3 {
            font-size: 18px;

            margin-bottom: 5px;
        }

        .patient-summary p {
            color: #dbeafe;

            font-size: 11px;
        }

        .patient-code {
            background:
                rgba(255, 255, 255, 0.14);

            border:
                1px solid
                rgba(255, 255, 255, 0.16);

            border-radius: 8px;

            padding: 8px 12px;

            font-size: 12px;
            font-weight: 700;
        }

        /* FORM */

        .form-panel {
            background: #ffffff;

            border:
                1px solid #e2e8f0;

            border-radius: 12px;

            overflow: hidden;
        }

        .form-panel-header {
            padding: 20px 24px;

            border-bottom:
                1px solid #e2e8f0;

            background: #f8fafc;
        }

        .form-panel-header h3 {
            color: #102a43;

            font-size: 16px;

            margin-bottom: 4px;
        }

        .form-panel-header p {
            color: #64748b;

            font-size: 11px;
        }

        .form-body {
            padding: 26px;
        }

        .form-grid {
            display: grid;

            grid-template-columns:
                repeat(2, minmax(0, 1fr));

            gap:
                20px 22px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        .form-group.full-width {
            grid-column:
                1 / -1;
        }

        .form-group label {
            color: #334155;

            font-size: 12px;
            font-weight: 600;

            margin-bottom: 7px;
        }

        .required {
            color: #dc2626;
        }

        .form-control {
            width: 100%;
            height: 44px;

            padding: 0 12px;

            border:
                1px solid #cbd5e1;

            border-radius: 8px;

            background: #ffffff;
            color: #1e293b;

            outline: none;

            font-family: inherit;
            font-size: 13px;
        }

        textarea.form-control {
            height: 95px;

            padding: 12px;

            resize: vertical;
        }

        .form-control:focus {
            border-color: #2563a6;

            box-shadow:
                0 0 0 3px
                rgba(37, 99, 166, 0.10);
        }

        .readonly-control {
            background: #f8fafc;
            color: #64748b;

            cursor: not-allowed;
        }

        .form-help {
            color: #94a3b8;

            font-size: 10px;

            margin-top: 5px;
        }

        /* ACTIONS */

        .form-actions {
            margin-top: 26px;

            padding-top: 20px;

            border-top:
                1px solid #e2e8f0;

            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }

        .button {
            min-height: 42px;

            padding: 0 18px;

            border-radius: 8px;

            font-family: inherit;

            font-size: 12px;
            font-weight: 600;

            display: inline-flex;
            align-items: center;
            justify-content: center;

            text-decoration: none;

            cursor: pointer;
        }

        .button-secondary {
            background: #ffffff;
            color: #334155;

            border:
                1px solid #cbd5e1;
        }

        .button-secondary:hover {
            background: #f8fafc;
        }

        .button-primary {
            background: #153e75;
            color: #ffffff;

            border: none;
        }

        .button-primary:disabled {
            opacity: 0.60;

            cursor: not-allowed;
        }

        /* RESPONSIVE */

        @media (max-width: 850px) {

            .layout {
                grid-template-columns: 1fr;
            }

            .sidebar {
                display: none;
            }
        }

        @media (max-width: 650px) {

            .content {
                padding: 20px;
            }

            .topbar {
                padding: 0 18px;
            }

            .form-grid {
                grid-template-columns: 1fr;
            }

            .form-group.full-width {
                grid-column: auto;
            }

            .user-details {
                display: none;
            }

            .patient-summary {
                align-items: flex-start;
                flex-direction: column;
            }

            .form-actions {
                flex-direction: column-reverse;
            }

            .button {
                width: 100%;
            }
        }

    </style>

</head>

<body>

<div class="layout">

    <!-- SIDEBAR -->

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

            <a
                class="active"
                href="<%= request.getContextPath() %>/patients">

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

        </nav>

        <div class="sidebar-footer">

            <a href="<%= request.getContextPath() %>/logout">
                Sign out
            </a>

        </div>

    </aside>

    <!-- MAIN -->

    <main class="main">

        <header class="topbar">

            <div class="page-title">

                <h1>
                    Patient Management
                </h1>

                <p>
                    Review and update patient information
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

            <a
                href="<%= request.getContextPath() %>/patients"
                class="back-link">

                &#8592;
                Back to Patients

            </a>

            <div class="page-header">

                <h2>
                    View / Edit Patient
                </h2>

                <p>
                    Review the selected patient's information.
                    The system-generated patient identifier
                    cannot be changed.
                </p>

            </div>

            <!-- PATIENT SUMMARY -->

            <div class="patient-summary">

                <div>

                    <h3>
                        <%= escapeHtml(
                                patient.getFullName()
                        ) %>
                    </h3>

                    <p>
                        Patient record #<%= patient.getPatientId() %>
                    </p>

                </div>

                <div class="patient-code">

                    <%= escapeHtml(
                            patient.getPatientCode()
                    ) %>

                </div>

            </div>

            <!-- EDIT FORM -->

            <div class="form-panel">

                <div class="form-panel-header">

                    <h3>
                        Patient Information
                    </h3>

                    <p>
                        Review or update patient details.
                    </p>

                </div>

                <div class="form-body">

                    <form
                        method="post"
                        action="<%= request.getContextPath() %>/patients/edit"
                        id="patientEditForm">

                        <input
                            type="hidden"
                            name="patientId"
                            value="<%= patient.getPatientId() %>">

                        <div class="form-grid">

                            <!-- PATIENT CODE -->

                            <div class="form-group">

                                <label for="patientCode">
                                    Patient Code
                                </label>

                                <input
                                    type="text"
                                    id="patientCode"
                                    class="form-control readonly-control"
                                    value="<%= escapeHtml(
                                            patient.getPatientCode()
                                    ) %>"
                                    readonly>

                                <span class="form-help">
                                    Generated automatically by the system.
                                </span>

                            </div>

                            <!-- FULL NAME -->

                            <div class="form-group">

                                <label for="fullName">

                                    Full Name

                                    <span class="required">
                                        *
                                    </span>

                                </label>

                                <input
                                    type="text"
                                    id="fullName"
                                    name="fullName"
                                    class="form-control"
                                    minlength="2"
                                    maxlength="150"
                                    value="<%= escapeHtml(
                                            patient.getFullName()
                                    ) %>"
                                    required>

                            </div>

                            <!-- CONTACT -->

                            <div class="form-group">

                                <label for="contactNumber">

                                    Contact Number

                                    <span class="required">
                                        *
                                    </span>

                                </label>

                                <input
                                    type="tel"
                                    id="contactNumber"
                                    name="contactNumber"
                                    class="form-control"
                                    pattern="[0-9]{10,15}"
                                    minlength="10"
                                    maxlength="15"
                                    inputmode="numeric"
                                    value="<%= escapeHtml(
                                            patient.getContactNumber()
                                    ) %>"
                                    required>

                            </div>

                            <!-- EMAIL -->

                            <div class="form-group">

                                <label for="email">
                                    Email Address
                                </label>

                                <input
                                    type="email"
                                    id="email"
                                    name="email"
                                    class="form-control"
                                    maxlength="150"
                                    value="<%= escapeHtml(
                                            patient.getEmail()
                                    ) %>">

                            </div>

                            <!-- DOB -->

                            <div class="form-group">

                                <label for="dateOfBirth">

                                    Date of Birth

                                    <span class="required">
                                        *
                                    </span>

                                </label>

                                <input
                                    type="date"
                                    id="dateOfBirth"
                                    name="dateOfBirth"
                                    class="form-control"
                                    value="<%= escapeHtml(
                                            patient.getDateOfBirth()
                                    ) %>"
                                    required>

                            </div>

                            <!-- GENDER -->

                            <div class="form-group">

                                <label for="gender">

                                    Gender

                                    <span class="required">
                                        *
                                    </span>

                                </label>

                                <select
                                    id="gender"
                                    name="gender"
                                    class="form-control"
                                    required>

                                    <option
                                        value="MALE"
                                        <%= "MALE".equals(
                                                patient.getGender()
                                        )
                                                ? "selected"
                                                : "" %>>

                                        Male

                                    </option>

                                    <option
                                        value="FEMALE"
                                        <%= "FEMALE".equals(
                                                patient.getGender()
                                        )
                                                ? "selected"
                                                : "" %>>

                                        Female

                                    </option>

                                    <option
                                        value="OTHER"
                                        <%= "OTHER".equals(
                                                patient.getGender()
                                        )
                                                ? "selected"
                                                : "" %>>

                                        Other

                                    </option>

                                </select>

                            </div>

                            <!-- ADDRESS -->

                            <div class="form-group full-width">

                                <label for="address">

                                    Address

                                    <span class="required">
                                        *
                                    </span>

                                </label>

                                <textarea
                                    id="address"
                                    name="address"
                                    class="form-control"
                                    minlength="5"
                                    maxlength="300"
                                    required><%= escapeHtml(
                                            patient.getAddress()
                                    ) %></textarea>

                            </div>

                        </div>

                        <div class="form-actions">

                            <a
                                href="<%= request.getContextPath() %>/patients"
                                class="button button-secondary">

                                Cancel

                            </a>

                           <button
    type="submit"
    class="button button-primary"
    id="saveChangesButton">

    Save Changes

</button>

                        </div>

                    </form>

                </div>

            </div>

        </section>

    </main>

</div>

<script>

    /*
     * Prevent future dates of birth.
     */
    const dateOfBirth =
            document.getElementById(
                "dateOfBirth"
            );

    if (dateOfBirth) {

        const today =
                new Date();

        const year =
                today.getFullYear();

        const month =
                String(
                    today.getMonth() + 1
                ).padStart(
                    2,
                    "0"
                );

        const day =
                String(
                    today.getDate()
                ).padStart(
                    2,
                    "0"
                );

        dateOfBirth.max =
                year
                + "-"
                + month
                + "-"
                + day;
    }

    /*
     * Contact number accepts digits only.
     */
    const contactNumber =
            document.getElementById(
                "contactNumber"
            );

    if (contactNumber) {

        contactNumber.addEventListener(
            "input",
            function () {

                this.value =
                        this.value.replace(
                            /[^0-9]/g,
                            ""
                        );
            }
        );
    }
    
    
    
    /*
 * Prevent accidental duplicate updates.
 */
const patientEditForm =
        document.getElementById(
            "patientEditForm"
        );

const saveChangesButton =
        document.getElementById(
            "saveChangesButton"
        );

if (patientEditForm
        && saveChangesButton) {

    patientEditForm.addEventListener(
        "submit",
        function () {

            if (patientEditForm.checkValidity()) {

                saveChangesButton.disabled =
                        true;

                saveChangesButton.textContent =
                        "Saving...";
            }
        }
    );
}

</script>

</body>

</html>