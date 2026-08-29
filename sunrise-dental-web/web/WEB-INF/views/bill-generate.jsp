<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="com.sunrisedental.web.model.AppointmentViewModel" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>


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


    private String displayValue(String value) {

        if (value == null || value.isBlank()) {
            return "-";
        }

        return escapeHtml(value);
    }


    private String formatMoney(Double amount) {

        if (amount == null) {
            amount = 0.0;
        }

        DecimalFormat formatter =
                new DecimalFormat("#,##0.00");

        return formatter.format(amount);
    }


    private String valueOrEmpty(Object value) {

        return value == null
                ? ""
                : value.toString();
    }
%>


<%
    String contextPath =
            request.getContextPath();


    List<AppointmentViewModel> appointments =
            (List<AppointmentViewModel>)
                    request.getAttribute(
                            "appointments"
                    );


    if (appointments == null) {

        appointments =
                Collections.emptyList();
    }


    String selectedAppointmentId =
            valueOrEmpty(
                    request.getAttribute(
                            "selectedAppointmentId"
                    )
            );


    String submittedAdditionalCharge =
            valueOrEmpty(
                    request.getAttribute(
                            "submittedAdditionalCharge"
                    )
            );


    String submittedDiscountAmount =
            valueOrEmpty(
                    request.getAttribute(
                            "submittedDiscountAmount"
                    )
            );


    if (submittedAdditionalCharge.isBlank()) {
        submittedAdditionalCharge = "0.00";
    }


    if (submittedDiscountAmount.isBlank()) {
        submittedDiscountAmount = "0.00";
    }


    String errorMessage =
            valueOrEmpty(
                    request.getAttribute(
                            "errorMessage"
                    )
            );


    String fullName =
            valueOrEmpty(
                    session.getAttribute(
                            "fullName"
                    )
            );


    String role =
            valueOrEmpty(
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
        Generate Bill | Sunrise Dental Clinic
    </title>


    <style>

        * {
            box-sizing: border-box;
        }


        body {
            margin: 0;
            font-family:
                    "Segoe UI",
                    Arial,
                    sans-serif;
            background: #f4f7fb;
            color: #1f2937;
        }


        .page-container {
            min-height: 100vh;
            display: flex;
        }


        /* =====================================================
           SIDEBAR
           ===================================================== */

        .sidebar {
            width: 245px;
            background: #153f63;
            color: white;
            min-height: 100vh;
            padding: 24px 18px;
            position: fixed;
            top: 0;
            left: 0;
            bottom: 0;
        }


        .brand {
            margin-bottom: 32px;
        }


        .brand h2 {
            margin: 0;
            font-size: 21px;
        }


        .brand p {
            margin: 6px 0 0;
            font-size: 12px;
            color: #d9e8f5;
        }


        .nav-link {
            display: block;
            padding: 12px 14px;
            margin-bottom: 7px;
            border-radius: 8px;
            text-decoration: none;
            color: #e9f3fb;
            font-size: 14px;
        }


        .nav-link:hover {
            background: rgba(255,255,255,0.12);
        }


        .nav-link.active {
            background: white;
            color: #153f63;
            font-weight: 700;
        }


        .logout-link {
            margin-top: 28px;
            border-top:
                    1px solid rgba(255,255,255,0.2);
            padding-top: 18px;
        }


        /* =====================================================
           MAIN
           ===================================================== */

        .main-content {
            margin-left: 245px;
            width: calc(100% - 245px);
            padding: 28px;
        }


        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
        }


        .page-title h1 {
            margin: 0;
            color: #153f63;
            font-size: 28px;
        }


        .page-title p {
            margin: 6px 0 0;
            color: #6b7280;
            font-size: 14px;
        }


        .user-panel {
            text-align: right;
        }


        .user-panel strong {
            display: block;
            color: #153f63;
        }


        .role-badge {
            display: inline-block;
            margin-top: 4px;
            padding: 4px 10px;
            border-radius: 14px;
            background: #e7f1f8;
            color: #153f63;
            font-size: 11px;
            font-weight: 700;
        }


        /* =====================================================
           CARD / ACTIONS
           ===================================================== */

        .card {
            max-width: 1050px;
            margin: 0 auto 22px;
            background: white;
            border: 1px solid #e1e8ef;
            border-radius: 12px;
            box-shadow:
                    0 3px 13px rgba(0,0,0,0.05);
            padding: 24px;
        }


        .actions {
            max-width: 1050px;
            margin: 0 auto 18px;
        }


        .btn {
            display: inline-block;
            padding: 10px 15px;
            border: none;
            border-radius: 7px;
            text-decoration: none;
            cursor: pointer;
            font-family: inherit;
            font-size: 13px;
            font-weight: 700;
        }


        .btn-primary {
            background: #153f63;
            color: white;
        }


        .btn-primary:hover {
            background: #0f304c;
        }


        .btn-secondary {
            background: #edf2f7;
            color: #374151;
        }


        .btn-disabled {
            background: #cbd5df;
            color: #6b7280;
            cursor: not-allowed;
        }


        /* =====================================================
           ALERT
           ===================================================== */

        .alert {
            max-width: 1050px;
            margin: 0 auto 18px;
            padding: 13px 16px;
            border-radius: 8px;
            font-size: 13px;
        }


        .alert-error {
            background: #fff0f0;
            border: 1px solid #efc1c1;
            color: #a02a2a;
        }


        .alert-info {
            background: #eef6fc;
            border: 1px solid #c8deed;
            color: #245779;
        }


        /* =====================================================
           FORM
           ===================================================== */

        .section-title {
            margin: 0 0 18px;
            color: #153f63;
            font-size: 19px;
        }


        .field {
            margin-bottom: 18px;
        }


        .field label {
            display: block;
            margin-bottom: 7px;
            color: #374151;
            font-size: 13px;
            font-weight: 700;
        }


        .field select,
        .field input {
            width: 100%;
            padding: 11px 12px;
            border: 1px solid #ccd7e1;
            border-radius: 7px;
            background: white;
            font-family: inherit;
            font-size: 13px;
        }


        .field select:focus,
        .field input:focus {
            outline: none;
            border-color: #4c87b3;
            box-shadow:
                    0 0 0 2px rgba(76,135,179,0.12);
        }


        .two-column {
            display: grid;
            grid-template-columns:
                    repeat(2, minmax(0,1fr));
            gap: 18px;
        }


        .help-text {
            margin-top: 5px;
            color: #6b7280;
            font-size: 11px;
        }


        /* =====================================================
           APPOINTMENT PREVIEW
           ===================================================== */

        .preview {
            display: none;
            margin-top: 22px;
            border: 1px solid #dfe8ef;
            border-radius: 10px;
            overflow: hidden;
        }


        .preview-header {
            background: #eef4f8;
            color: #153f63;
            padding: 13px 16px;
            font-weight: 800;
        }


        .preview-body {
            padding: 18px;
        }


        .preview-grid {
            display: grid;
            grid-template-columns:
                    repeat(2, minmax(0,1fr));
            gap: 15px 22px;
        }


        .preview-item {
            border-bottom: 1px solid #edf1f4;
            padding-bottom: 9px;
        }


        .preview-label {
            display: block;
            color: #6b7280;
            font-size: 11px;
            font-weight: 700;
            margin-bottom: 4px;
        }


        .preview-value {
            color: #1f2937;
            font-size: 13px;
            font-weight: 600;
        }


        .estimated-cost-box {
            margin-top: 18px;
            padding: 16px;
            border-radius: 9px;
            background: #f8fafc;
            border: 1px solid #dfe7ee;
        }


        .calculation-row {
            display: flex;
            justify-content: space-between;
            gap: 20px;
            padding: 5px 0;
            font-size: 13px;
        }


        .calculation-row span:first-child {
            color: #6b7280;
        }


        .final-preview-total {
            margin-top: 8px;
            padding-top: 10px;
            border-top: 2px solid #153f63;
            color: #153f63;
            font-size: 19px;
            font-weight: 800;
        }


        .form-actions {
            margin-top: 24px;
            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }


        /* =====================================================
           RESPONSIVE
           ===================================================== */

        @media (max-width: 760px) {

            .sidebar {
                position: static;
                width: 100%;
                min-height: auto;
            }


            .page-container {
                display: block;
            }


            .main-content {
                width: 100%;
                margin-left: 0;
                padding: 18px;
            }


            .two-column,
            .preview-grid {
                grid-template-columns: 1fr;
            }
        }

    </style>

