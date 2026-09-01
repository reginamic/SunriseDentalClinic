<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>

<%@page import="com.sunrisedental.web.model.UserViewModel"%>

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

    private int safeInt(Object value) {

        if (value == null) {
            return 0;
        }

        try {
            return Integer.parseInt(
                    String.valueOf(value)
            );
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
%>

<%
    List<UserViewModel> users =
            (List<UserViewModel>)
            request.getAttribute("users");

    if (users == null) {
        users = Collections.emptyList();
    }

    Integer userCount =
            (Integer)
            request.getAttribute("userCount");

    Long activeUserCount =
            (Long)
            request.getAttribute("activeUserCount");

    Long administratorCount =
            (Long)
            request.getAttribute("administratorCount");

    Long receptionistCount =
            (Long)
            request.getAttribute("receptionistCount");

    String errorMessage =
            (String)
            request.getAttribute("errorMessage");

    if (userCount == null) {
        userCount = users.size();
    }

    if (activeUserCount == null) {
        activeUserCount = 0L;
    }

    if (administratorCount == null) {
        administratorCount = 0L;
    }

    if (receptionistCount == null) {
        receptionistCount = 0L;
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

    int currentUserId =
            safeInt(
                    session.getAttribute("userId")
            );

    String avatarLetter = "A";

    if (!username.isBlank()) {

        avatarLetter =
                username.substring(
                        0,
                        1
                ).toUpperCase();
    }

    boolean created =
            "true".equalsIgnoreCase(
                    request.getParameter("created")
            );

    boolean updated =
            "true".equalsIgnoreCase(
                    request.getParameter("updated")
            );

    boolean statusChanged =
            "true".equalsIgnoreCase(
                    request.getParameter("statusChanged")
            );

    boolean passwordReset =
            "true".equalsIgnoreCase(
                    request.getParameter("passwordReset")
            );
%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        Staff User Management | Sunrise Dental Clinic
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


        button,
        input,
        select {
            font-family: inherit;
        }


        .layout {
            min-height: 100vh;

            display: grid;
            grid-template-columns: 250px 1fr;
        }


        /* =========================================================
           SIDEBAR
           ========================================================= */

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


        .nav-section-title {
            color: #7695b2;

            font-size: 9px;
            font-weight: 700;

            text-transform: uppercase;
            letter-spacing: 0.08em;

            padding:
                15px 13px
                4px;
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


        /* =========================================================
           MAIN / TOP BAR
           ========================================================= */

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


        /* =========================================================
           CONTENT
           ========================================================= */

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

            max-width: 720px;
        }


        .header-actions {
            display: flex;
            align-items: center;
            gap: 10px;

            flex-wrap: wrap;
        }


        .role-badge {
            background: #eaf2fb;
            color: #153e75;

            border:
                1px solid #d5e5f5;

            border-radius: 8px;

            padding: 9px 13px;

            font-size: 11px;
            font-weight: 700;

            white-space: nowrap;
        }


        .primary-button {
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

            cursor: pointer;

            font-size: 12px;
            font-weight: 600;

            white-space: nowrap;
        }


        .primary-button:hover {
            background: #102f57;
        }


        /* =========================================================
           MESSAGES
           ========================================================= */

        .message {
            padding: 13px 16px;

            border-radius: 8px;

            margin-bottom: 20px;

            font-size: 12px;
        }


        .message-success {
            background: #ecfdf5;
            color: #047857;

            border:
                1px solid #a7f3d0;
        }


        .message-error {
            background: #fef2f2;
            color: #b91c1c;

            border:
                1px solid #fecaca;
        }


        /* =========================================================
           STATISTICS
           ========================================================= */

        .stats-grid {
            display: grid;

            grid-template-columns:
                repeat(4, minmax(0, 1fr));

            gap: 15px;

            margin-bottom: 22px;
        }


        .stat-card {
            background: #ffffff;

            border:
                1px solid #e2e8f0;

            border-radius: 11px;

            padding: 18px 20px;
        }


        .stat-label {
            color: #64748b;

            font-size: 10px;
            font-weight: 700;

            margin-bottom: 9px;

            text-transform: uppercase;
            letter-spacing: 0.03em;
        }


        .stat-value {
            color: #102a43;

            font-size: 24px;
            font-weight: 700;
        }


        .stat-value.admin {
            color: #153e75;
        }


        .stat-value.reception {
            color: #475569;
        }


        .stat-detail {
            color: #94a3b8;

            font-size: 10px;

            margin-top: 5px;
        }


        /* =========================================================
           SECURITY NOTICE
           ========================================================= */

        .management-notice {
            margin-bottom: 20px;

            padding: 14px 16px;

            border-radius: 9px;

            font-size: 11px;
            line-height: 1.7;

            background: #eff6ff;
            color: #1e40af;

            border:
                1px solid #bfdbfe;
        }


        /* =========================================================
           TABLE PANEL
           ========================================================= */

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


        .toolbar-controls {
            display: flex;
            align-items: center;
            gap: 10px;

            flex-wrap: wrap;
        }


        .search-box {
            width: 270px;
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


        .filter-select {
            height: 39px;

            padding: 0 11px;

            border:
                1px solid #cbd5e1;

            border-radius: 8px;

            background: #ffffff;

            color: #334155;

            font-size: 11px;

            outline: none;
        }


        .table-wrapper {
            width: 100%;

            overflow-x: auto;
        }


        table {
            width: 100%;

            border-collapse: collapse;

            min-width: 1050px;
        }


        th {
            background: #f8fafc;

            color: #64748b;

            text-align: left;

            font-size: 9px;
            font-weight: 700;

            text-transform: uppercase;
            letter-spacing: 0.04em;

            padding: 13px 14px;

            border-bottom:
                1px solid #e2e8f0;
        }


        td {
            padding: 15px 14px;

            border-bottom:
                1px solid #edf2f7;

            font-size: 11px;

            vertical-align: middle;
        }


        tbody tr:hover {
            background: #f8fbff;
        }


        tbody tr:last-child td {
            border-bottom: none;
        }


        .user-id {
            color: #64748b;

            font-weight: 600;

            white-space: nowrap;
        }


        .staff-name {
            color: #1e293b;

            font-weight: 700;
        }


        .username {
            color: #153e75;

            font-weight: 600;
        }


        .current-label {
            display: inline-flex;

            margin-left: 6px;

            padding: 2px 6px;

            border-radius: 10px;

            background: #e0f2fe;
            color: #0369a1;

            font-size: 8px;
            font-weight: 700;

            vertical-align: middle;
        }


        /* =========================================================
           ROLE / STATUS BADGES
           ========================================================= */

        .role-pill,
        .status {
            display: inline-flex;
            align-items: center;
            gap: 5px;

            border-radius: 20px;

            padding: 5px 9px;

            font-size: 10px;
            font-weight: 700;

            white-space: nowrap;
        }


        .role-admin {
            background: #eff6ff;
            color: #1d4ed8;
        }


        .role-receptionist {
            background: #f1f5f9;
            color: #475569;
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


        /* =========================================================
           ACTION BUTTONS
           ========================================================= */

        .action-group {
            display: flex;
            align-items: center;
            gap: 6px;

            flex-wrap: wrap;
        }


        .small-button {
            min-height: 31px;

            padding: 0 10px;

            border-radius: 7px;

            font-size: 9px;
            font-weight: 600;

            cursor: pointer;

            white-space: nowrap;
        }


        .button-edit {
            background: #ffffff;
            color: #153e75;

            border:
                1px solid #cbd5e1;
        }


        .button-edit:hover {
            background: #eff6ff;
            border-color: #93c5fd;
        }


        .button-password {
            background: #ffffff;
            color: #6d28d9;

            border:
                1px solid #ddd6fe;
        }


        .button-password:hover {
            background: #f5f3ff;
        }


        .button-deactivate {
            background: #ffffff;
            color: #b45309;

            border:
                1px solid #fed7aa;
        }


        .button-deactivate:hover {
            background: #fff7ed;
        }


        .button-activate {
            background: #ffffff;
            color: #047857;

            border:
                1px solid #a7f3d0;
        }


        .button-activate:hover {
            background: #ecfdf5;
        }


        .button-disabled {
            background: #f8fafc;
            color: #94a3b8;

            border:
                1px solid #e2e8f0;

            cursor: not-allowed;
        }


        /* =========================================================
           EMPTY / RESULTS
           ========================================================= */

        .no-results {
            display: none;

            text-align: center;

            padding: 32px;

            color: #64748b;

            font-size: 12px;
        }


        .empty-state {
            text-align: center;

            padding: 35px;

            color: #64748b;

            font-size: 12px;
        }


        .table-footer {
            border-top:
                1px solid #e2e8f0;

            background: #f8fafc;

            padding: 11px 16px;

            display: flex;
            justify-content: space-between;

            gap: 12px;

            color: #64748b;

            font-size: 10px;
        }


        /* =========================================================
           MODAL
           ========================================================= */

        .modal-overlay {
            position: fixed;

            inset: 0;

            background:
                rgba(15, 23, 42, 0.55);

            display: none;

            align-items: center;
            justify-content: center;

            padding: 20px;

            z-index: 1000;
        }


        .modal-overlay.open {
            display: flex;
        }


        .modal {
            width: 100%;
            max-width: 520px;

            max-height: 92vh;

            overflow-y: auto;

            background: #ffffff;

            border-radius: 13px;

            box-shadow:
                0 24px 60px
                rgba(15, 23, 42, 0.22);
        }


        .modal-header {
            padding: 20px 22px;

            border-bottom:
                1px solid #e2e8f0;

            display: flex;
            align-items: flex-start;
            justify-content: space-between;

            gap: 15px;
        }


        .modal-header h3 {
            color: #102a43;

            font-size: 17px;
        }


        .modal-header p {
            color: #64748b;

            font-size: 10px;
            line-height: 1.5;

            margin-top: 5px;
        }


        .modal-close {
            width: 32px;
            height: 32px;

            border: none;

            border-radius: 7px;

            background: #f1f5f9;
            color: #475569;

            cursor: pointer;

            font-size: 17px;
        }


        .modal-body {
            padding: 22px;
        }


        .form-grid {
            display: grid;
            gap: 15px;
        }


        .form-row {
            display: grid;
            grid-template-columns:
                repeat(2, minmax(0, 1fr));

            gap: 14px;
        }


        .form-group {
            display: grid;
            gap: 6px;
        }


        .form-group label {
            color: #334155;

            font-size: 10px;
            font-weight: 700;
        }


        .required {
            color: #dc2626;
        }


        .form-group input,
        .form-group select {
            width: 100%;
            height: 41px;

            border:
                1px solid #cbd5e1;

            border-radius: 8px;

            padding: 0 11px;

            outline: none;

            background: #ffffff;

            color: #1e293b;

            font-size: 12px;
        }


        .form-group input:focus,
        .form-group select:focus {
            border-color: #2563a6;

            box-shadow:
                0 0 0 3px
                rgba(37, 99, 166, 0.10);
        }


        .form-group input[readonly] {
            background: #f8fafc;

            color: #64748b;
        }


        .field-help {
            color: #94a3b8;

            font-size: 9px;
            line-height: 1.5;
        }


        .security-box {
            margin-top: 2px;

            padding: 11px 12px;

            border-radius: 8px;

            background: #f8fafc;

            border:
                1px solid #e2e8f0;

            color: #64748b;

            font-size: 9px;
            line-height: 1.6;
        }


        .modal-footer {
            padding: 16px 22px;

            border-top:
                1px solid #e2e8f0;

            background: #f8fafc;

            display: flex;
            justify-content: flex-end;

            gap: 9px;
        }


        .secondary-button {
            min-height: 38px;

            padding: 0 15px;

            background: #ffffff;
            color: #475569;

            border:
                1px solid #cbd5e1;

            border-radius: 8px;

            cursor: pointer;

            font-size: 11px;
            font-weight: 600;
        }


        /* =========================================================
           RESPONSIVE
           ========================================================= */

        @media (max-width: 1100px) {

            .stats-grid {
                grid-template-columns:
                    repeat(2, 1fr);
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

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .header-actions,
            .toolbar-controls {
                flex-direction: column;
                align-items: stretch;
            }

            .search-box {
                width: 100%;
            }

            .primary-button {
                width: 100%;
            }

            .form-row {
                grid-template-columns: 1fr;
            }

            .user-details {
                display: none;
            }

            .table-footer {
                flex-direction: column;
            }
        }

    </style>

</head>


<body>


<div class="layout">


    <!-- =========================================================
         SIDEBAR
         ========================================================= -->

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


            <a href="<%= request.getContextPath() %>/reports">

                <span class="nav-icon">
                    &#128202;
                </span>

                Reports

            </a>


            <div class="nav-section-title">
                Administration
            </div>


            <a
                class="active"
                href="<%= request.getContextPath() %>/users">

                <span class="nav-icon">
                    &#128101;
                </span>

                Staff Users

            </a>


            <div class="nav-section-title">
                Support
            </div>


            <a href="<%= request.getContextPath() %>/help">

                <span class="nav-icon">
                    &#10067;
                </span>

                Help &amp; User Guide

            </a>


        </nav>


        <div class="sidebar-footer">

            <a href="<%= request.getContextPath() %>/logout">
                Sign out
            </a>

        </div>


    </aside>


    <!-- =========================================================
         MAIN
         ========================================================= -->

    <main class="main">


        <header class="topbar">


            <div class="page-title">

                <h1>
                    Staff User Management
                </h1>

                <p>
                    Secure staff accounts, roles and access status
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


            <!-- =================================================
                 PAGE HEADER
                 ================================================= -->

            <div class="content-header">


                <div>

                    <h2>
                        Staff Accounts &amp; Access Control
                    </h2>

                    <p>
                        Create and maintain authorised clinic staff
                        accounts. Administrators can manage staff identity,
                        role assignment, account status and secure password
                        resets while historical user records are preserved.
                    </p>

                </div>


                <div class="header-actions">

                    <div class="role-badge">
                        Administrator Only
                    </div>

                    <button
                        type="button"
                        class="primary-button"
                        onclick="openCreateModal()">

                        + Create Staff Account

                    </button>

                </div>


            </div>


            <!-- =================================================
                 MESSAGES
                 ================================================= -->

            <% if (created) { %>

                <div class="message message-success">

                    Staff account created successfully.

                </div>

            <% } %>


            <% if (updated) { %>

                <div class="message message-success">

                    Staff account details updated successfully.

                </div>

            <% } %>


            <% if (statusChanged) { %>

                <div class="message message-success">

                    Staff account status updated successfully.

                </div>

            <% } %>


            <% if (passwordReset) { %>

                <div class="message message-success">

                    Staff account password reset successfully.

                </div>

            <% } %>


            <% if (errorMessage != null
                    && !errorMessage.isBlank()) { %>

                <div class="message message-error">

                    <%= escapeHtml(errorMessage) %>

                </div>

            <% } %>


            <!-- =================================================
                 STATISTICS
                 ================================================= -->

            <div class="stats-grid">


                <div class="stat-card">

                    <div class="stat-label">
                        Registered Accounts
                    </div>

                    <div
                        class="stat-value"
                        id="visibleUserCount">

                        <%= userCount %>

                    </div>

                    <div class="stat-detail">
                        Staff identities recorded
                    </div>

                </div>


                <div class="stat-card">

                    <div class="stat-label">
                        Active Accounts
                    </div>

                    <div class="stat-value">

                        <%= activeUserCount %>

                    </div>

                    <div class="stat-detail">
                        Accounts permitted to authenticate
                    </div>

                </div>


                <div class="stat-card">

                    <div class="stat-label">
                        Administrators
                    </div>

                    <div class="stat-value admin">

                        <%= administratorCount %>

                    </div>

                    <div class="stat-detail">
                        Privileged management accounts
                    </div>

                </div>


                <div class="stat-card">

                    <div class="stat-label">
                        Receptionists
                    </div>

                    <div class="stat-value reception">

                        <%= receptionistCount %>

                    </div>

                    <div class="stat-detail">
                        Operational front-desk accounts
                    </div>

                </div>


            </div>


            <!-- =================================================
                 SECURITY NOTICE
                 ================================================= -->

            <div class="management-notice">

                <strong>Security control:</strong>
                Staff passwords are never displayed on this page.
                New and reset passwords are sent through the Web tier
                to the REST API and stored only as secure password hashes.
                Accounts are activated or deactivated rather than
                physically deleted, preserving clinical, billing and
                audit history.

            </div>


            <!-- =================================================
                 TABLE
                 ================================================= -->

            <div class="table-panel">


                <div class="table-toolbar">


                    <h3>
                        Authorised Staff Accounts
                    </h3>


                    <div class="toolbar-controls">


                        <select
                            id="roleFilter"
                            class="filter-select">

                            <option value="ALL">
                                All Roles
                            </option>

                            <option value="ADMIN">
                                Administrators
                            </option>

                            <option value="RECEPTIONIST">
                                Receptionists
                            </option>

                        </select>


                        <select
                            id="statusFilter"
                            class="filter-select">

                            <option value="ALL">
                                All Statuses
                            </option>

                            <option value="ACTIVE">
                                Active
                            </option>

                            <option value="INACTIVE">
                                Inactive
                            </option>

                        </select>


                        <div class="search-box">

                            <input
                                type="search"
                                id="userSearch"
                                placeholder="Search staff accounts..."
                                autocomplete="off">

                            <span class="search-icon">
                                &#128269;
                            </span>

                        </div>


                    </div>


                </div>


                <div class="table-wrapper">


                    <% if (!users.isEmpty()) { %>


                        <table>


                            <thead>

                                <tr>

                                    <th>
                                        ID
                                    </th>

                                    <th>
                                        Staff Member
                                    </th>

                                    <th>
                                        Username
                                    </th>

                                    <th>
                                        Role
                                    </th>

                                    <th>
                                        Status
                                    </th>

                                    <th>
                                        Created
                                    </th>

                                    <th>
                                        Actions
                                    </th>

                                </tr>

                            </thead>


                            <tbody id="userTableBody">


                                <% for (UserViewModel user : users) {

                                    boolean currentAccount =
                                            user.getUserId()
                                                    == currentUserId;
                                %>


                                    <tr
                                        class="user-row"
                                        data-role="<%= escapeHtml(user.getRole()) %>"
                                        data-status="<%= user.isActive()
                                                ? "ACTIVE"
                                                : "INACTIVE" %>">


                                        <td>

                                            <span class="user-id">

                                                #<%= user.getUserId() %>

                                            </span>

                                        </td>


                                        <td>

                                            <span class="staff-name">

                                                <%= escapeHtml(
                                                        user.getFullName()
                                                ) %>

                                            </span>


                                            <% if (currentAccount) { %>

                                                <span class="current-label">
                                                    CURRENT
                                                </span>

                                            <% } %>

                                        </td>


                                        <td>

                                            <span class="username">

                                                <%= escapeHtml(
                                                        user.getUsername()
                                                ) %>

                                            </span>

                                        </td>


                                        <td>

                                            <% if (user.isAdmin()) { %>

                                                <span
                                                    class="role-pill role-admin">

                                                    Administrator

                                                </span>

                                            <% } else { %>

                                                <span
                                                    class="role-pill role-receptionist">

                                                    Receptionist

                                                </span>

                                            <% } %>

                                        </td>


                                        <td>

                                            <% if (user.isActive()) { %>

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

                                            <%= escapeHtml(
                                                    user.getCreatedAtText()
                                            ) %>

                                        </td>


                                        <td>


                                            <div class="action-group">


                                                <button
                                                    type="button"
                                                    class="small-button button-edit"
                                                    onclick="openEditModal(
                                                        '<%= user.getUserId() %>',
                                                        '<%= escapeHtml(user.getFullName()) %>',
                                                        '<%= escapeHtml(user.getUsername()) %>',
                                                        '<%= escapeHtml(user.getRole()) %>'
                                                    )">

                                                    Edit

                                                </button>


                                                <button
                                                    type="button"
                                                    class="small-button button-password"
                                                    onclick="openPasswordModal(
                                                        '<%= user.getUserId() %>',
                                                        '<%= escapeHtml(user.getUsername()) %>'
                                                    )">

                                                    Reset Password

                                                </button>


                                                <% if (currentAccount) { %>

                                                    <button
                                                        type="button"
                                                        class="small-button button-disabled"
                                                        disabled
                                                        title="The currently signed-in account cannot change its own active status.">

                                                        Current Account

                                                    </button>


                                                <% } else if (user.isActive()) { %>


                                                    <form
                                                        method="post"
                                                        action="<%= request.getContextPath() %>/users/manage"
                                                        onsubmit="return confirm(
                                                            'Deactivate this staff account?'
                                                        );">

                                                        <input
                                                            type="hidden"
                                                            name="action"
                                                            value="status">

                                                        <input
                                                            type="hidden"
                                                            name="userId"
                                                            value="<%= user.getUserId() %>">

                                                        <input
                                                            type="hidden"
                                                            name="active"
                                                            value="false">

                                                        <button
                                                            type="submit"
                                                            class="small-button button-deactivate">

                                                            Deactivate

                                                        </button>

                                                    </form>


                                                <% } else { %>


                                                    <form
                                                        method="post"
                                                        action="<%= request.getContextPath() %>/users/manage">

                                                        <input
                                                            type="hidden"
                                                            name="action"
                                                            value="status">

                                                        <input
                                                            type="hidden"
                                                            name="userId"
                                                            value="<%= user.getUserId() %>">

                                                        <input
                                                            type="hidden"
                                                            name="active"
                                                            value="true">

                                                        <button
                                                            type="submit"
                                                            class="small-button button-activate">

                                                            Activate

                                                        </button>

                                                    </form>


                                                <% } %>


                                            </div>


                                        </td>


                                    </tr>


                                <% } %>


                            </tbody>


                        </table>


                        <div
                            class="no-results"
                            id="noUserResults">

                            No staff accounts match your search
                            or selected filters.

                        </div>


                    <% } else { %>


                        <div class="empty-state">

                            No staff accounts are currently available.

                        </div>


                    <% } %>


                </div>


                <div class="table-footer">

                    <span>
                        Sunrise Dental Staff Access Administration
                    </span>

                    <span>
                        User data supplied through REST / JSON services
                    </span>

                </div>


            </div>


        </section>


    </main>


</div>


<!-- =============================================================
     CREATE USER MODAL
     ============================================================= -->

<div
    class="modal-overlay"
    id="createUserModal">


    <div class="modal">


        <div class="modal-header">

            <div>

                <h3>
                    Create Staff Account
                </h3>

                <p>
                    Register a new authorised administrator
                    or receptionist account.
                </p>

            </div>

            <button
                type="button"
                class="modal-close"
                onclick="closeModal('createUserModal')">

                &times;

            </button>

        </div>


        <form
            method="post"
            action="<%= request.getContextPath() %>/users/manage"
            onsubmit="return validateCreateForm();">


            <input
                type="hidden"
                name="action"
                value="create">


            <div class="modal-body">


                <div class="form-grid">


                    <div class="form-group">

                        <label for="createFullName">

                            Full Name
                            <span class="required">*</span>

                        </label>

                        <input
                            type="text"
                            id="createFullName"
                            name="fullName"
                            maxlength="100"
                            required
                            autocomplete="name">

                    </div>


                    <div class="form-row">


                        <div class="form-group">

                            <label for="createUsername">

                                Username
                                <span class="required">*</span>

                            </label>

                            <input
                                type="text"
                                id="createUsername"
                                name="username"
                                minlength="3"
                                maxlength="50"
                                pattern="[A-Za-z0-9._-]+"
                                required
                                autocomplete="off">

                            <div class="field-help">
                                Letters, numbers, dots, underscores
                                and hyphens only.
                            </div>

                        </div>


                        <div class="form-group">

                            <label for="createRole">

                                Access Role
                                <span class="required">*</span>

                            </label>

                            <select
                                id="createRole"
                                name="role"
                                required>

                                <option value="RECEPTIONIST">
                                    Receptionist
                                </option>

                                <option value="ADMIN">
                                    Administrator
                                </option>

                            </select>

                        </div>


                    </div>


                    <div class="form-row">


                        <div class="form-group">

                            <label for="createPassword">

                                Password
                                <span class="required">*</span>

                            </label>

                            <input
                                type="password"
                                id="createPassword"
                                name="password"
                                minlength="8"
                                required
                                autocomplete="new-password">

                        </div>


                        <div class="form-group">

                            <label for="createConfirmPassword">

                                Confirm Password
                                <span class="required">*</span>

                            </label>

                            <input
                                type="password"
                                id="createConfirmPassword"
                                minlength="8"
                                required
                                autocomplete="new-password">

                        </div>


                    </div>


                    <div class="security-box">

                        Passwords must contain at least eight
                        characters. Plain-text passwords are never
                        displayed or stored by the application.

                    </div>


                </div>


            </div>


            <div class="modal-footer">

                <button
                    type="button"
                    class="secondary-button"
                    onclick="closeModal('createUserModal')">

                    Cancel

                </button>

                <button
                    type="submit"
                    class="primary-button">

                    Create Account

                </button>

            </div>


        </form>


    </div>


</div>


<!-- =============================================================
     EDIT USER MODAL
     ============================================================= -->

<div
    class="modal-overlay"
    id="editUserModal">


    <div class="modal">


        <div class="modal-header">

            <div>

                <h3>
                    Edit Staff Account
                </h3>

                <p>
                    Update the staff member's display name
                    or authorised role.
                </p>

            </div>

            <button
                type="button"
                class="modal-close"
                onclick="closeModal('editUserModal')">

                &times;

            </button>

        </div>


        <form
            method="post"
            action="<%= request.getContextPath() %>/users/manage">


            <input
                type="hidden"
                name="action"
                value="details">

            <input
                type="hidden"
                name="userId"
                id="editUserId">


            <div class="modal-body">


                <div class="form-grid">


                    <div class="form-group">

                        <label for="editUsername">
                            Username
                        </label>

                        <input
                            type="text"
                            id="editUsername"
                            readonly>

                        <div class="field-help">
                            Username is the permanent login identifier
                            and is not changed during account editing.
                        </div>

                    </div>


                    <div class="form-group">

                        <label for="editFullName">

                            Full Name
                            <span class="required">*</span>

                        </label>

                        <input
                            type="text"
                            id="editFullName"
                            name="fullName"
                            maxlength="100"
                            required>

                    </div>


                    <div class="form-group">

                        <label for="editRole">

                            Access Role
                            <span class="required">*</span>

                        </label>

                        <select
                            id="editRole"
                            name="role"
                            required>

                            <option value="ADMIN">
                                Administrator
                            </option>

                            <option value="RECEPTIONIST">
                                Receptionist
                            </option>

                        </select>

                    </div>


                    <div class="security-box">

                        The system protects the final active
                        Administrator account from being accidentally
                        demoted.

                    </div>


                </div>


            </div>


            <div class="modal-footer">

                <button
                    type="button"
                    class="secondary-button"
                    onclick="closeModal('editUserModal')">

                    Cancel

                </button>

                <button
                    type="submit"
                    class="primary-button">

                    Save Changes

                </button>

            </div>


        </form>


    </div>


</div>


<!-- =============================================================
     PASSWORD RESET MODAL
     ============================================================= -->

<div
    class="modal-overlay"
    id="passwordModal">


    <div class="modal">


        <div class="modal-header">

            <div>

                <h3>
                    Reset Staff Password
                </h3>

                <p id="passwordModalDescription">
                    Set a new secure password for this account.
                </p>

            </div>

            <button
                type="button"
                class="modal-close"
                onclick="closeModal('passwordModal')">

                &times;

            </button>

        </div>


        <form
            method="post"
            action="<%= request.getContextPath() %>/users/manage"
            onsubmit="return validatePasswordResetForm();">


            <input
                type="hidden"
                name="action"
                value="password">

            <input
                type="hidden"
                name="userId"
                id="passwordUserId">


            <div class="modal-body">


                <div class="form-grid">


                    <div class="form-group">

                        <label for="resetUsername">
                            Account
                        </label>

                        <input
                            type="text"
                            id="resetUsername"
                            readonly>

                    </div>


                    <div class="form-row">


                        <div class="form-group">

                            <label for="newPassword">

                                New Password
                                <span class="required">*</span>

                            </label>

                            <input
                                type="password"
                                id="newPassword"
                                name="password"
                                minlength="8"
                                required
                                autocomplete="new-password">

                        </div>


                        <div class="form-group">

                            <label for="confirmNewPassword">

                                Confirm Password
                                <span class="required">*</span>

                            </label>

                            <input
                                type="password"
                                id="confirmNewPassword"
                                minlength="8"
                                required
                                autocomplete="new-password">

                        </div>


                    </div>


                    <div class="security-box">

                        The new password is securely processed by
                        the API password service. Existing password
                        values and password hashes are never sent
                        to this page.

                    </div>


                </div>


            </div>


            <div class="modal-footer">

                <button
                    type="button"
                    class="secondary-button"
                    onclick="closeModal('passwordModal')">

                    Cancel

                </button>

                <button
                    type="submit"
                    class="primary-button">

                    Reset Password

                </button>

            </div>


        </form>


    </div>


</div>


<script>

    const userSearch =
            document.getElementById(
                "userSearch"
            );

    const roleFilter =
            document.getElementById(
                "roleFilter"
            );

    const statusFilter =
            document.getElementById(
                "statusFilter"
            );

    const userRows =
            document.querySelectorAll(
                "#userTableBody .user-row"
            );

    const noUserResults =
            document.getElementById(
                "noUserResults"
            );

    const visibleUserCount =
            document.getElementById(
                "visibleUserCount"
            );


    /* ============================================================
       TABLE FILTERING
       ============================================================ */

    function filterUsers() {

        const searchTerm =
                userSearch
                        ? userSearch.value
                                .trim()
                                .toLowerCase()
                        : "";

        const selectedRole =
                roleFilter
                        ? roleFilter.value
                        : "ALL";

        const selectedStatus =
                statusFilter
                        ? statusFilter.value
                        : "ALL";

        let visibleCount = 0;


        userRows.forEach(
                function(row) {

                    const rowText =
                            row.textContent
                                    .toLowerCase();

                    const rowRole =
                            row.dataset.role;

                    const rowStatus =
                            row.dataset.status;

                    const matchesSearch =
                            searchTerm === ""
                            || rowText.includes(
                                    searchTerm
                            );

                    const matchesRole =
                            selectedRole === "ALL"
                            || rowRole ===
                                    selectedRole;

                    const matchesStatus =
                            selectedStatus === "ALL"
                            || rowStatus ===
                                    selectedStatus;


                    const visible =
                            matchesSearch
                            && matchesRole
                            && matchesStatus;


                    row.style.display =
                            visible
                                    ? ""
                                    : "none";


                    if (visible) {
                        visibleCount++;
                    }
                }
        );


        if (visibleUserCount) {
            visibleUserCount.textContent =
                    visibleCount;
        }


        if (noUserResults) {

            noUserResults.style.display =
                    visibleCount === 0
                            ? "block"
                            : "none";
        }
    }


    if (userSearch) {

        userSearch.addEventListener(
                "input",
                filterUsers
        );
    }


    if (roleFilter) {

        roleFilter.addEventListener(
                "change",
                filterUsers
        );
    }


    if (statusFilter) {

        statusFilter.addEventListener(
                "change",
                filterUsers
        );
    }


    /* ============================================================
       MODAL CONTROL
       ============================================================ */

    function openModal(modalId) {

        const modal =
                document.getElementById(
                        modalId
                );

        if (modal) {

            modal.classList.add(
                    "open"
            );

            document.body.style.overflow =
                    "hidden";
        }
    }


    function closeModal(modalId) {

        const modal =
                document.getElementById(
                        modalId
                );

        if (modal) {

            modal.classList.remove(
                    "open"
            );
        }

        document.body.style.overflow =
                "";
    }


    function openCreateModal() {

        const form =
                document.querySelector(
                    "#createUserModal form"
                );

        if (form) {
            form.reset();
        }

        openModal(
                "createUserModal"
        );
    }


    function openEditModal(
            userId,
            fullName,
            username,
            role) {

        document.getElementById(
                "editUserId"
        ).value =
                userId;

        document.getElementById(
                "editFullName"
        ).value =
                fullName;

        document.getElementById(
                "editUsername"
        ).value =
                username;

        document.getElementById(
                "editRole"
        ).value =
                role;

        openModal(
                "editUserModal"
        );
    }


    function openPasswordModal(
            userId,
            username) {

        document.getElementById(
                "passwordUserId"
        ).value =
                userId;

        document.getElementById(
                "resetUsername"
        ).value =
                username;

        document.getElementById(
                "newPassword"
        ).value =
                "";

        document.getElementById(
                "confirmNewPassword"
        ).value =
                "";

        openModal(
                "passwordModal"
        );
    }


    /* ============================================================
       CLIENT-SIDE PASSWORD CONFIRMATION
       ============================================================ */

    function validateCreateForm() {

        const password =
                document.getElementById(
                    "createPassword"
                ).value;

        const confirmPassword =
                document.getElementById(
                    "createConfirmPassword"
                ).value;


        if (password !== confirmPassword) {

            alert(
                "Password and confirmation password do not match."
            );

            return false;
        }

        return true;
    }


    function validatePasswordResetForm() {

        const password =
                document.getElementById(
                    "newPassword"
                ).value;

        const confirmPassword =
                document.getElementById(
                    "confirmNewPassword"
                ).value;


        if (password !== confirmPassword) {

            alert(
                "New password and confirmation password do not match."
            );

            return false;
        }

        return true;
    }


    /* ============================================================
       CLOSE MODALS
       ============================================================ */

    document.querySelectorAll(
            ".modal-overlay"
    ).forEach(
            function(modal) {

                modal.addEventListener(
                        "click",
                        function(event) {

                            if (event.target === modal) {

                                closeModal(
                                        modal.id
                                );
                            }
                        }
                );
            }
    );


    document.addEventListener(
            "keydown",
            function(event) {

                if (event.key === "Escape") {

                    document.querySelectorAll(
                            ".modal-overlay.open"
                    ).forEach(
                            function(modal) {

                                closeModal(
                                        modal.id
                                );
                            }
                    );
                }
            }
    );

</script>


</body>

</html>