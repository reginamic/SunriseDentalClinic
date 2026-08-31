<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Dashboard | Sunrise Dental Clinic</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family:
                "Segoe UI",
                Arial,
                sans-serif;

            background: #f4f7fb;
            color: #1e293b;

            min-height: 100vh;
        }

        .layout {
            min-height: 100vh;

            display: grid;
            grid-template-columns:
                250px 1fr;
        }

        /* -------------------------
           SIDEBAR
           ------------------------- */

        .sidebar {
            background: #102a43;
            color: #ffffff;

            padding: 28px 18px;

            display: flex;
            flex-direction: column;

            min-height: 100vh;
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
            line-height: 1.3;
        }

        .brand p {
            margin-top: 5px;

            color: #a9c1d8;

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

            text-decoration: none;

            font-size: 12px;
        }

        .sidebar-footer a:hover {
            color: #ffffff;
        }

        /* -------------------------
           MAIN AREA
           ------------------------- */

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
            font-weight: 700;
        }

        .page-title p {
            margin-top: 3px;

            color: #64748b;

            font-size: 12px;
        }

        .user-box {
            display: flex;
            align-items: center;
            gap: 12px;
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

        .user-details {
            text-align: right;
        }

        .user-details strong {
            display: block;

            color: #1e293b;

            font-size: 13px;
        }

        .user-details span {
            color: #64748b;

            font-size: 11px;
        }

        /* -------------------------
           CONTENT
           ------------------------- */

        .content {
            padding: 32px;
        }

        .welcome-panel {
            background:
                linear-gradient(
                    135deg,
                    #153e75,
                    #1f5f99
                );

            color: #ffffff;

            border-radius: 14px;

            padding: 30px;

            display: flex;
            justify-content: space-between;
            align-items: center;

            margin-bottom: 28px;

            box-shadow:
                0 12px 28px
                rgba(21, 62, 117, 0.14);
        }

        .welcome-panel h2 {
            font-size: 23px;
            margin-bottom: 7px;
        }

        .welcome-panel p {
            color: #d8e8f7;

            font-size: 13px;
            line-height: 1.6;
        }

        .welcome-icon {
            font-size: 54px;

            opacity: 0.22;
        }

        .section-heading {
            margin-bottom: 17px;
        }

        .section-heading h2 {
            color: #102a43;

            font-size: 17px;
        }

        .section-heading p {
            color: #64748b;

            font-size: 12px;

            margin-top: 4px;
        }

        .module-grid {
            display: grid;

            grid-template-columns:
                repeat(4, minmax(0, 1fr));

            gap: 18px;

            margin-bottom: 30px;
        }

        .module-card {
            background: #ffffff;

            border:
                1px solid #e2e8f0;

            border-radius: 12px;

            padding: 22px;

            text-decoration: none;

            color: inherit;

            transition:
                transform 0.2s,
                box-shadow 0.2s,
                border-color 0.2s;
        }

        .module-card:hover {
            transform:
                translateY(-3px);

            box-shadow:
                0 10px 24px
                rgba(15, 23, 42, 0.08);

            border-color: #b8cce0;
        }

        .module-icon {
            width: 43px;
            height: 43px;

            border-radius: 10px;

            background: #edf4fb;

            color: #1f5f99;

            display: flex;
            align-items: center;
            justify-content: center;

            font-size: 20px;

            margin-bottom: 17px;
        }

        .module-card h3 {
            color: #102a43;

            font-size: 14px;

            margin-bottom: 7px;
        }

        .module-card p {
            color: #64748b;

            font-size: 11px;
            line-height: 1.55;
        }

        .bottom-grid {
            display: grid;

            grid-template-columns:
                1.5fr 1fr;

            gap: 18px;
        }

        .panel {
            background: #ffffff;

            border:
                1px solid #e2e8f0;

            border-radius: 12px;

            padding: 22px;
        }

        .panel h3 {
            color: #102a43;

            font-size: 15px;

            margin-bottom: 16px;
        }

        .workflow {
            display: grid;
            gap: 13px;
        }

        .workflow-item {
            display: flex;
            align-items: flex-start;
            gap: 11px;

            padding-bottom: 13px;

            border-bottom:
                1px solid #edf2f7;
        }

        .workflow-item:last-child {
            border-bottom: none;
            padding-bottom: 0;
        }

        .workflow-number {
            width: 27px;
            height: 27px;

            border-radius: 50%;

            background: #edf4fb;
            color: #1f5f99;

            display: flex;
            align-items: center;
            justify-content: center;

            font-size: 11px;
            font-weight: 700;

            flex-shrink: 0;
        }

        .workflow-text strong {
            display: block;

            color: #334155;

            font-size: 12px;

            margin-bottom: 3px;
        }

        .workflow-text span {
            color: #64748b;

            font-size: 11px;
        }

        .system-info {
            display: grid;
            gap: 14px;
        }

        .info-row {
            display: flex;
            justify-content: space-between;
            gap: 15px;

            font-size: 12px;
        }

        .info-row span {
            color: #64748b;
        }

        .info-row strong {
            color: #334155;
        }

        .online {
            color: #15803d !important;
        }

        /* -------------------------
           RESPONSIVE
           ------------------------- */

        @media (max-width: 1100px) {

            .module-grid {
                grid-template-columns:
                    repeat(2, minmax(0, 1fr));
            }
        }

        @media (max-width: 850px) {

            .layout {
                grid-template-columns: 1fr;
            }

            .sidebar {
                display: none;
            }

            .bottom-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 600px) {

            .topbar {
                padding: 0 18px;
            }

            .content {
                padding: 20px;
            }

            .module-grid {
                grid-template-columns: 1fr;
            }

            .welcome-panel {
                padding: 24px;
            }

            .welcome-icon {
                display: none;
            }

            .user-details {
                display: none;
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

            <a
                class="active"
                href="${pageContext.request.contextPath}/dashboard">

                <span class="nav-icon">
                    &#9632;
                </span>

                Dashboard
            </a>

            <a href="${pageContext.request.contextPath}/patients">

                <span class="nav-icon">
                    &#128100;
                </span>

                Patients
            </a>

            <a href="${pageContext.request.contextPath}/dentists">

                <span class="nav-icon">
                    &#9877;
                </span>

                Dentists
            </a>

            <a href="${pageContext.request.contextPath}/treatments">

                <span class="nav-icon">
                    &#10010;
                </span>

                Treatments
            </a>

            <a href="${pageContext.request.contextPath}/appointments">

                <span class="nav-icon">
                    &#128197;
                </span>

                Appointments
            </a>

            <a href="${pageContext.request.contextPath}/bills">

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
        </nav>

        <div class="sidebar-footer">

            <a href="${pageContext.request.contextPath}/logout">
                Sign out
            </a>

        </div>

    </aside>

    <!-- MAIN -->
    <main class="main">

        <header class="topbar">

            <div class="page-title">

                <h1>
                    Dashboard
                </h1>

                <p>
                    Sunrise Dental Clinic operations
                </p>

            </div>

            <div class="user-box">

                <div class="user-details">

                    <strong>
                        ${sessionScope.fullName}
                    </strong>

                    <span>
                        ${sessionScope.role}
                    </span>

                </div>

                <div class="avatar">
                    ${sessionScope.username.substring(0,1).toUpperCase()}
                </div>

            </div>

        </header>

        <section class="content">

            <div class="welcome-panel">

                <div>

                    <h2>
                        Welcome, ${sessionScope.fullName}
                    </h2>

                    <p>
                        Manage appointments, patient records,
                        treatments and billing from one secure
                        workspace.
                    </p>

                </div>

                <div class="welcome-icon">
                    &#10010;
                </div>

            </div>

            <div class="section-heading">

                <h2>
                    Clinic Management
                </h2>

                <p>
                    Select a module to continue.
                </p>

            </div>

            <div class="module-grid">

                <a
                    class="module-card"
                    href="${pageContext.request.contextPath}/patients">

                    <div class="module-icon">
                        &#128100;
                    </div>

                    <h3>
                        Patients
                    </h3>

                    <p>
                        Register, search and update
                        patient information.
                    </p>

                </a>

                <a
                    class="module-card"
                    href="${pageContext.request.contextPath}/appointments">

                    <div class="module-icon">
                        &#128197;
                    </div>

                    <h3>
                        Appointments
                    </h3>

                    <p>
                        Schedule appointments and prevent
                        dentist booking conflicts.
                    </p>

                </a>

                <a
                    class="module-card"
                    href="${pageContext.request.contextPath}/treatments">

                    <div class="module-icon">
                        &#10010;
                    </div>

                    <h3>
                        Treatments
                    </h3>

                    <p>
                        Manage treatment details,
                        prices and consultation fees.
                    </p>

                </a>

                <a
                    class="module-card"
                    href="${pageContext.request.contextPath}/bills">

                    <div class="module-icon">
                        &#128179;
                    </div>

                    <h3>
                        Billing
                    </h3>

                    <p>
                        Generate bills, review charges
                        and manage payments.
                    </p>

                </a>

            </div>

            <div class="bottom-grid">

                <div class="panel">

                    <h3>
                        Standard Clinic Workflow
                    </h3>

                    <div class="workflow">

                        <div class="workflow-item">

                            <div class="workflow-number">
                                1
                            </div>

                            <div class="workflow-text">

                                <strong>
                                    Register Patient
                                </strong>

                                <span>
                                    Capture and maintain patient
                                    information securely.
                                </span>

                            </div>

                        </div>

                        <div class="workflow-item">

                            <div class="workflow-number">
                                2
                            </div>

                            <div class="workflow-text">

                                <strong>
                                    Schedule Appointment
                                </strong>

                                <span>
                                    Select dentist, treatment,
                                    date and available time.
                                </span>

                            </div>

                        </div>

                        <div class="workflow-item">

                            <div class="workflow-number">
                                3
                            </div>

                            <div class="workflow-text">

                                <strong>
                                    Complete Treatment
                                </strong>

                                <span>
                                    Maintain accurate treatment
                                    and appointment information.
                                </span>

                            </div>

                        </div>

                        <div class="workflow-item">

                            <div class="workflow-number">
                                4
                            </div>

                            <div class="workflow-text">

                                <strong>
                                    Generate Patient Bill
                                </strong>

                                <span>
                                    Calculate treatment and
                                    consultation charges.
                                </span>

                            </div>

                        </div>

                    </div>

                </div>

                <div class="panel">

                    <h3>
                        Session Information
                    </h3>

                    <div class="system-info">

                        <div class="info-row">

                            <span>
                                Username
                            </span>

                            <strong>
                                ${sessionScope.username}
                            </strong>

                        </div>

                        <div class="info-row">

                            <span>
                                Role
                            </span>

                            <strong>
                                ${sessionScope.role}
                            </strong>

                        </div>

                        <div class="info-row">

                            <span>
                                Authentication
                            </span>

                            <strong class="online">
                                Active
                            </strong>

                        </div>

                        <div class="info-row">

                            <span>
                                API Connection
                            </span>

                            <strong class="online">
                                Connected
                            </strong>

                        </div>

                    </div>

                </div>

            </div>

        </section>

    </main>

</div>

</body>

</html>