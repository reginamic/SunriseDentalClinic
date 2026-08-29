<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
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
    List<DentistViewModel> dentists =
            (List<DentistViewModel>)
            request.getAttribute("dentists");

    Integer dentistCount =
            (Integer)
            request.getAttribute("dentistCount");

    Long activeDentistCount =
            (Long)
            request.getAttribute("activeDentistCount");

    String errorMessage =
            (String)
            request.getAttribute("errorMessage");

    if (dentists == null) {
        dentists = java.util.Collections.emptyList();
    }

    if (dentistCount == null) {
        dentistCount = dentists.size();
    }

    if (activeDentistCount == null) {
        activeDentistCount = 0L;
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

    boolean isAdmin =
            "ADMIN".equalsIgnoreCase(role);

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
        Dentist Management | Sunrise Dental Clinic
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

            font-size: 22px;

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

            font-weight: 700;
            font-size: 14px;
        }

        /* ==============================
           CONTENT
           ============================== */

        .content {
            padding: 30px 32px;
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

            font-size: 24px;

            margin-bottom: 6px;
        }

        .content-header p {
            color: #64748b;

            font-size: 12px;
            line-height: 1.6;
        }

        .role-badge {
            background: #eaf2fb;
            color: #153e75;

            border:
                1px solid #d5e5f5;

            padding: 9px 13px;

            border-radius: 8px;

            font-size: 11px;
            font-weight: 700;
        }
        
        
        
        
        .header-actions {
    display: flex;
    align-items: center;
    gap: 10px;
}

.register-button {
    min-height: 40px;

    padding: 0 16px;

    background: #153e75;
    color: #ffffff;

    border: none;
    border-radius: 8px;

    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 7px;

    text-decoration: none;

    font-size: 12px;
    font-weight: 600;

    transition: 0.2s ease;
}

.register-button:hover {
    background: #102f57;
}

        /* ==============================
           STAT CARDS
           ============================== */

        .stats-grid {
            display: grid;

            grid-template-columns:
                repeat(3, minmax(0, 1fr));

            gap: 16px;

            margin-bottom: 22px;
        }

        .stat-card {
            background: #ffffff;

            border:
                1px solid #e2e8f0;

            border-radius: 11px;

            padding: 19px 20px;
        }

        .stat-label {
            color: #64748b;

            font-size: 11px;
            font-weight: 600;

            margin-bottom: 9px;
        }

        .stat-value {
            color: #102a43;

            font-size: 25px;
            font-weight: 700;
        }

        .stat-detail {
            color: #94a3b8;

            font-size: 10px;

            margin-top: 5px;
        }

        .connection-value {
            font-size: 16px;
        }

        .connected {
            color: #15803d;
        }

        /* ==============================
           MANAGEMENT NOTICE
           ============================== */

        .management-notice {
            margin-bottom: 20px;

            padding: 13px 16px;

            border-radius: 9px;

            font-size: 11px;
            line-height: 1.6;
        }

        .admin-notice {
            background: #eff6ff;
            color: #1e40af;

            border:
                1px solid #bfdbfe;
        }

        .reception-notice {
            background: #f8fafc;
            color: #475569;

            border:
                1px solid #e2e8f0;
        }

        .error-message {
            background: #fef2f2;
            color: #b91c1c;

            border:
                1px solid #fecaca;

            padding: 13px 16px;

            border-radius: 8px;

            font-size: 12px;

            margin-bottom: 20px;
        }

        /* ==============================
           TABLE PANEL
           ============================== */

        .table-panel {
            background: #ffffff;

            border:
                1px solid #e2e8f0;

            border-radius: 11px;

            overflow: hidden;
        }

        .table-toolbar {
            padding: 17px 20px;

            border-bottom:
                1px solid #e2e8f0;

            display: flex;
            align-items: center;
            justify-content: space-between;

            gap: 16px;
        }

        .table-toolbar h3 {
            color: #102a43;

            font-size: 15px;
        }

        .search-box {
            width: 320px;
            max-width: 100%;

            position: relative;
        }

        .search-box input {
            width: 100%;
            height: 39px;

            padding:
                0 38px
                0 12px;

            border:
                1px solid #cbd5e1;

            border-radius: 8px;

            outline: none;

            font-family: inherit;
            font-size: 12px;
        }

        .search-box input:focus {
            border-color: #2563a6;

            box-shadow:
                0 0 0 3px
                rgba(37, 99, 166, 0.10);
        }

        .search-icon {
            position: absolute;

            right: 12px;
            top: 50%;

            transform:
                translateY(-50%);

            color: #94a3b8;

            font-size: 14px;
        }

        .table-wrapper {
            width: 100%;

            overflow-x: auto;
        }

        table {
            width: 100%;

            border-collapse: collapse;
        }

        th {
            background: #f8fafc;
            color: #64748b;

            text-align: left;

            font-size: 10px;
            font-weight: 700;

            text-transform: uppercase;
            letter-spacing: 0.04em;

            padding: 13px 16px;

            border-bottom:
                1px solid #e2e8f0;
        }

        td {
            padding: 15px 16px;

            border-bottom:
                1px solid #edf2f7;

            font-size: 12px;

            vertical-align: middle;
        }

        tbody tr:hover {
            background: #f8fbff;
        }

        tbody tr:last-child td {
            border-bottom: none;
        }

        .dentist-code {
            color: #153e75;

            font-weight: 700;

            white-space: nowrap;
        }

        .dentist-name {
            color: #1e293b;

            font-weight: 600;
        }

        .specialization {
            color: #475569;
        }

        .secondary-text {
            color: #64748b;

            font-size: 11px;
        }

        .status {
            display: inline-flex;
            align-items: center;
            gap: 5px;

            border-radius: 20px;

            padding: 5px 9px;

            font-size: 10px;
            font-weight: 700;
        }

        .status-active {
            background: #ecfdf5;
            color: #047857;
        }

        .status-inactive {
            background: #f1f5f9;
            color: #64748b;
        }

        .status-dot {
            width: 6px;
            height: 6px;

            border-radius: 50%;

            background: currentColor;
        }

        .no-results {
            display: none;

            text-align: center;

            padding: 30px;

            color: #64748b;

            font-size: 12px;
        }

        .empty-state {
            text-align: center;

            padding: 35px;

            color: #64748b;

            font-size: 12px;
        }

        /* ==============================
           FOOTER INFO
           ============================== */

        .table-footer {
            border-top:
                1px solid #e2e8f0;

            background: #f8fafc;

            padding: 11px 16px;

            display: flex;
            justify-content: space-between;

            color: #64748b;

            font-size: 10px;
        }
        
        
        
        .edit-button {
    display: inline-flex;
    align-items: center;
    justify-content: center;

    min-height: 32px;

    padding: 0 11px;

    background: #ffffff;
    color: #153e75;

    border: 1px solid #cbd5e1;
    border-radius: 7px;

    text-decoration: none;

    font-size: 10px;
    font-weight: 600;

    white-space: nowrap;
}