</head>


<body>


<div class="page-container">


    <!-- =====================================================
         SIDEBAR
         ===================================================== -->

    <aside class="sidebar">


        <div class="brand">

            <h2>Sunrise Dental</h2>

            <p>Clinic Management System</p>

        </div>


        <a
                class="nav-link"
                href="<%= contextPath %>/dashboard">
            Dashboard
        </a>


        <a
                class="nav-link"
                href="<%= contextPath %>/patients">
            Patients
        </a>


        <a
                class="nav-link"
                href="<%= contextPath %>/appointments">
            Appointments
        </a>


        <a
                class="nav-link active"
                href="<%= contextPath %>/bills">
            Billing
        </a>


        <a
                class="nav-link"
                href="<%= contextPath %>/dentists">
            Dentists
        </a>


        <a
                class="nav-link"
                href="<%= contextPath %>/treatments">
            Treatments
        </a>


        <div class="logout-link">

            <a
                    class="nav-link"
                    href="<%= contextPath %>/logout">
                Logout
            </a>

        </div>


    </aside>


    <!-- =====================================================
         MAIN
         ===================================================== -->

    <main class="main-content">


        <div class="topbar">


            <div class="page-title">

                <h1>Generate Patient Bill</h1>

                <p>
                    Create a final bill for a completed dental
                    appointment.
                </p>

            </div>


            <div class="user-panel">

                <strong>
                    <%= escapeHtml(fullName) %>
                </strong>

                <span class="role-badge">
                    <%= escapeHtml(role) %>
                </span>

            </div>


        </div>


        <div class="actions">

            <a
                    class="btn btn-secondary"
                    href="<%= contextPath %>/bills">

                ← Back to Billing

            </a>

        </div>


        <%
            if (!errorMessage.isBlank()) {
        %>

        <div class="alert alert-error">

            <%= escapeHtml(errorMessage) %>

        </div>

        <%
            }
        %>


        <%
            if (appointments.isEmpty()) {
        %>

        <div class="alert alert-info">

            There are currently no completed appointments
            available for billing. Completed appointments that
            already have a bill are automatically excluded.

        </div>

        <%
            }
        %>


        <section class="card">


            <h2 class="section-title">
                Billing Information
            </h2>


            <form
                    method="post"
                    action="<%= contextPath %>/bill-generate"
                    onsubmit="return validateFinalAmount();">


                <!-- =========================================
                     APPOINTMENT
                     ========================================= -->

                <div class="field">

                    <label for="appointmentId">
                        Completed Appointment *
                    </label>


                    <select
                            id="appointmentId"
                            name="appointmentId"
                            required
                            onchange="updateAppointmentPreview();"
                            <%= appointments.isEmpty()
                                    ? "disabled"
                                    : "" %>>


                        <option value="">
                            Select a completed appointment
                        </option>


                        <%
                            for (
                                    AppointmentViewModel appointment
                                    : appointments
                            ) {

                                String appointmentIdText =
                                        String.valueOf(
                                                appointment.getAppointmentId()
                                        );


                                boolean selected =
                                        appointmentIdText.equals(
                                                selectedAppointmentId
                                        );
                        %>


                        <option
                                value="<%= appointment.getAppointmentId() %>"

                                data-appointment-number="<%= escapeHtml(
                                        appointment.getAppointmentNumber()
                                ) %>"

                                data-patient-code="<%= escapeHtml(
                                        appointment.getPatientCode()
                                ) %>"

                                data-patient-name="<%= escapeHtml(
                                        appointment.getPatientName()
                                ) %>"

                                data-dentist-name="<%= escapeHtml(
                                        appointment.getDentistName()
                                ) %>"

                                data-treatment-name="<%= escapeHtml(
                                        appointment.getTreatmentName()
                                ) %>"

                                data-treatment-price="<%= appointment.getTreatmentPrice() == null
                                        ? "0"
                                        : appointment.getTreatmentPrice() %>"

                                data-consultation-fee="<%= appointment.getConsultationFee() == null
                                        ? "0"
                                        : appointment.getConsultationFee() %>"

                                data-estimated-total="<%= appointment.getEstimatedTotalCost() == null
                                        ? "0"
                                        : appointment.getEstimatedTotalCost() %>"

                                data-date="<%= escapeHtml(
                                        appointment.getAppointmentDate()
                                ) %>"

                                data-time="<%= escapeHtml(
                                        appointment.getAppointmentTime()
                                ) %>"

                                <%= selected
                                        ? "selected"
                                        : "" %>>


                            <%= escapeHtml(
                                    appointment.getAppointmentNumber()
                            ) %>

                            —

                            <%= escapeHtml(
                                    appointment.getPatientName()
                            ) %>

                            —

                            <%= escapeHtml(
                                    appointment.getTreatmentName()
                            ) %>


                        </option>


                        <%
                            }
                        %>


                    </select>


                    <div class="help-text">

                        Only completed appointments without an
                        existing bill are shown.

                    </div>

                </div>


                <!-- =========================================
                     OPTIONAL FINANCIAL ADJUSTMENTS
                     ========================================= -->

                <div class="two-column">


                    <div class="field">

                        <label for="additionalCharge">
                            Additional Clinic Charge (Rs.)
                        </label>

                        <input
                                type="number"
                                id="additionalCharge"
                                name="additionalCharge"
                                min="0"
                                step="0.01"
                                value="<%= escapeHtml(
                                        submittedAdditionalCharge
                                ) %>"
                                oninput="updateCalculation();">

                        <div class="help-text">

                            Optional additional clinic or service
                            charge. Use 0 when not required.

                        </div>

                    </div>


                    <div class="field">

                        <label for="discountAmount">
                            Discount Amount (Rs.)
                        </label>

                        <input
                                type="number"
                                id="discountAmount"
                                name="discountAmount"
                                min="0"
                                step="0.01"
                                value="<%= escapeHtml(
                                        submittedDiscountAmount
                                ) %>"
                                oninput="updateCalculation();">

                        <div class="help-text">

                            Optional monetary discount. The server
                            prevents discounts greater than the
                            payable amount.

                        </div>

                    </div>


                </div>


                <!-- =========================================
                     LIVE APPOINTMENT PREVIEW
                     ========================================= -->

                <div
                        id="appointmentPreview"
                        class="preview">


                    <div class="preview-header">

                        Selected Appointment Summary

                    </div>


                    <div class="preview-body">


                        <div class="preview-grid">


                            <div class="preview-item">

                                <span class="preview-label">
                                    Appointment Number
                                </span>

                                <span
                                        id="previewAppointmentNumber"
                                        class="preview-value">
                                    -
                                </span>

                            </div>


                            <div class="preview-item">

                                <span class="preview-label">
                                    Patient
                                </span>

                                <span
                                        id="previewPatient"
                                        class="preview-value">
                                    -
                                </span>

                            </div>


                            <div class="preview-item">

                                <span class="preview-label">
                                    Dentist
                                </span>

                                <span
                                        id="previewDentist"
                                        class="preview-value">
                                    -
                                </span>

                            </div>


                            <div class="preview-item">

                                <span class="preview-label">
                                    Treatment
                                </span>

                                <span
                                        id="previewTreatment"
                                        class="preview-value">
                                    -
                                </span>

                            </div>


                            <div class="preview-item">

                                <span class="preview-label">
                                    Appointment Date
                                </span>

                                <span
                                        id="previewDate"
                                        class="preview-value">
                                    -
                                </span>

                            </div>


                            <div class="preview-item">

                                <span class="preview-label">
                                    Appointment Time
                                </span>

                                <span
                                        id="previewTime"
                                        class="preview-value">
                                    -
                                </span>

                            </div>


                        </div>


                        <div class="estimated-cost-box">


                            <div class="calculation-row">

                                <span>Treatment Charge</span>

                                <span id="treatmentChargeText">
                                    Rs. 0.00
                                </span>

                            </div>


                            <div class="calculation-row">

                                <span>Consultation Fee</span>

                                <span id="consultationFeeText">
                                    Rs. 0.00
                                </span>

                            </div>


                            <div class="calculation-row">

                                <span>Subtotal</span>

                                <span id="subtotalText">
                                    Rs. 0.00
                                </span>

                            </div>


                            <div class="calculation-row">

                                <span>Additional Charge</span>

                                <span id="additionalChargeText">
                                    + Rs. 0.00
                                </span>

                            </div>


                            <div class="calculation-row">

                                <span>Discount</span>

                                <span id="discountText">
                                    - Rs. 0.00
                                </span>

                            </div>


                            <div class="calculation-row final-preview-total">

                                <span>Estimated Final Total</span>

                                <span id="finalTotalText">
                                    Rs. 0.00
                                </span>

                            </div>


                        </div>


                    </div>


                </div>


                <!-- =========================================
                     ACTIONS
                     ========================================= -->

                <div class="form-actions">


                    <a
                            class="btn btn-secondary"
                            href="<%= contextPath %>/bills">

                        Cancel

                    </a>


                    <button
                            type="submit"
                            class="btn <%= appointments.isEmpty()
                                    ? "btn-disabled"
                                    : "btn-primary" %>"
                            <%= appointments.isEmpty()
                                    ? "disabled"
                                    : "" %>>

                        Generate Bill & View Receipt

                    </button>


                </div>


            </form>


        </section>


    </main>


