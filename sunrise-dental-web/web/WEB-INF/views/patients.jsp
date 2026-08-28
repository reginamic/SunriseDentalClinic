<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
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
%>

<%
    List<PatientViewModel> patients =
            (List<PatientViewModel>)
            request.getAttribute("patients");

    Integer patientCount =
            (Integer)
            request.getAttribute("patientCount");

    String errorMessage =
            (String)
            request.getAttribute("errorMessage");

    if (patients == null) {
        patients = Collections.emptyList();
    }

    if (patientCount == null) {
        patientCount = patients.size();
    }

    String fullName =
            String.valueOf(
                    session.getAttribute("fullName")
            );

    String role =
            String.valueOf(
                    session.getAttribute("role")
            );

    String username =
            String.valueOf(
                    session.getAttribute("username")
            );

    String avatarLetter = "U";

    if (username != null
            && !username.isBlank()
            && !"null".equals(username)) {

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
        Patients | Sunrise Dental Clinic
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
        }

        .page-header {
            display: flex;
            align-items: flex-start;
            justify-content: space-between;

            gap: 20px;

            margin-bottom: 24px;
        }

        .page-header h2 {
            color: #102a43;

            font-size: 24px;

            margin-bottom: 6px;
        }

        .page-header p {
            color: #64748b;

            font-size: 13px;
            line-height: 1.6;
        }

        .primary-button {
            min-height: 42px;

            padding:
                0 18px;

            border: none;
            border-radius: 8px;

            background: #153e75;
            color: #ffffff;

            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;

            text-decoration: none;

            font-size: 13px;
            font-weight: 600;

            cursor: pointer;

            transition:
                background 0.2s;
        }

        .primary-button:hover {
            background: #102f59;
        }

        /*
         * Registration will be enabled
         * in the next Patient Management step.
         */
        .primary-button.disabled {
            opacity: 0.65;
            cursor: default;
            pointer-events: none;
        }

        /* =====================================
           SUMMARY
           ===================================== */

        .summary-grid {
            display: grid;

            grid-template-columns:
                repeat(3, minmax(0, 1fr));

            gap: 16px;

            margin-bottom: 22px;
        }

        .summary-card {
            background: #ffffff;

            border:
                1px solid #e2e8f0;

            border-radius: 11px;

            padding: 19px;
        }

        .summary-label {
            color: #64748b;

            font-size: 11px;
            font-weight: 600;

            text-transform: uppercase;
            letter-spacing: 0.7px;

            margin-bottom: 8px;
        }

        .summary-value {
            color: #102a43;

            font-size: 25px;
            font-weight: 700;
        }

        .summary-text {
            color: #64748b;

            margin-top: 5px;

            font-size: 11px;
        }

        .status-value {
            color: #15803d;
        }

        /* =====================================
           ERROR
           ===================================== */

        .error-message {
            background: #fff5f5;

            border:
                1px solid #fecaca;

            border-left:
                4px solid #dc2626;

            color: #991b1b;

            padding: 13px 15px;

            border-radius: 8px;

            margin-bottom: 20px;

            font-size: 13px;
        }

        /* =====================================
           TABLE
           ===================================== */

        .table-panel {
            background: #ffffff;

            border:
                1px solid #e2e8f0;

            border-radius: 12px;

            overflow: hidden;
        }

        .table-toolbar {
            padding: 18px 20px;

            border-bottom:
                1px solid #e2e8f0;

            display: flex;
            align-items: center;
            justify-content: space-between;

            gap: 15px;
        }

        .table-toolbar h3 {
            color: #102a43;

            font-size: 15px;
        }

        .record-count {
            color: #64748b;

            font-size: 11px;

            margin-top: 4px;
        }

        /* =====================================
           SEARCH
           ===================================== */

        .search-box {
            position: relative;

            width: 315px;
        }

        .search-box input {
            width: 100%;
            height: 42px;

            padding:
                0 45px 0 13px;

            border:
                1px solid #cbd5e1;

            border-radius: 8px;

            background: #ffffff;
            color: #1e293b;

            outline: none;

            font-size: 12px;

            transition:
                border-color 0.2s,
                box-shadow 0.2s;
        }

        .search-box input:focus {
            border-color: #2563a6;

            box-shadow:
                0 0 0 3px
                rgba(37, 99, 166, 0.10);
        }

        .search-button {
            position: absolute;

            right: 6px;
            top: 50%;

            transform:
                translateY(-50%);

            width: 31px;
            height: 31px;

            border: none;
            border-radius: 6px;

            background: transparent;
            color: #64748b;

            display: flex;
            align-items: center;
            justify-content: center;

            cursor: pointer;

            font-size: 14px;

            transition:
                background 0.2s,
                color 0.2s;
        }

        .search-button:hover {
            background: #edf4fb;
            color: #1f5f99;
        }

        /* =====================================
           PATIENT TABLE
           ===================================== */

        .table-container {
            overflow-x: auto;
        }

        table {
            width: 100%;

            border-collapse: collapse;

            min-width: 900px;
        }

        thead {
            background: #f8fafc;
        }

        th {
            text-align: left;

            padding:
                13px 16px;

            color: #64748b;

            font-size: 10px;
            font-weight: 700;

            text-transform: uppercase;
            letter-spacing: 0.6px;

            border-bottom:
                1px solid #e2e8f0;
        }

        td {
            padding:
                15px 16px;

            border-bottom:
                1px solid #edf2f7;

            color: #334155;

            font-size: 12px;

            vertical-align: middle;
        }

        tbody tr:last-child td {
            border-bottom: none;
        }

        tbody tr:hover {
            background: #f8fbfe;
        }

        .patient-code {
            display: inline-block;

            padding:
                5px 8px;

            border-radius: 6px;

            background: #edf4fb;
            color: #1f5f99;

            font-weight: 700;

            font-size: 10px;
        }

        .patient-name strong {
            display: block;

            color: #1e293b;

            font-size: 12px;

            margin-bottom: 3px;
        }

        .patient-name span {
            color: #94a3b8;

            font-size: 10px;
        }

        .gender-badge {
            display: inline-block;

            padding:
                5px 9px;

            border-radius: 20px;

            background: #f1f5f9;
            color: #475569;

            font-size: 10px;
            font-weight: 600;
        }

        .action-button {
            border:
                1px solid #cbd5e1;

            background: #ffffff;
            color: #334155;

            border-radius: 6px;

            padding:
                6px 10px;

            font-size: 10px;
            font-weight: 600;

            cursor: default;
        }

        /* =====================================
           EMPTY / NO RESULT
           ===================================== */

        .empty-state {
            padding: 55px 20px;

            text-align: center;

            color: #64748b;
        }

        .empty-icon {
            font-size: 32px;

            margin-bottom: 12px;

            opacity: 0.6;
        }

        .empty-state h4 {
            color: #334155;

            margin-bottom: 5px;

            font-size: 14px;
        }

        .empty-state p {
            font-size: 11px;
        }

        .no-search-results {
            display: none;

            padding: 36px 20px;

            text-align: center;

            color: #64748b;

            border-top:
                1px solid #edf2f7;
        }

        .no-search-results strong {
            display: block;

            color: #334155;

            font-size: 13px;

            margin-bottom: 5px;
        }

        .no-search-results span {
            font-size: 11px;
        }

        /* =====================================
           RESPONSIVE
           ===================================== */

        @media (max-width: 1000px) {

            .summary-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 850px) {

            .layout {
                grid-template-columns: 1fr;
            }

            .sidebar {
                display: none;
            }

            .table-toolbar {
                align-items: stretch;
                flex-direction: column;
            }

            .search-box {
                width: 100%;
            }
        }

        @media (max-width: 600px) {

            .topbar {
                padding:
                    0 18px;
            }

            .content {
                padding: 20px;
            }

            .page-header {
                flex-direction: column;
            }

            .user-details {
                display: none;
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
                    Manage registered clinic patients
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

            <!-- =====================================
                 PAGE HEADER
                 ===================================== -->

            <div class="page-header">

                <div>

                    <h2>
                        Patients
                    </h2>

                    <p>
                        Register, review and maintain
                        patient information used by the
                        clinic appointment system.
                    </p>

                </div>

            <a
    href="<%= request.getContextPath() %>/patients/new"
    class="primary-button">

    <span>
        +
    </span>

    Register Patient

</a>

            </div>

            <!-- =====================================
                 SUMMARY
                 ===================================== -->

            <div class="summary-grid">

                <div class="summary-card">

                    <div class="summary-label">
                        Registered Patients
                    </div>

                    <div class="summary-value">
                        <%= patientCount %>
                    </div>

                    <div class="summary-text">
                        Patient records currently available
                    </div>

                </div>

                <div class="summary-card">

                    <div class="summary-label">
                        Data Source
                    </div>

                    <div class="summary-value">
                        REST
                    </div>

                    <div class="summary-text">
                        Retrieved through Sunrise Dental API
                    </div>

                </div>

                <div class="summary-card">

                    <div class="summary-label">
                        Service Status
                    </div>

                    <div class="summary-value status-value">

                        <%
                            if (errorMessage == null) {
                                out.print("Available");
                            } else {
                                out.print("Unavailable");
                            }
                        %>

                    </div>

                    <div class="summary-text">
                        Patient information service
                    </div>

                </div>

            </div>

            <!-- =====================================
                 ERROR MESSAGE
                 ===================================== -->

            <%
                if (errorMessage != null) {
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
                 PATIENT TABLE
                 ===================================== -->

            <div class="table-panel">

                <div class="table-toolbar">

                    <div>

                        <h3>
                            Patient Records
                        </h3>

                        <div
                            class="record-count"
                            id="recordCountText">

                            <%= patientCount %>
                            record<%= patientCount == 1 ? "" : "s" %>
                            found

                        </div>

                    </div>

                    <!-- SEARCH -->

                    <div class="search-box">

                        <input
                            type="text"
                            id="patientSearch"
                            placeholder="Search by code, name, contact or email..."
                            autocomplete="off"
                            aria-label="Search patients">

                        <button
                            type="button"
                            id="patientSearchButton"
                            class="search-button"
                            aria-label="Search patients"
                            title="Search patients">

                            &#128269;

                        </button>

                    </div>

                </div>

                <%
                    if (patients.isEmpty()) {
                %>

                    <div class="empty-state">

                        <div class="empty-icon">
                            &#128100;
                        </div>

                        <h4>
                            No patient records found
                        </h4>

                        <p>
                            Patient information will appear
                            here after registration.
                        </p>

                    </div>

                <%
                    } else {
                %>

                    <div class="table-container">

                        <table>

                            <thead>

                                <tr>

                                    <th>
                                        Patient Code
                                    </th>

                                    <th>
                                        Patient
                                    </th>

                                    <th>
                                        Contact
                                    </th>

                                    <th>
                                        Date of Birth
                                    </th>

                                    <th>
                                        Gender
                                    </th>

                                    <th>
                                        Address
                                    </th>

                                    <th>
                                        Action
                                    </th>

                                </tr>

                            </thead>

                            <tbody id="patientTableBody">

                            <%
                                for (PatientViewModel patient
                                        : patients) {
                            %>

                                <tr class="patient-row">

                                    <td>

                                        <span class="patient-code">

                                            <%= escapeHtml(
                                                    patient.getPatientCode()
                                            ) %>

                                        </span>

                                    </td>

                                    <td>

                                        <div class="patient-name">

                                            <strong>

                                                <%= escapeHtml(
                                                        patient.getFullName()
                                                ) %>

                                            </strong>

                                            <span>

                                                <%= escapeHtml(
                                                        patient.getEmail()
                                                ) %>

                                            </span>

                                        </div>

                                    </td>

                                    <td>

                                        <%= escapeHtml(
                                                patient.getContactNumber()
                                        ) %>

                                    </td>

                                    <td>

                                        <%= escapeHtml(
                                                patient.getDateOfBirth()
                                        ) %>

                                    </td>

                                    <td>

                                        <span class="gender-badge">

                                            <%= escapeHtml(
                                                    patient.getDisplayGender()
                                            ) %>

                                        </span>

                                    </td>

                                    <td>

                                        <%= escapeHtml(
                                                patient.getAddress()
                                        ) %>

                                    </td>

                                    <td>

                                      <a
    href="<%= request.getContextPath() %>/patients/edit?id=<%= patient.getPatientId() %>"
    class="action-button"
    style="text-decoration: none; cursor: pointer;">

    View / Edit

</a>

                                    </td>

                                </tr>

                            <%
                                }
                            %>

                            </tbody>

                        </table>

                    </div>

                    <!-- Shown only when search has zero matches -->

                    <div
                        id="noSearchResults"
                        class="no-search-results">

                        <strong>
                            No matching patients found
                        </strong>

                        <span>
                            Try another patient code,
                            name, contact number or email.
                        </span>

                    </div>

                <%
                    }
                %>

            </div>

        </section>

    </main>

</div>

<!-- =========================================
     PATIENT SEARCH SCRIPT

     IMPORTANT:
     This script is intentionally placed AFTER
     the HTML table so all DOM elements exist
     before JavaScript accesses them.
     ========================================= -->

<script>

(function () {

    const searchInput =
            document.getElementById(
                "patientSearch"
            );

    const searchButton =
            document.getElementById(
                "patientSearchButton"
            );

    const patientRows =
            document.querySelectorAll(
                "#patientTableBody .patient-row"
            );

    const recordCountText =
            document.getElementById(
                "recordCountText"
            );

    const noSearchResults =
            document.getElementById(
                "noSearchResults"
            );

    /*
     * Stop safely if the patient table
     * is not available.
     */
    if (!searchInput) {
        return;
    }

    function filterPatients() {

        const keyword =
                searchInput.value
                           .trim()
                           .toLowerCase();

        let visibleCount = 0;

        patientRows.forEach(
            function (row) {

                const rowText =
                        row.textContent
                           .replace(/\s+/g, " ")
                           .trim()
                           .toLowerCase();

                const matches =
                        keyword === ""
                        || rowText.includes(
                            keyword
                        );

                if (matches) {

                    row.style.display = "";

                    visibleCount++;

                } else {

                    row.style.display = "none";
                }
            }
        );

        /*
         * Update the record counter based
         * on the current search result.
         */
        if (recordCountText) {

            recordCountText.textContent =
                    visibleCount
                    + " record"
                    + (visibleCount === 1
                        ? ""
                        : "s")
                    + " found";
        }

        /*
         * Display a friendly message when
         * no patients match the search.
         */
        if (noSearchResults) {

            if (patientRows.length > 0
                    && visibleCount === 0) {

                noSearchResults.style.display =
                        "block";

            } else {

                noSearchResults.style.display =
                        "none";
            }
        }
    }

    /*
     * Live search while typing.
     */
    searchInput.addEventListener(
        "input",
        filterPatients
    );

    /*
     * Search using Enter key.
     */
    searchInput.addEventListener(
        "keydown",
        function (event) {

            if (event.key === "Enter") {

                event.preventDefault();

                filterPatients();
            }
        }
    );

    /*
     * Search using magnifying-glass button.
     */
    if (searchButton) {

        searchButton.addEventListener(
            "click",
            function () {

                filterPatients();

                searchInput.focus();
            }
        );
    }

})();

</script>

</body>

</html>