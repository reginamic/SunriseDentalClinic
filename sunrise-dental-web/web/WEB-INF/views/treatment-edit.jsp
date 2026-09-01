<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@page import="com.sunrisedental.web.model.TreatmentViewModel"%>

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

    private String safeSessionValue(Object value) {

        return value == null
                ? ""
                : String.valueOf(value);
    }
%>

<%
    TreatmentViewModel treatment =
            (TreatmentViewModel)
            request.getAttribute("treatment");

    String errorMessage =
            (String)
            request.getAttribute("errorMessage");


    String treatmentId;

    String treatmentCode;

    String treatmentName;

    String description;

    String treatmentPrice;

    String consultationFee;

    String estimatedDurationMinutes;

    boolean active;


    /*
     * When validation fails, use the submitted values
     * so the administrator does not lose form data.
     */
    if (request.getAttribute(
            "submittedTreatmentId") != null) {

        treatmentId =
                String.valueOf(
                        request.getAttribute(
                                "submittedTreatmentId"
                        )
                );

        treatmentCode =
                treatment == null
                        ? ""
                        : treatment.getTreatmentCode();

        treatmentName =
                (String)
                request.getAttribute(
                        "submittedTreatmentName"
                );

        description =
                (String)
                request.getAttribute(
                        "submittedDescription"
                );

        treatmentPrice =
                (String)
                request.getAttribute(
                        "submittedTreatmentPrice"
                );

        consultationFee =
                (String)
                request.getAttribute(
                        "submittedConsultationFee"
                );

        estimatedDurationMinutes =
                (String)
                request.getAttribute(
                        "submittedEstimatedDurationMinutes"
                );

        Object submittedActive =
                request.getAttribute(
                        "submittedActive"
                );

        active =
                submittedActive != null
                && Boolean.parseBoolean(
                        String.valueOf(
                                submittedActive
                        )
                );

    } else {

        treatmentId =
                treatment == null
                        ? ""
                        : String.valueOf(
                                treatment.getTreatmentId()
                        );

        treatmentCode =
                treatment == null
                        ? ""
                        : treatment.getTreatmentCode();

        treatmentName =
                treatment == null
                        ? ""
                        : treatment.getTreatmentName();

        description =
                treatment == null
                        ? ""
                        : treatment.getDescription();

        treatmentPrice =
                treatment == null
                        ? ""
                        : treatment
                                .getTreatmentPrice()
                                .toPlainString();

        consultationFee =
                treatment == null
                        ? ""
                        : treatment
                                .getConsultationFee()
                                .toPlainString();

        estimatedDurationMinutes =
                treatment == null
                        ? ""
                        : String.valueOf(
                                treatment
                                    .getEstimatedDurationMinutes()
                        );

        active =
                treatment != null
                && treatment.isActive();
    }


    String fullName =
            safeSessionValue(
                    session.getAttribute(
                            "fullName"
                    )
            );

    String role =
            safeSessionValue(
                    session.getAttribute(
                            "role"
                    )
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
        Edit Treatment | Sunrise Dental Clinic
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


        /* SIDEBAR */

        .sidebar {
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
                rgba(255,255,255,0.10);
        }

        .brand-mark {
            width: 46px;
            height: 46px;

            border-radius: 12px;

            background:
                rgba(255,255,255,0.12);

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
            color: #cbdbea;

            text-decoration: none;

            padding: 12px 13px;

            border-radius: 8px;

            font-size: 13px;
        }

        .navigation a:hover,
        .navigation a.active {
            background: #1f5f99;
            color: #ffffff;
        }

        .sidebar-footer {
            margin-top: auto;

            padding: 20px 10px 0;

            border-top:
                1px solid
                rgba(255,255,255,0.10);
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

            padding: 0 32px;

            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .topbar h1 {
            color: #102a43;

            font-size: 21px;
        }

        .topbar p {
            margin-top: 3px;

            color: #64748b;

            font-size: 12px;
        }

        .user-info {
            text-align: right;
        }

        .user-info strong {
            display: block;

            font-size: 13px;
        }

        .user-info span {
            color: #64748b;

            font-size: 11px;
        }


        /* CONTENT */

        .content {
            max-width: 1150px;

            padding: 30px 32px;
        }

        .page-heading {
            display: flex;

            justify-content: space-between;

            align-items: flex-start;

            gap: 20px;

            margin-bottom: 22px;
        }

        .page-heading h2 {
            color: #102a43;

            font-size: 24px;

            margin-bottom: 6px;
        }

        .page-heading p {
            color: #64748b;

            font-size: 12px;

            line-height: 1.6;
        }

        .code-badge {
            background: #eaf2fb;

            border:
                1px solid #d5e5f5;

            border-radius: 8px;

            color: #153e75;

            padding: 10px 15px;

            font-size: 12px;
            font-weight: 700;

            white-space: nowrap;
        }


        /* NOTICES */

        .admin-notice {
            margin-bottom: 20px;

            padding: 13px 16px;

            border:
                1px solid #bfdbfe;

            border-radius: 9px;

            background: #eff6ff;

            color: #1e40af;

            font-size: 11px;
        }

        .error-message {
            margin-bottom: 20px;

            padding: 13px 16px;

            border:
                1px solid #fecaca;

            border-radius: 9px;

            background: #fef2f2;

            color: #b91c1c;

            font-size: 12px;
        }


        /* FORM */

        .form-panel {
            overflow: hidden;

            background: #ffffff;

            border:
                1px solid #e2e8f0;

            border-radius: 12px;
        }

        .form-header {
            padding: 21px 24px;

            border-bottom:
                1px solid #e2e8f0;

            display: flex;

            justify-content: space-between;

            align-items: center;

            gap: 20px;
        }

        .form-header h3 {
            color: #102a43;

            font-size: 16px;
        }

        .form-header p {
            margin-top: 5px;

            color: #64748b;

            font-size: 11px;
        }

        .current-status {
            display: inline-flex;

            align-items: center;

            gap: 6px;

            border-radius: 20px;

            padding: 6px 10px;

            font-size: 10px;
            font-weight: 700;
        }

        .current-status.active {
            background: #ecfdf5;

            color: #047857;
        }

        .current-status.inactive {
            background: #f1f5f9;

            color: #64748b;
        }

        .status-dot {
            width: 6px;
            height: 6px;

            border-radius: 50%;

            background: currentColor;
        }

        .form-body {
            padding: 25px;
        }

        .form-grid {
            display: grid;

            grid-template-columns:
                repeat(2, minmax(0,1fr));

            gap: 20px;
        }

        .form-group {
            display: flex;

            flex-direction: column;

            gap: 7px;
        }

        .form-group.full-width {
            grid-column: 1 / -1;
        }

        label {
            color: #334155;

            font-size: 11px;

            font-weight: 700;
        }

        .required {
            color: #dc2626;
        }

        input,
        textarea {
            width: 100%;

            border:
                1px solid #cbd5e1;

            border-radius: 8px;

            padding: 11px 12px;

            font-family: inherit;

            font-size: 12px;

            outline: none;
        }

        textarea {
            min-height: 110px;

            resize: vertical;
        }

        input:focus,
        textarea:focus {
            border-color: #2563a6;

            box-shadow:
                0 0 0 3px
                rgba(37,99,166,0.10);
        }

        .help-text {
            color: #94a3b8;

            font-size: 10px;

            line-height: 1.5;
        }

        .money-wrapper {
            position: relative;
        }

        .money-prefix {
            position: absolute;

            left: 12px;

            top: 50%;

            transform:
                translateY(-50%);

            color: #64748b;

            font-size: 11px;

            font-weight: 600;
        }

        .money-wrapper input {
            padding-left: 40px;
        }


        /* STATUS CONTROL */

        .status-control {
            margin-top: 23px;

            padding: 17px;

            border:
                1px solid #e2e8f0;

            border-radius: 9px;

            background: #f8fafc;

            display: flex;

            justify-content: space-between;

            align-items: center;

            gap: 20px;
        }

        .status-description strong {
            display: block;

            color: #102a43;

            font-size: 12px;
        }

        .status-description span {
            display: block;

            margin-top: 5px;

            color: #64748b;

            font-size: 10px;

            line-height: 1.5;
        }

        .switch {
            position: relative;

            display: inline-block;

            width: 48px;
            height: 26px;

            flex-shrink: 0;
        }

        .switch input {
            opacity: 0;

            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;

            cursor: pointer;

            inset: 0;

            background: #cbd5e1;

            transition: 0.2s;

            border-radius: 30px;
        }

        .slider::before {
            content: "";

            position: absolute;

            width: 20px;
            height: 20px;

            left: 3px;
            bottom: 3px;

            background: #ffffff;

            transition: 0.2s;

            border-radius: 50%;
        }

        .switch input:checked
        + .slider {

            background: #15803d;
        }

        .switch input:checked
        + .slider::before {

            transform:
                translateX(22px);
        }


        /* SUMMARY */

        .summary-box {
            margin-top: 23px;

            display: grid;

            grid-template-columns:
                repeat(3, 1fr);

            gap: 15px;

            padding: 16px;

            border:
                1px solid #e2e8f0;

            border-radius: 9px;

            background: #f8fafc;
        }

        .summary-item span {
            display: block;

            color: #64748b;

            font-size: 9px;

            font-weight: 700;

            text-transform: uppercase;
        }

        .summary-item strong {
            display: block;

            margin-top: 5px;

            color: #102a43;

            font-size: 15px;
        }


        /* ACTIONS */

        .form-actions {
            margin-top: 25px;

            padding-top: 20px;

            border-top:
                1px solid #e2e8f0;

            display: flex;

            justify-content: space-between;

            gap: 10px;
        }

        .right-actions {
            display: flex;

            gap: 10px;
        }

        .button {
            min-height: 40px;

            padding: 0 18px;

            border-radius: 8px;

            display: inline-flex;

            align-items: center;

            justify-content: center;

            font-family: inherit;

            font-size: 12px;

            font-weight: 600;

            text-decoration: none;

            cursor: pointer;
        }

        .button-back {
            background: #f8fafc;

            color: #475569;

            border:
                1px solid #e2e8f0;
        }

        .button-secondary {
            background: #ffffff;

            color: #475569;

            border:
                1px solid #cbd5e1;
        }

        .button-primary {
            border: none;

            background: #153e75;

            color: #ffffff;
        }

        .button-primary:hover {
            background: #102f57;
        }

        .button-primary:disabled {
            opacity: 0.65;

            cursor: not-allowed;
        }


        /* RESPONSIVE */

        @media(max-width:850px) {

            .layout {
                grid-template-columns: 1fr;
            }

            .sidebar {
                display: none;
            }
        }

        @media(max-width:650px) {

            .content {
                padding: 20px;
            }

            .page-heading {
                flex-direction: column;
            }

            .form-grid,
            .summary-box {
                grid-template-columns: 1fr;
            }

            .status-control {
                align-items: flex-start;
            }

            .form-actions,
            .right-actions {
                flex-direction: column;
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
                Dashboard
            </a>

            <a href="<%= request.getContextPath() %>/patients">
                Patients
            </a>

            <a href="<%= request.getContextPath() %>/dentists">
                Dentists
            </a>

            <a
                class="active"
                href="<%= request.getContextPath() %>/treatments">

                Treatments

            </a>

            <a href="<%= request.getContextPath() %>/appointments">
                Appointments
            </a>

            <a href="<%= request.getContextPath() %>/bills">
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

            <div>

                <h1>
                    Edit Treatment
                </h1>

                <p>
                    Review clinical service and pricing configuration
                </p>

            </div>

            <div class="user-info">

                <strong>
                    <%= escapeHtml(fullName) %>
                </strong>

                <span>
                    <%= escapeHtml(role) %>
                </span>

            </div>

        </header>


        <section class="content">


            <div class="page-heading">

                <div>

                    <h2>
                        Treatment Details
                    </h2>

                    <p>
                        Review or update treatment pricing,
                        consultation charges, expected duration
                        and operational status.
                    </p>

                </div>

                <div class="code-badge">

                    <%= treatmentCode == null
                            || treatmentCode.isBlank()
                            ? "Treatment Record"
                            : escapeHtml(treatmentCode) %>

                </div>

            </div>


            <div class="admin-notice">

                <strong>ADMIN ONLY:</strong>
                Changes made here affect appointment availability
                and billing calculations throughout the system.

            </div>


            <% if (errorMessage != null
                    && !errorMessage.isBlank()) { %>

                <div class="error-message">

                    <%= escapeHtml(errorMessage) %>

                </div>

            <% } %>


            <div class="form-panel">


                <div class="form-header">

                    <div>

                        <h3>
                            Clinical Service Configuration
                        </h3>

                        <p>
                            Treatment code is system-generated
                            and cannot be edited.
                        </p>

                    </div>


                    <span
                        id="currentStatusBadge"
                        class="current-status <%= active
                                ? "active"
                                : "inactive" %>">

                        <span class="status-dot">
                        </span>

                        <span id="currentStatusText">

                            <%= active
                                    ? "Active"
                                    : "Inactive" %>

                        </span>

                    </span>

                </div>


                <form
                    id="treatmentEditForm"
                    method="post"
                    action="<%= request.getContextPath() %>/treatments/edit">


                    <input
                        type="hidden"
                        name="treatmentId"
                        value="<%= escapeHtml(treatmentId) %>">


                    <div class="form-body">


                        <div class="form-grid">


                            <!-- NAME -->

                            <div class="form-group full-width">

                                <label for="treatmentName">

                                    Treatment Name

                                    <span class="required">
                                        *
                                    </span>

                                </label>

                                <input
                                    type="text"
                                    id="treatmentName"
                                    name="treatmentName"
                                    minlength="3"
                                    maxlength="100"
                                    required
                                    value="<%= escapeHtml(treatmentName) %>">

                            </div>


                            <!-- DESCRIPTION -->

                            <div class="form-group full-width">

                                <label for="description">

                                    Description

                                </label>

                                <textarea
                                    id="description"
                                    name="description"
                                    maxlength="500"><%= escapeHtml(description) %></textarea>

                            </div>


                            <!-- PRICE -->

                            <div class="form-group">

                                <label for="treatmentPrice">

                                    Treatment Price

                                    <span class="required">
                                        *
                                    </span>

                                </label>

                                <div class="money-wrapper">

                                    <span class="money-prefix">
                                        Rs.
                                    </span>

                                    <input
                                        type="number"
                                        id="treatmentPrice"
                                        name="treatmentPrice"
                                        min="0"
                                        step="0.01"
                                        required
                                        value="<%= escapeHtml(treatmentPrice) %>">

                                </div>

                                <span class="help-text">

                                    Treatment amount before
                                    consultation charges.

                                </span>

                            </div>


                            <!-- CONSULTATION -->

                            <div class="form-group">

                                <label for="consultationFee">

                                    Consultation Fee

                                    <span class="required">
                                        *
                                    </span>

                                </label>

                                <div class="money-wrapper">

                                    <span class="money-prefix">
                                        Rs.
                                    </span>

                                    <input
                                        type="number"
                                        id="consultationFee"
                                        name="consultationFee"
                                        min="0"
                                        step="0.01"
                                        required
                                        value="<%= escapeHtml(consultationFee) %>">

                                </div>

                            </div>


                            <!-- DURATION -->

                            <div class="form-group">

                                <label for="estimatedDurationMinutes">

                                    Estimated Duration

                                    <span class="required">
                                        *
                                    </span>

                                </label>

                                <input
                                    type="number"
                                    id="estimatedDurationMinutes"
                                    name="estimatedDurationMinutes"
                                    min="1"
                                    step="1"
                                    required
                                    value="<%= escapeHtml(
                                            estimatedDurationMinutes
                                    ) %>">

                                <span class="help-text">

                                    Expected procedure time
                                    in minutes.

                                </span>

                            </div>


                        </div>


                        <!-- ACTIVE / INACTIVE -->

                        <div class="status-control">

                            <div class="status-description">

                                <strong>
                                    Treatment Availability
                                </strong>

                                <span>

                                    Active treatments can be used
                                    in new appointments and billing.
                                    Inactive treatments remain preserved
                                    for historical records.

                                </span>

                            </div>


                            <label class="switch">

                                <input
                                    type="checkbox"
                                    id="active"
                                    name="active"
                                    value="true"
                                    <%= active
                                            ? "checked"
                                            : "" %>>

                                <span class="slider">
                                </span>

                            </label>

                        </div>


                        <!-- LIVE SUMMARY -->

                        <div class="summary-box">


                            <div class="summary-item">

                                <span>
                                    Treatment
                                </span>

                                <strong id="summaryTreatment">

                                    <%= escapeHtml(treatmentName) %>

                                </strong>

                            </div>


                            <div class="summary-item">

                                <span>
                                    Standard Charge
                                </span>

                                <strong id="summaryCharge">

                                    Rs. 0.00

                                </strong>

                            </div>


                            <div class="summary-item">

                                <span>
                                    Duration
                                </span>

                                <strong id="summaryDuration">

                                    0 min

                                </strong>

                            </div>


                        </div>


                        <!-- ACTIONS -->

                        <div class="form-actions">


                            <a
                                href="<%= request.getContextPath() %>/treatments"
                                class="button button-back">

                                &#8592; Back to Treatments

                            </a>


                            <div class="right-actions">


                                <a
                                    href="<%= request.getContextPath() %>/treatments"
                                    class="button button-secondary">

                                    Cancel

                                </a>


                                <button
                                    type="submit"
                                    id="saveButton"
                                    class="button button-primary">

                                    Save Changes

                                </button>


                            </div>

                        </div>


                    </div>

                </form>


            </div>

        </section>

    </main>

</div>


<script>

    const treatmentEditForm =
            document.getElementById(
                "treatmentEditForm"
            );

    const treatmentNameInput =
            document.getElementById(
                "treatmentName"
            );

    const treatmentPriceInput =
            document.getElementById(
                "treatmentPrice"
            );

    const consultationFeeInput =
            document.getElementById(
                "consultationFee"
            );

    const durationInput =
            document.getElementById(
                "estimatedDurationMinutes"
            );

    const activeInput =
            document.getElementById(
                "active"
            );

    const summaryTreatment =
            document.getElementById(
                "summaryTreatment"
            );

    const summaryCharge =
            document.getElementById(
                "summaryCharge"
            );

    const summaryDuration =
            document.getElementById(
                "summaryDuration"
            );

    const statusBadge =
            document.getElementById(
                "currentStatusBadge"
            );

    const statusText =
            document.getElementById(
                "currentStatusText"
            );

    const saveButton =
            document.getElementById(
                "saveButton"
            );


    function updateSummary() {

        const name =
                treatmentNameInput.value.trim();

        const price =
                parseFloat(
                    treatmentPriceInput.value
                ) || 0;

        const consultation =
                parseFloat(
                    consultationFeeInput.value
                ) || 0;

        const duration =
                parseInt(
                    durationInput.value
                ) || 0;


        summaryTreatment.textContent =
                name === ""
                        ? "Not entered"
                        : name;


        const standardCharge =
                price + consultation;


        summaryCharge.textContent =
                "Rs. "
                + standardCharge.toLocaleString(
                    "en-LK",
                    {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                    }
                );


        if (duration < 60) {

            summaryDuration.textContent =
                    duration + " min";

        } else {

            const hours =
                    Math.floor(
                        duration / 60
                    );

            const minutes =
                    duration % 60;


            if (minutes === 0) {

                summaryDuration.textContent =
                        hours === 1
                                ? "1 hr"
                                : hours + " hrs";

            } else {

                summaryDuration.textContent =
                        hours
                        + " hr "
                        + minutes
                        + " min";
            }
        }
    }


    function updateStatus() {

        if (activeInput.checked) {

            statusBadge.classList.remove(
                "inactive"
            );

            statusBadge.classList.add(
                "active"
            );

            statusText.textContent =
                    "Active";

        } else {

            statusBadge.classList.remove(
                "active"
            );

            statusBadge.classList.add(
                "inactive"
            );

            statusText.textContent =
                    "Inactive";
        }
    }


    treatmentNameInput.addEventListener(
        "input",
        updateSummary
    );

    treatmentPriceInput.addEventListener(
        "input",
        updateSummary
    );

    consultationFeeInput.addEventListener(
        "input",
        updateSummary
    );

    durationInput.addEventListener(
        "input",
        updateSummary
    );

    activeInput.addEventListener(
        "change",
        updateStatus
    );


    treatmentEditForm.addEventListener(
        "submit",
        function () {

            if (treatmentEditForm.checkValidity()) {

                saveButton.disabled = true;

                saveButton.textContent =
                        "Saving...";
            }
        }
    );


    updateSummary();

    updateStatus();

</script>

</body>

</html>