</div>


<script>

    function parseMoney(value) {

        const parsed =
                Number.parseFloat(value);

        return Number.isFinite(parsed)
                ? parsed
                : 0;
    }


    function formatMoney(value) {

        return "Rs. "
                + value.toLocaleString(
                        "en-LK",
                        {
                            minimumFractionDigits: 2,
                            maximumFractionDigits: 2
                        }
                );
    }


    function updateAppointmentPreview() {

        const select =
                document.getElementById(
                        "appointmentId"
                );


        const preview =
                document.getElementById(
                        "appointmentPreview"
                );


        if (!select
                || select.selectedIndex <= 0) {

            if (preview) {
                preview.style.display = "none";
            }

            return;
        }


        const option =
                select.options[
                        select.selectedIndex
                ];


        document.getElementById(
                "previewAppointmentNumber"
        ).textContent =
                option.dataset.appointmentNumber
                || "-";


        document.getElementById(
                "previewPatient"
        ).textContent =
                (
                    option.dataset.patientCode
                    ? option.dataset.patientCode
                      + " — "
                    : ""
                )
                + (
                    option.dataset.patientName
                    || "-"
                );


        document.getElementById(
                "previewDentist"
        ).textContent =
                option.dataset.dentistName
                || "-";


        document.getElementById(
                "previewTreatment"
        ).textContent =
                option.dataset.treatmentName
                || "-";


        document.getElementById(
                "previewDate"
        ).textContent =
                option.dataset.date
                || "-";


        document.getElementById(
                "previewTime"
        ).textContent =
                option.dataset.time
                || "-";


        preview.style.display =
                "block";


        updateCalculation();
    }


    function updateCalculation() {

        const select =
                document.getElementById(
                        "appointmentId"
                );


        if (!select
                || select.selectedIndex <= 0) {

            return;
        }


        const option =
                select.options[
                        select.selectedIndex
                ];


        const treatment =
                parseMoney(
                        option.dataset.treatmentPrice
                );


        const consultation =
                parseMoney(
                        option.dataset.consultationFee
                );


        const additional =
                parseMoney(
                        document.getElementById(
                                "additionalCharge"
                        ).value
                );


        const discount =
                parseMoney(
                        document.getElementById(
                                "discountAmount"
                        ).value
                );


        const subtotal =
                treatment
                + consultation;


        const finalTotal =
                subtotal
                + additional
                - discount;


        document.getElementById(
                "treatmentChargeText"
        ).textContent =
                formatMoney(
                        treatment
                );


        document.getElementById(
                "consultationFeeText"
        ).textContent =
                formatMoney(
                        consultation
                );


        document.getElementById(
                "subtotalText"
        ).textContent =
                formatMoney(
                        subtotal
                );


        document.getElementById(
                "additionalChargeText"
        ).textContent =
                "+ "
                + formatMoney(
                        additional
                );


        document.getElementById(
                "discountText"
        ).textContent =
                "- "
                + formatMoney(
                        discount
                );


        document.getElementById(
                "finalTotalText"
        ).textContent =
                formatMoney(
                        Math.max(
                                finalTotal,
                                0
                        )
                );
    }


    function validateFinalAmount() {

        const select =
                document.getElementById(
                        "appointmentId"
                );


        if (!select
                || select.selectedIndex <= 0) {

            alert(
                    "Please select a completed appointment."
            );

            return false;
        }


        const option =
                select.options[
                        select.selectedIndex
                ];


        const treatment =
                parseMoney(
                        option.dataset.treatmentPrice
                );


        const consultation =
                parseMoney(
                        option.dataset.consultationFee
                );


        const additional =
                parseMoney(
                        document.getElementById(
                                "additionalCharge"
                        ).value
                );


        const discount =
                parseMoney(
                        document.getElementById(
                                "discountAmount"
                        ).value
                );


        const payableBeforeDiscount =
                treatment
                + consultation
                + additional;


        if (discount > payableBeforeDiscount) {

            alert(
                    "Discount cannot exceed the payable amount."
            );

            return false;
        }


        return confirm(
                "Generate the final bill for this completed appointment?"
        );
    }


    document.addEventListener(
            "DOMContentLoaded",
            function () {

                updateAppointmentPreview();
            }
    );

</script>


</body>

</html>