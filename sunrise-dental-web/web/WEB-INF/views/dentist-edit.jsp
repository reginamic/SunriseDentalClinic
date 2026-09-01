<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.sunrisedental.web.model.DentistViewModel"%>

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
    DentistViewModel dentist =
            (DentistViewModel)
            request.getAttribute("dentist");

    if (dentist == null) {

        response.sendRedirect(
                request.getContextPath()
                + "/dentists"
        );

        return;
    }

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

    String avatarLetter = "U";

    if (!username.isBlank()) {

        avatarLetter =
                username.substring(0, 1)
                        .toUpperCase();
    }
%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Edit Dentist | Sunrise Dental Clinic
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

        /* ==============================
           SIDEBAR
           ============================== */

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

        /* ==============================
           MAIN
           ============================== */

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

        /* ==============================
           CONTENT
           ============================== */

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
            margin-bottom: 22px;
        }

        .content-header h2 {
            color: #102a43;

            font-size: 25px;

            margin-bottom: 6px;
        }

        .content-header p {
            color: #64748b;

            font-size: 12px;
        }

        /* ==============================
           DENTIST SUMMARY
           ============================== */

        .dentist-summary {
            background:
                linear-gradient(
                    135deg,
                    #153e75,
                    #2467a3
                );

            color: #ffffff;

            padding: 20px 24px;

            border-radius: 12px;

            margin-bottom: 22px;

            display: flex;
            align-items: center;
            justify-content: space-between;

            gap: 20px;
        }

        .dentist-summary h3 {
            font-size: 18px;

            margin-bottom: 5px;
        }

        .dentist-summary p {
            color: #dbeafe;

            font-size: 11px;
        }

        .dentist-code {
            background:
                rgba(255, 255, 255, 0.14);

            border:
                1px solid
                rgba(255, 255, 255, 0.18);

            border-radius: 8px;

            padding: 9px 13px;

            font-size: 12px;
            font-weight: 700;
        }

        /* ==============================
           FORM
           ============================== */

        .form-panel {
            background: #ffffff;

            border:
                1px solid #e2e8f0;

            border-radius: 12px;

            overflow: hidden;
        }

        .form-panel-header {
            padding: 20px 24px;

            background: #f8fafc;

            border-bottom:
                1px solid #e2e8f0;
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

        /* ==============================
           STATUS
           ============================== */

        .status-information {
            margin-top: 22px;

            padding: 15px 17px;

            background: #f8fafc;

            border:
                1px solid #e2e8f0;

            border-radius: 9px;
        }

        .status-information strong {
            color: #334155;

            font-size: 11px;
        }

        .status-information p {
            color: #64748b;

            margin-top: 5px;

            font-size: 10px;
            line-height: 1.6;
        }

        /* ==============================
           BUTTONS
           ============================== */

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

        .button-primary:disabled {
            opacity: 0.60;

            cursor: not-allowed;
        }

        /* ==============================
           RESPONSIVE
           ============================== */

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

            .dentist-summary {
                flex-direction: column;
                align-items: flex-start;
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
                    Review and maintain clinical staff records
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

                <h2>
                    View / Edit Dentist
                </h2>

                <p>
                    Review and update the selected
                    dentist's clinical information.
                </p>

            </div>


            <!-- DENTIST SUMMARY -->

            <div class="dentist-summary">

                <div>

                    <h3>
                        <%= escapeHtml(
                                dentist.getFullName()
                        ) %>
                    </h3>

                    <p>
                        <%= escapeHtml(
                                dentist.getSpecialization()
                        ) %>
                    </p>

                </div>

                <div class="dentist-code">

                    <%= escapeHtml(
                            dentist.getDentistCode()
                    ) %>

                </div>

            </div>


            <!-- EDIT FORM -->

            <div class="form-panel">

                <div class="form-panel-header">

                    <h3>
                        Dentist Information
                    </h3>

                    <p>
                        The system-generated dentist code
                        cannot be modified.
                    </p>

                </div>


                <div class="form-body">

                    <form
                        method="post"
                        action="<%= request.getContextPath() %>/dentists/edit"
                        id="dentistEditForm">


                        <input
                            type="hidden"
                            name="dentistId"
                            value="<%= dentist.getDentistId() %>">


                        <div class="form-grid">


                            <!-- DENTIST CODE -->

                            <div class="form-group">

                                <label>
                                    Dentist Code
                                </label>

                                <input
                                    type="text"
                                    class="form-control readonly-control"
                                    value="<%= escapeHtml(
                                            dentist.getDentistCode()
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
                                    minlength="3"
                                    maxlength="120"
                                    value="<%= escapeHtml(
                                            dentist.getFullName()
                                    ) %>"
                                    required>

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
                                    value="<%= escapeHtml(
                                            dentist.getSpecialization()
                                    ) %>"
                                    required>

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
                                    maxlength="15"
                                    inputmode="numeric"
                                    value="<%= escapeHtml(
                                            dentist.getContactNumber()
                                    ) %>">

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
                                            dentist.getEmail()
                                    ) %>">

                            </div>


                            <!-- STATUS -->

                            <div class="form-group">

                                <label for="active">

                                    Account Status

                                    <span class="required">
                                        *
                                    </span>

                                </label>

                                <select
                                    id="active"
                                    name="active"
                                    class="form-control"
                                    required>

                                    <option
                                        value="true"
                                        <%= dentist.isActive()
                                                ? "selected"
                                                : "" %>>

                                        Active

                                    </option>

                                    <option
                                        value="false"
                                        <%= !dentist.isActive()
                                                ? "selected"
                                                : "" %>>

                                        Inactive

                                    </option>

                                </select>

                            </div>

                        </div>


                        <div class="status-information">

                            <strong>
                                Dentist status control
                            </strong>

                            <p>
                                Inactive dentist records remain available
                                for historical information but should not
                                be used for new appointment scheduling.
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
    id="saveDentistChangesButton">

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
 * Prevent accidental duplicate update requests.
 */
const dentistEditForm =
        document.getElementById(
            "dentistEditForm"
        );

const saveDentistChangesButton =
        document.getElementById(
            "saveDentistChangesButton"
        );

if (dentistEditForm
        && saveDentistChangesButton) {

    dentistEditForm.addEventListener(
        "submit",
        function () {

            if (dentistEditForm.checkValidity()) {

                saveDentistChangesButton.disabled =
                        true;

                saveDentistChangesButton.textContent =
                        "Saving...";
            }
        }
    );
}

</script>

</body>

</html>