.edit-button:hover {
    background: #eff6ff;
    border-color: #93c5fd;
}

        /* ==============================
           RESPONSIVE
           ============================== */

        @media (max-width: 950px) {

            .stats-grid {
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
        }

        @media (max-width: 650px) {

            .content {
                padding: 20px;
            }

            .topbar {
                padding: 0 18px;
            }

            .content-header,
            .table-toolbar {
                flex-direction: column;
                align-items: stretch;
            }

            .user-details {
                display: none;
            }

            .search-box {
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
                    Clinical staff directory and availability records
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

          <div class="header-actions">

    <% if (isAdmin) { %>

        <div class="role-badge">
            Administrator Management
        </div>

        <a
            href="<%= request.getContextPath() %>/dentists/new"
            class="register-button">

            + Register Dentist

        </a>

    <% } else { %>

        <div class="role-badge">
            Staff Directory Access
        </div>

    <% } %>

</div>


            <!-- STATISTICS -->

            <div class="stats-grid">

                <div class="stat-card">

                    <div class="stat-label">
                        Registered Dentists
                    </div>

                    <div
                        class="stat-value"
                        id="visibleDentistCount">

                        <%= dentistCount %>

                    </div>

                    <div class="stat-detail">
                        Clinic dentist records
                    </div>

                </div>


                <div class="stat-card">

                    <div class="stat-label">
                        Active Dentists
                    </div>

                    <div class="stat-value">

                        <%= activeDentistCount %>

                    </div>

                    <div class="stat-detail">
                        Currently active records
                    </div>

                </div>


                <div class="stat-card">

                    <div class="stat-label">
                        Data Source
                    </div>

                    <div class="stat-value connection-value connected">
                        REST API
                    </div>

                    <div class="stat-detail">
                        HTTP / JSON service connection
                    </div>

                </div>

            </div>


            <!-- ROLE-AWARE INFORMATION -->

            <% if (isAdmin) { %>

                <div class="management-notice admin-notice">

                    <strong>Administrator access:</strong>
                    You have management privileges for dentist records.
                    Register and edit functionality will be connected
                    through the secured Dentist API.

                </div>

            <% } else { %>

                <div class="management-notice reception-notice">

                    <strong>Reception access:</strong>
                    Dentist information is available for appointment
                    operations. Configuration changes are restricted
                    to administrators.

                </div>

            <% } %>


            <% if (errorMessage != null
                    && !errorMessage.isBlank()) { %>

                <div class="error-message">

                    <%= escapeHtml(errorMessage) %>

                </div>

            <% } %>


            <!-- DENTIST TABLE -->

            <div class="table-panel">

                <div class="table-toolbar">

                    <h3>
                        Dentist Directory
                    </h3>

                    <div class="search-box">

                        <input
                            type="search"
                            id="dentistSearch"
                            placeholder="Search dentists..."
                            autocomplete="off">

                        <span class="search-icon">
                            &#128269;
                        </span>

                    </div>

                </div>


                <div class="table-wrapper">

                    <% if (!dentists.isEmpty()) { %>

                        <table>

                            <thead>

                                <tr>

                                    <th>
                                        Dentist Code
                                    </th>

                                    <th>
                                        Dentist
                                    </th>

                                    <th>
                                        Specialization
                                    </th>

                                    <th>
                                        Contact
                                    </th>

                                    <th>
                                        Email
                                    </th>

                                    <th>
                                        Status
                                    </th>
                                    
                                    <th>
    Action
</th>

                                </tr>

                            </thead>

                            <tbody id="dentistTableBody">

                                <% for (DentistViewModel dentist
                                        : dentists) { %>

                                    <tr class="dentist-row">

                                        <td>

                                            <span class="dentist-code">

                                                <%= escapeHtml(
                                                        dentist
                                                                .getDentistCode()
                                                ) %>

                                            </span>

                                        </td>


                                        <td>

                                            <span class="dentist-name">

                                                <%= escapeHtml(
                                                        dentist
                                                                .getFullName()
                                                ) %>

                                            </span>

                                        </td>


                                        <td>

                                            <span class="specialization">

                                                <%= escapeHtml(
                                                        dentist
                                                                .getSpecialization()
                                                ) %>

                                            </span>

                                        </td>


                                        <td>

                                            <span class="secondary-text">

                                                <%= escapeHtml(
                                                        dentist
                                                                .getContactNumber()
                                                ) %>

                                            </span>

                                        </td>


                                        <td>

                                            <span class="secondary-text">

                                                <%= escapeHtml(
                                                        dentist
                                                                .getEmail()
                                                ) %>

                                            </span>

                                        </td>


                                        <td>

                                            <% if (dentist.isActive()) { %>

                                                <span
                                                    class="status status-active">

                                                    <span
                                                        class="status-dot">
                                                    </span>

                                                    Active

                                                </span>

                                            <% } else { %>

                                                <span
                                                    class="status status-inactive">

                                                    <span
                                                        class="status-dot">
                                                    </span>

                                                    Inactive

                                                </span>

                                            <% } %>

                                        </td>
                                        
                                        
                                        <td>

    <% if (isAdmin) { %>

        <a
            href="<%= request.getContextPath() %>/dentists/edit?id=<%= dentist.getDentistId() %>"
            class="edit-button">

            View / Edit

        </a>

    <% } else { %>

        <span class="secondary-text">
            View only
        </span>

    <% } %>

</td>

                                    </tr>

                                <% } %>

                            </tbody>

                        </table>


                        <div
                            class="no-results"
                            id="noDentistResults">

                            No dentists match your search.

                        </div>

                    <% } else { %>

                        <div class="empty-state">

                            No dentist records are currently available.

                        </div>

                    <% } %>

                </div>


                <div class="table-footer">

                    <span>
                        Sunrise Dental Dentist Directory
                    </span>

                    <span>
                        Secured REST Data
                    </span>

                </div>

            </div>

        </section>

    </main>

</div>


<script>

    /*
     * Client-side dentist search.
     */
    const dentistSearch =
            document.getElementById(
                "dentistSearch"
            );

    const dentistRows =
            document.querySelectorAll(
                "#dentistTableBody .dentist-row"
            );

    const noDentistResults =
            document.getElementById(
                "noDentistResults"
            );

    const visibleDentistCount =
            document.getElementById(
                "visibleDentistCount"
            );

    if (dentistSearch) {

        dentistSearch.addEventListener(
            "input",
            function () {

                const searchTerm =
                        this.value
                                .trim()
                                .toLowerCase();

                let visibleCount = 0;

                dentistRows.forEach(
                    function (row) {

                        const rowText =
                                row.textContent
                                        .toLowerCase();

                        const matches =
                                rowText.includes(
                                    searchTerm
                                );

                        row.style.display =
                                matches
                                        ? ""
                                        : "none";

                        if (matches) {
                            visibleCount++;
                        }
                    }
                );

                if (visibleDentistCount) {

                    visibleDentistCount.textContent =
                            visibleCount;
                }

                if (noDentistResults) {

                    noDentistResults.style.display =
                            visibleCount === 0
                                    ? "block"
                                    : "none";
                }
            }
        );
    }

</script>

</body>

</html>