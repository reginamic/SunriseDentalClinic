<%@page contentType="text/html" pageEncoding="UTF-8"%>

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

        if (value == null) {
            return "";
        }

        return String.valueOf(value);
    }
%>

<%
    /*
     * Logged-in user information.
     */
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

    /*
     * Server-side validation/API error.
     */
    String errorMessage =
            safeValue(
                    request.getAttribute("errorMessage")
            );

    /*
     * Preserve entered form values if
     * validation or API registration fails.
     */
    String enteredFullName =
            safeValue(
                    request.getAttribute("enteredFullName")
            );

    String enteredContactNumber =
            safeValue(
                    request.getAttribute("enteredContactNumber")
            );

    String enteredEmail =
            safeValue(
                    request.getAttribute("enteredEmail")
            );

    String enteredDateOfBirth =
            safeValue(
                    request.getAttribute("enteredDateOfBirth")
            );

    String enteredGender =
            safeValue(
                    request.getAttribute("enteredGender")
            );

    String enteredAddress =
            safeValue(
                    request.getAttribute("enteredAddress")
            );
%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Register Patient | Sunrise Dental Clinic
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

            grid-template-columns:
                250px 1fr;
        }

        /* =====================================
           SIDEBAR
           ===================================== */

        .sidebar {
            min-height: 100vh;

            background: #102a43;
            color: #ffffff;

            padding: 28px 18px;

            display: flex;
            flex-direction: column;
        }

        .brand {
            padding:
                0 10px 28px;

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

            transition:
                background 0.2s,
                color 0.2s;
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

            padding:
                20px 10px 0;

            border-top:
                1px solid
                rgba(255, 255, 255, 0.10);
        }

        .sidebar-footer a {
            color: #cbdbea;

            font-size: 12px;

            text-decoration: none;
        }

        .sidebar-footer a:hover {
            color: #ffffff;
        }

        /* =====================================
           MAIN
           ===================================== */

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

            padding:
                0 32px;
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

        /* =====================================
           CONTENT
           ===================================== */

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

        /* =====================================
           FORM PANEL
           ===================================== */

        .form-panel {
            background: #ffffff;

            border:
                1px solid #e2e8f0;

            border-radius: 12px;

            overflow: hidden;

            box-shadow:
                0 4px 14px
                rgba(15, 23, 42, 0.03);
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

            padding:
                0 12px;

            border:
                1px solid #cbd5e1;

            border-radius: 8px;

            background: #ffffff;
            color: #1e293b;

            outline: none;

            font-family: inherit;
            font-size: 13px;

            transition:
                border-color 0.2s,
                box-shadow 0.2s;
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

        .form-help {
            color: #94a3b8;

            font-size: 10px;

            margin-top: 5px;
        }

        /* =====================================
           ERROR MESSAGE
           ===================================== */

        .error-message {
            background: #fff5f5;

            border:
                1px solid #fecaca;

            border-left:
                4px solid #dc2626;

            color: #991b1b;

            padding: 12px 14px;

            border-radius: 8px;

            margin-bottom: 20px;

            font-size: 12px;
            line-height: 1.5;
        }

        /* =====================================
           INFORMATION
           ===================================== */

        .information-box {
            margin-top: 22px;

            background: #f0f6fc;

            border:
                1px solid #d8e7f5;

            border-radius: 8px;

            padding: 13px 15px;

            display: flex;
            gap: 10px;

            color: #475569;

            font-size: 11px;
            line-height: 1.6;
        }

        .information-icon {
            width: 20px;
            height: 20px;

            flex-shrink: 0;

            border-radius: 50%;

            background: #dcecf9;
            color: #1f5f99;

            display: flex;
            align-items: center;
            justify-content: center;

            font-weight: 700;
        }

        /* =====================================
           FORM ACTIONS
           ===================================== */

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

            padding:
                0 18px;

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
            border: none;

            background: #153e75;
            color: #ffffff;

            transition:
                background 0.2s,
                transform 0.1s;
        }

        .button-primary:hover {
            background: #102f59;
        }

        .button-primary:active {
            transform:
                translateY(1px);
        }

        /* =====================================
           RESPONSIVE
           ===================================== */

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
                padding:
                    0 18px;
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

    <!-- =====================================
         SIDEBAR
         ===================================== -->

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

    <!-- =====================================
         MAIN
         ===================================== -->

    <main class="main">

        <header class="topbar">

            <div class="page-title">

                <h1>
                    Patient Management
                </h1>

                <p>
                    Register new clinic patient
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

            <!-- BACK -->

            <a
                href="<%= request.getContextPath() %>/patients"
                class="back-link">

                &#8592;
                Back to Patients

            </a>

            <!-- PAGE HEADER -->

            <div class="page-header">

                <h2>
                    Register New Patient
                </h2>

                <p>
                    Enter the patient's personal and contact
                    information. A unique patient code will be
                    generated automatically by the system.
                </p>

            </div>

            <!-- SERVER ERROR -->

            <%
                if (!errorMessage.isBlank()) {
            %>

                <div
                    class="error-message"
                    role="alert">

                    <%= escapeHtml(errorMessage) %>

                </div>

            <%
                }
            %>

            <!-- =====================================
                 FORM
                 ===================================== -->

            <div class="form-panel">

                <div class="form-panel-header">

                    <h3>
                        Patient Information
                    </h3>

                    <p>
                        Fields marked with * are required.
                    </p>

                </div>

                <div class="form-body">

                    <form
                        method="post"
                        action="<%= request.getContextPath() %>/patients/new"
                        id="patientForm">

                        <div class="form-grid">

                            <!-- FULL NAME -->

                            <div class="form-group full-width">

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
                                    placeholder="Enter patient's full name"
                                    minlength="2"
                                    maxlength="150"
                                    autocomplete="name"
                                    value="<%= escapeHtml(enteredFullName) %>"
                                    required>

                                <span class="form-help">
                                    Enter the patient's legal or
                                    preferred full name.
                                </span>

                            </div>

                            <!-- CONTACT NUMBER -->

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
                                    placeholder="e.g. 0771234567"
                                    pattern="[0-9]{10,15}"
                                    minlength="10"
                                    maxlength="15"
                                    inputmode="numeric"
                                    autocomplete="tel"
                                    value="<%= escapeHtml(enteredContactNumber) %>"
                                    required>

                                <span class="form-help">
                                    Use 10–15 digits without spaces
                                    or special characters.
                                </span>

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
                                    placeholder="patient@example.com"
                                    maxlength="150"
                                    autocomplete="email"
                                    value="<%= escapeHtml(enteredEmail) %>">

                                <span class="form-help">
                                    Optional if the patient does
                                    not provide an email address.
                                </span>

                            </div>

                            <!-- DATE OF BIRTH -->

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
                                    value="<%= escapeHtml(enteredDateOfBirth) %>"
                                    required>

                                <span class="form-help">
                                    Future dates are not permitted.
                                </span>

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
                                        value=""
                                        disabled
                                        <%= enteredGender.isBlank()
                                                ? "selected"
                                                : "" %>>

                                        Select gender

                                    </option>

                                    <option
                                        value="MALE"
                                        <%= "MALE".equals(
                                                enteredGender
                                        )
                                                ? "selected"
                                                : "" %>>

                                        Male

                                    </option>

                                    <option
                                        value="FEMALE"
                                        <%= "FEMALE".equals(
                                                enteredGender
                                        )
                                                ? "selected"
                                                : "" %>>

                                        Female

                                    </option>

                                    <option
                                        value="OTHER"
                                        <%= "OTHER".equals(
                                                enteredGender
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
                                    placeholder="Enter patient's residential address"
                                    minlength="5"
                                    maxlength="300"
                                    autocomplete="street-address"
                                    required><%= escapeHtml(enteredAddress) %></textarea>

                            </div>

                        </div>

                        <!-- INFORMATION -->

                        <div class="information-box">

                            <span class="information-icon">
                                i
                            </span>

                            <span>
                                The patient code is generated
                                automatically by the Sunrise Dental
                                API. Staff should not manually create
                                or modify patient identifiers.
                            </span>

                        </div>

                        <!-- ACTIONS -->

                        <div class="form-actions">

                            <a
                                href="<%= request.getContextPath() %>/patients"
                                class="button button-secondary">

                                Cancel

                            </a>

                            <button
                                type="submit"
                                class="button button-primary"
                                id="savePatientButton">

                                Save Patient

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
     * Limit Date of Birth to today or earlier.
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

        const todayValue =
                year
                + "-"
                + month
                + "-"
                + day;

        dateOfBirth.max =
                todayValue;
    }

    /*
     * Contact number must contain
     * numeric characters only.
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
     * Prevent accidental double-clicking
     * after valid form submission.
     */
    const patientForm =
            document.getElementById(
                "patientForm"
            );

    const savePatientButton =
            document.getElementById(
                "savePatientButton"
            );

    if (patientForm
            && savePatientButton) {

        patientForm.addEventListener(
            "submit",
            function () {

                if (patientForm.checkValidity()) {

                    savePatientButton.disabled =
                            true;

                    savePatientButton.textContent =
                            "Saving...";
                }
            }
        );
    }

</script>

</body>

</html>