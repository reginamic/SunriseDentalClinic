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
%>

<%
    String errorMessage =
            (String)
            request.getAttribute("errorMessage");

    String treatmentName =
            (String)
            request.getAttribute("treatmentName");

    String description =
            (String)
            request.getAttribute("description");

    String treatmentPrice =
            (String)
            request.getAttribute("treatmentPrice");

    String consultationFee =
            (String)
            request.getAttribute("consultationFee");

    String estimatedDurationMinutes =
            (String)
            request.getAttribute(
                    "estimatedDurationMinutes"
            );

    String fullName =
            String.valueOf(
                    session.getAttribute("fullName")
            );

    String role =
            String.valueOf(
                    session.getAttribute("role")
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
        Register Treatment | Sunrise Dental Clinic
    </title>

    <style>

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            min-height: 100vh;
            font-family: "Segoe UI", Arial, sans-serif;
            background: #f4f7fb;
            color: #1e293b;
        }

        .layout {
            min-height: 100vh;
            display: grid;
            grid-template-columns: 250px 1fr;
        }

        .sidebar {
            background: #102a43;
            color: white;
            padding: 28px 18px;
            display: flex;
            flex-direction: column;
        }

        .brand {
            padding: 0 10px 28px;
            border-bottom:
                1px solid rgba(255,255,255,0.10);
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
            color: #cbdbea;
            text-decoration: none;
            padding: 12px 13px;
            border-radius: 8px;
            font-size: 13px;
        }

        .navigation a:hover,
        .navigation a.active {
            background: #1f5f99;
            color: white;
        }

        .sidebar-footer {
            margin-top: auto;
            padding: 20px 10px 0;
            border-top:
                1px solid rgba(255,255,255,0.10);
        }

        .sidebar-footer a {
            color: #cbdbea;
            text-decoration: none;
            font-size: 12px;
        }

        .main {
            min-width: 0;
        }

        .topbar {
            height: 74px;
            background: white;
            border-bottom: 1px solid #e2e8f0;
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
            color: #64748b;
            font-size: 12px;
            margin-top: 3px;
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

        .content {
            padding: 30px 32px;
            max-width: 1150px;
        }

        .page-heading {
            margin-bottom: 22px;
        }

        .page-heading h2 {
            color: #102a43;
            font-size: 24px;
            margin-bottom: 7px;
        }

        .page-heading p {
            color: #64748b;
            font-size: 12px;
            line-height: 1.6;
        }

        .admin-notice {
            background: #eff6ff;
            border: 1px solid #bfdbfe;
            color: #1e40af;

            padding: 13px 16px;
            border-radius: 9px;
            margin-bottom: 20px;
            font-size: 11px;
        }

        .error-message {
            background: #fef2f2;
            border: 1px solid #fecaca;
            color: #b91c1c;

            padding: 13px 16px;
            border-radius: 9px;
            margin-bottom: 20px;
            font-size: 12px;
        }

        .form-panel {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            overflow: hidden;
        }

        .form-header {
            padding: 21px 24px;
            border-bottom: 1px solid #e2e8f0;
        }

        .form-header h3 {
            color: #102a43;
            font-size: 16px;
        }

        .form-header p {
            color: #64748b;
            font-size: 11px;
            margin-top: 5px;
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
            border: 1px solid #cbd5e1;
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
            transform: translateY(-50%);

            color: #64748b;
            font-size: 11px;
            font-weight: 600;
        }

        .money-wrapper input {
            padding-left: 40px;
        }

        .summary-box {
            margin-top: 23px;

            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 9px;

            padding: 16px;

            display: grid;
            grid-template-columns:
                repeat(3,1fr);
            gap: 15px;
        }

        .summary-item span {
            display: block;
            color: #64748b;
            font-size: 9px;
            text-transform: uppercase;
            font-weight: 700;
        }

        .summary-item strong {
            display: block;
            color: #102a43;
            margin-top: 5px;
            font-size: 15px;
        }

        .form-actions {
            margin-top: 25px;

            padding-top: 20px;
            border-top: 1px solid #e2e8f0;

            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }

        .button {
            min-height: 40px;

            padding: 0 18px;

            border-radius: 8px;
            border: none;

            display: inline-flex;
            align-items: center;
            justify-content: center;

            font-family: inherit;
            font-size: 12px;
            font-weight: 600;

            text-decoration: none;
            cursor: pointer;
        }

        .button-secondary {
            background: white;
            color: #475569;
            border: 1px solid #cbd5e1;
        }

        .button-primary {
            background: #153e75;
            color: white;
        }

        .button-primary:hover {
            background: #102f57;
        }

        .button-primary:disabled {
            opacity: 0.65;
            cursor: not-allowed;
        }

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

            .form-grid,
            .summary-box {
                grid-template-columns: 1fr;
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


    <main class="main">

        <header class="topbar">

            <div>

                <h1>
                    Register Treatment
                </h1>

                <p>
                    Add a new clinical service and pricing configuration
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

                <h2>
                    New Treatment
                </h2>

                <p>
                    Configure treatment pricing, consultation fees
                    and expected clinical duration.
                    A treatment code will be generated by the system.
                </p>

            </div>


            <div class="admin-notice">

                <strong>ADMIN ONLY:</strong>
                Treatment catalogue configuration affects
                appointment selection and billing calculations.

            </div>


            <% if (errorMessage != null
                    && !errorMessage.isBlank()) { %>

                <div class="error-message">

                    <%= escapeHtml(errorMessage) %>

                </div>

            <% } %>


            <div class="form-panel">

                <div class="form-header">

                    <h3>
                        Treatment Information
                    </h3>

                    <p>
                        Fields marked * are required.
                    </p>

                </div>


                <form
                    id="treatmentForm"
                    method="post"
                    action="<%= request.getContextPath() %>/treatments/new">

                    <div class="form-body">

                        <div class="form-grid">


                            <div class="form-group full-width">

                                <label for="treatmentName">

                                    Treatment Name
                                    <span class="required">*</span>

                                </label>

                                <input
                                    type="text"
                                    id="treatmentName"
                                    name="treatmentName"
                                    minlength="3"
                                    maxlength="100"
                                    required
                                    value="<%= escapeHtml(treatmentName) %>"
                                    placeholder="e.g. Dental Implant">

                                <span class="help-text">
                                    Clinical service name shown during
                                    appointment and billing operations.
                                </span>

                            </div>


                            <div class="form-group full-width">

                                <label for="description">
                                    Description
                                </label>

                                <textarea
                                    id="description"
                                    name="description"
                                    maxlength="500"
                                    placeholder="Describe the treatment or procedure"><%= escapeHtml(description) %></textarea>

                            </div>


                            <div class="form-group">

                                <label for="treatmentPrice">

                                    Treatment Price
                                    <span class="required">*</span>

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
                                        value="<%= escapeHtml(treatmentPrice) %>"
                                        placeholder="0.00">

                                </div>

                                <span class="help-text">
                                    Clinical treatment cost excluding
                                    consultation fee.
                                </span>

                            </div>


                            <div class="form-group">

                                <label for="consultationFee">

                                    Consultation Fee
                                    <span class="required">*</span>

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
                                        value="<%= escapeHtml(consultationFee) %>"
                                        placeholder="1500.00">

                                </div>

                                <span class="help-text">
                                    Consultation charge added to the
                                    treatment price.
                                </span>

                            </div>


                            <div class="form-group">

                                <label for="estimatedDurationMinutes">

                                    Estimated Duration
                                    <span class="required">*</span>

                                </label>

                                <input
                                    type="number"
                                    id="estimatedDurationMinutes"
                                    name="estimatedDurationMinutes"
                                    min="1"
                                    step="1"
                                    required
                                    value="<%= escapeHtml(estimatedDurationMinutes) %>"
                                    placeholder="60">

                                <span class="help-text">
                                    Expected treatment duration in minutes.
                                </span>

                            </div>


                        </div>


                        <div class="summary-box">

                            <div class="summary-item">

                                <span>
                                    Treatment
                                </span>

                                <strong id="summaryTreatment">
                                    Not entered
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


                        <div class="form-actions">

                            <a
                                href="<%= request.getContextPath() %>/treatments"
                                class="button button-secondary">

                                Cancel

                            </a>

                            <button
                                type="submit"
                                id="submitButton"
                                class="button button-primary">

                                Register Treatment

                            </button>

                        </div>

                    </div>

                </form>

            </div>

        </section>

    </main>

</div>


<script>

    const treatmentForm =
            document.getElementById(
                "treatmentForm"
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

    const submitButton =
            document.getElementById(
                "submitButton"
            );


    function updateSummary() {

        const name =
                treatmentNameInput.value.trim();

        const treatmentPrice =
                parseFloat(
                    treatmentPriceInput.value
                ) || 0;

        const consultationFee =
                parseFloat(
                    consultationFeeInput.value
                ) || 0;

        const duration =
                parseInt(
                    durationInput.value
                ) || 0;

        const standardCharge =
                treatmentPrice
                + consultationFee;


        summaryTreatment.textContent =
                name === ""
                        ? "Not entered"
                        : name;


        summaryCharge.textContent =
                "Rs. "
                + standardCharge.toLocaleString(
                    "en-LK",
                    {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                    }
                );


        summaryDuration.textContent =
                duration
                + " min";
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


    treatmentForm.addEventListener(
        "submit",
        function () {

            if (treatmentForm.checkValidity()) {

                submitButton.disabled = true;

                submitButton.textContent =
                        "Registering...";
            }
        }
    );


    updateSummary();

</script>

</body>

</html>