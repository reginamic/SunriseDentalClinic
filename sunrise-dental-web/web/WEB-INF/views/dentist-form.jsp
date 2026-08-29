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

        return value == null
                ? ""
                : String.valueOf(value);
    }
%>

<%
    String fullName =
            safeValue(
                    session.getAttribute("fullName")
            );

    String username =
            safeValue(
                    session.getAttribute("username")
            );

    String role =
            safeValue(
                    session.getAttribute("role")
            );

    String errorMessage =
            (String)
            request.getAttribute("errorMessage");

    String fullNameValue =
            safeValue(
                    request.getAttribute("fullNameValue")
            );

    String specializationValue =
            safeValue(
                    request.getAttribute("specializationValue")
            );

    String contactNumberValue =
            safeValue(
                    request.getAttribute("contactNumberValue")
            );

    String emailValue =
            safeValue(
                    request.getAttribute("emailValue")
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
        Register Dentist | Sunrise Dental Clinic
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
            padding: 30px 32px;

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

        .content-header {
            display: flex;

            align-items: flex-start;
            justify-content: space-between;

            gap: 20px;

            margin-bottom: 24px;
        }

        .content-header h2 {
            color: #102a43;

            font-size: 25px;

            margin-bottom: 6px;
        }

        .content-header p {
            color: #64748b;

            font-size: 12px;
            line-height: 1.6;
        }

        .admin-badge {
            background: #eff6ff;
            color: #1e40af;

            border:
                1px solid #bfdbfe;

            border-radius: 8px;

            padding: 8px 12px;

            font-size: 11px;
            font-weight: 700;

            white-space: nowrap;
        }

        /* ERROR */

        .error-message {
            background: #fef2f2;
            color: #b91c1c;

            border:
                1px solid #fecaca;

            border-radius: 8px;

            padding: 13px 15px;

            margin-bottom: 20px;

            font-size: 12px;
        }

        /* FORM PANEL */

        .form-panel {
            background: #ffffff;

            border:
                1px solid #e2e8f0;

            border-radius: 12px;

            overflow: hidden;
        }

        .form-panel-header {
            background: #f8fafc;

            border-bottom:
                1px solid #e2e8f0;

            padding: 20px 24px;
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

            gap: 21px 22px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        .form-group.full-width {
            grid-column: 1 / -1;
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

            font-family: inherit;
            font-size: 13px;

            outline: none;
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

        /* INFO PANEL */

        .info-panel {
            margin-top: 22px;

            background: #f8fafc;

            border:
                1px solid #e2e8f0;

            border-radius: 9px;

            padding: 15px 17px;
        }

        .info-panel strong {
            color: #334155;

            font-size: 11px;
        }

        .info-panel p {
            color: #64748b;

            margin-top: 4px;

            font-size: 10px;
            line-height: 1.6;
        }

        /* BUTTONS */

        .form-actions {
            margin-top: 25px;

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

            display: inline-flex;
            align-items: center;
            justify-content: center;

            text-decoration: none;

            font-family: inherit;
            font-size: 12px;
            font-weight: 600;

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

        .button-primary:hover {
            background: #102f57;
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

            .content-header {
                flex-direction: column;
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

            <a href="<%= request.getContextPath() %>/patients">

                <span class="nav-icon">
                    &#128100;
                </span>

                Patients

            </a>

            <a
                class="active"
                href="<%= request.getContextPath() %>/dentists">

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
                    Dentist Management
                </h1>

                <p>
                    Register clinical staff records
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
                href="<%= request.getContextPath() %>/dentists"
                class="back-link">

                &#8592;
                Back to Dentists

            </a>


            <div class="content-header">

                <div>

                    <h2>
                        Register Dentist
                    </h2>

                    <p>
                        Add a new dentist to the Sunrise Dental
                        clinical staff directory.
                    </p>

                </div>

                <div class="admin-badge">
                    ADMIN ONLY
                </div>

            </div>


            <% if (errorMessage != null
                    && !errorMessage.isBlank()) { %>

                <div class="error-message">

                    <%= escapeHtml(errorMessage) %>

                </div>

            <% } %>


            <div class="form-panel">

                <div class="form-panel-header">

                    <h3>
                        Dentist Information
                    </h3>

                    <p>
                        The dentist code will be generated
                        automatically by the system.
                    </p>

                </div>


                <div class="form-body">

                    <form
                        method="post"
                        action="<%= request.getContextPath() %>/dentists/new"
                        id="dentistForm">


                        <div class="form-grid">


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
                                    minlength="3"
                                    maxlength="120"
                                    placeholder="e.g. Dr. Amaya Fernando"
                                    value="<%= escapeHtml(fullNameValue) %>"
                                    required>

                                <span class="form-help">
                                    Include professional title where appropriate.
                                </span>

                            </div>


                            <!-- SPECIALIZATION -->

                            <div class="form-group">

                                <label for="specialization">

                                    Specialization

                                    <span class="required">
                                        *
                                    </span>

                                </label>

                                <input
                                    type="text"
                                    id="specialization"
                                    name="specialization"
                                    class="form-control"
                                    minlength="3"
                                    maxlength="120"
                                    placeholder="e.g. Periodontics"
                                    value="<%= escapeHtml(specializationValue) %>"
                                    required>

                                <span class="form-help">
                                    Dentist's primary clinical specialization.
                                </span>

                            </div>


                            <!-- CONTACT -->

                            <div class="form-group">

                                <label for="contactNumber">
                                    Contact Number
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
                                    placeholder="0771234567"
                                    value="<%= escapeHtml(contactNumberValue) %>">

                                <span class="form-help">
                                    Optional. Use 10–15 digits only.
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
                                    maxlength="150"
                                    placeholder="dentist@sunrisedental.lk"
                                    value="<%= escapeHtml(emailValue) %>">

                                <span class="form-help">
                                    Optional professional contact email.
                                </span>

                            </div>

                        </div>


                        <div class="info-panel">

                            <strong>
                                System controlled fields
                            </strong>

                            <p>
                                A unique dentist code will be generated
                                automatically. Newly registered dentist
                                records are managed through the Sunrise
                                Dental REST API and stored in the clinic
                                database.
                            </p>

                        </div>


                        <div class="form-actions">

                            <a
                                href="<%= request.getContextPath() %>/dentists"
                                class="button button-secondary">

                                Cancel

                            </a>

                            <button
                                type="submit"
                                class="button button-primary"
                                id="saveDentistButton">

                                Register Dentist

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
     * Prevent accidental duplicate submissions.
     */
    const dentistForm =
            document.getElementById(
                "dentistForm"
            );

    const saveDentistButton =
            document.getElementById(
                "saveDentistButton"
            );

    if (dentistForm
            && saveDentistButton) {

        dentistForm.addEventListener(
            "submit",
            function () {

                if (dentistForm.checkValidity()) {

                    saveDentistButton.disabled =
                            true;

                    saveDentistButton.textContent =
                            "Registering...";
                }
            }
        );
    }

</script>

</body>

</html>