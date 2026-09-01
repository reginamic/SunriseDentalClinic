<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="en">
<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Login | Sunrise Dental Clinic</title>

    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            min-height: 100vh;
            font-family:
                "Segoe UI",
                Arial,
                sans-serif;

            background:
                linear-gradient(
                    135deg,
                    #f4f7fb 0%,
                    #eaf1f8 100%
                );

            color: #1f2937;

            display: flex;
            align-items: center;
            justify-content: center;

            padding: 24px;
        }

        .login-wrapper {
            width: 100%;
            max-width: 1000px;
            min-height: 600px;

            background: #ffffff;

            border-radius: 18px;

            box-shadow:
                0 20px 55px
                rgba(15, 23, 42, 0.12);

            overflow: hidden;

            display: grid;
            grid-template-columns:
                1.05fr 0.95fr;
        }

        /* -----------------------------
           LEFT BRANDING SECTION
           ----------------------------- */

        .brand-panel {
            background:
                linear-gradient(
                    145deg,
                    #102a43,
                    #153e75
                );

            color: #ffffff;

            padding: 60px;

            display: flex;
            flex-direction: column;
            justify-content: space-between;

            position: relative;
            overflow: hidden;
        }

        .brand-panel::before {
            content: "";

            position: absolute;

            width: 320px;
            height: 320px;

            border-radius: 50%;

            background:
                rgba(255, 255, 255, 0.05);

            right: -120px;
            top: -100px;
        }

        .brand-panel::after {
            content: "";

            position: absolute;

            width: 220px;
            height: 220px;

            border-radius: 50%;

            background:
                rgba(255, 255, 255, 0.04);

            left: -90px;
            bottom: -90px;
        }

        .brand-content {
            position: relative;
            z-index: 1;
        }

        .brand-icon {
            width: 62px;
            height: 62px;

            background:
                rgba(255, 255, 255, 0.12);

            border:
                1px solid
                rgba(255, 255, 255, 0.18);

            border-radius: 16px;

            display: flex;
            align-items: center;
            justify-content: center;

            margin-bottom: 30px;

            font-size: 30px;
        }

        .brand-panel h1 {
            font-size: 34px;
            line-height: 1.2;

            margin-bottom: 14px;

            font-weight: 700;
        }

        .brand-panel .subtitle {
            font-size: 16px;
            line-height: 1.7;

            color: #d9e8f7;

            max-width: 390px;
        }

        .feature-list {
            list-style: none;

            margin-top: 42px;

            display: grid;
            gap: 16px;
        }

        .feature-list li {
            display: flex;
            align-items: center;
            gap: 12px;

            color: #e7f0fa;

            font-size: 14px;
        }

        .check-icon {
            width: 24px;
            height: 24px;

            border-radius: 50%;

            background:
                rgba(99, 179, 237, 0.18);

            display: inline-flex;
            align-items: center;
            justify-content: center;

            color: #90cdf4;

            font-weight: bold;
            flex-shrink: 0;
        }

        .brand-footer {
            position: relative;
            z-index: 1;

            color: #aac4df;

            font-size: 12px;
        }

        /* -----------------------------
           LOGIN FORM SECTION
           ----------------------------- */

        .login-panel {
            padding: 60px;

            display: flex;
            align-items: center;
        }

        .login-content {
            width: 100%;
        }

        .login-heading {
            margin-bottom: 34px;
        }

        .login-heading .eyebrow {
            color: #2563a6;

            font-size: 13px;
            font-weight: 700;

            text-transform: uppercase;
            letter-spacing: 1.2px;

            margin-bottom: 10px;
        }

        .login-heading h2 {
            color: #102a43;

            font-size: 30px;
            font-weight: 700;

            margin-bottom: 10px;
        }

        .login-heading p {
            color: #64748b;

            font-size: 14px;
            line-height: 1.6;
        }

        .error-message {
            background: #fff5f5;

            border:
                1px solid #fecaca;

            border-left:
                4px solid #dc2626;

            color: #991b1b;

            padding: 12px 14px;

            border-radius: 8px;

            margin-bottom: 22px;

            font-size: 13px;
            line-height: 1.5;
        }

        .form-group {
            margin-bottom: 22px;
        }

        .form-group label {
            display: block;

            color: #334155;

            font-size: 13px;
            font-weight: 600;

            margin-bottom: 8px;
        }

        .input-wrapper {
            position: relative;
        }

        .input-wrapper input {
            width: 100%;

            height: 48px;

            padding:
                0 45px 0 14px;

            border:
                1px solid #cbd5e1;

            border-radius: 9px;

            background: #ffffff;

            color: #1e293b;

            font-size: 14px;

            outline: none;

            transition:
                border-color 0.2s,
                box-shadow 0.2s;
        }

        .input-wrapper input:focus {
            border-color: #2563a6;

            box-shadow:
                0 0 0 3px
                rgba(37, 99, 166, 0.10);
        }

        .input-wrapper input::placeholder {
            color: #94a3b8;
        }

        .field-icon {
            position: absolute;

            right: 14px;
            top: 50%;

            transform:
                translateY(-50%);

            color: #94a3b8;

            font-size: 16px;

            user-select: none;
        }

        .password-toggle {
            position: absolute;

            right: 12px;
            top: 50%;

            transform:
                translateY(-50%);

            border: none;
            background: transparent;

            color: #64748b;

            cursor: pointer;

            font-size: 12px;
            font-weight: 600;

            padding: 4px;
        }

        .password-toggle:hover {
            color: #2563a6;
        }

        .login-button {
            width: 100%;
            height: 48px;

            border: none;
            border-radius: 9px;

            background: #153e75;

            color: #ffffff;

            font-size: 14px;
            font-weight: 700;

            cursor: pointer;

            transition:
                background 0.2s,
                transform 0.1s;

            margin-top: 5px;
        }

        .login-button:hover {
            background: #102f59;
        }

        .login-button:active {
            transform:
                translateY(1px);
        }

        .security-note {
            margin-top: 24px;

            padding-top: 20px;

            border-top:
                1px solid #e2e8f0;

            display: flex;
            align-items: flex-start;
            gap: 10px;

            color: #64748b;

            font-size: 12px;
            line-height: 1.5;
        }

        .security-icon {
            color: #2563a6;
            font-size: 15px;
        }

        .system-status {
            margin-top: 18px;

            display: flex;
            align-items: center;
            gap: 8px;

            color: #64748b;

            font-size: 11px;
        }

        .status-dot {
            width: 8px;
            height: 8px;

            border-radius: 50%;

            background: #16a34a;
        }

        @media (max-width: 850px) {

            .login-wrapper {
                max-width: 520px;

                grid-template-columns: 1fr;
            }

            .brand-panel {
                padding: 35px;

                min-height: auto;
            }

            .feature-list {
                display: none;
            }

            .brand-footer {
                margin-top: 30px;
            }

            .login-panel {
                padding: 40px 35px;
            }
        }

        @media (max-width: 480px) {

            body {
                padding: 12px;
            }

            .login-panel,
            .brand-panel {
                padding: 30px 24px;
            }

            .brand-panel h1 {
                font-size: 28px;
            }

            .login-heading h2 {
                font-size: 25px;
            }
        }
    </style>
</head>

<body>

<div class="login-wrapper">

    <!-- LEFT SIDE -->
    <section class="brand-panel">

        <div class="brand-content">

            <div class="brand-icon">
                &#10010;
            </div>

            <h1>
                Sunrise Dental
                Clinic
            </h1>

            <p class="subtitle">
                Secure clinic management for appointments,
                patients, treatments and billing.
            </p>

            <ul class="feature-list">

                <li>
                    <span class="check-icon">
                        &#10003;
                    </span>

                    Manage patient appointments efficiently
                </li>

                <li>
                    <span class="check-icon">
                        &#10003;
                    </span>

                    Prevent dentist double bookings
                </li>

                <li>
                    <span class="check-icon">
                        &#10003;
                    </span>

                    Maintain treatment and billing records
                </li>

                <li>
                    <span class="check-icon">
                        &#10003;
                    </span>

                    Secure role-based staff access
                </li>

            </ul>

        </div>

        <div class="brand-footer">
            Sunrise Dental Clinic Management System
        </div>

    </section>

    <!-- RIGHT SIDE -->
    <section class="login-panel">

        <div class="login-content">

            <div class="login-heading">

                <div class="eyebrow">
                    Staff Portal
                </div>

                <h2>
                    Welcome back
                </h2>

                <p>
                    Sign in with your authorized staff
                    account to continue.
                </p>

            </div>

            <% if (request.getAttribute("errorMessage") != null) { %>

                <div
                    class="error-message"
                    role="alert">

                    <%= request.getAttribute("errorMessage") %>

                </div>

            <% } %>

            <form
                method="post"
                action="<%= request.getContextPath() %>/login"
                autocomplete="on">

                <div class="form-group">

                    <label for="username">
                        Username
                    </label>

                    <div class="input-wrapper">

                        <input
                            type="text"
                            id="username"
                            name="username"
                            placeholder="Enter your username"
                            autocomplete="username"
                            maxlength="80"
                            required
                            autofocus>

                        <span class="field-icon">
                            &#128100;
                        </span>

                    </div>

                </div>

                <div class="form-group">

                    <label for="password">
                        Password
                    </label>

                    <div class="input-wrapper">

                        <input
                            type="password"
                            id="password"
                            name="password"
                            placeholder="Enter your password"
                            autocomplete="current-password"
                            maxlength="128"
                            required>

                        <button
                            type="button"
                            class="password-toggle"
                            id="passwordToggle"
                            aria-label="Show password">
                            Show
                        </button>

                    </div>

                </div>

                <button
                    type="submit"
                    class="login-button">

                    Sign in securely

                </button>

            </form>

            <div class="security-note">

                <span class="security-icon">
                    &#128274;
                </span>

                <span>
                    Access is restricted to authorized
                    Sunrise Dental Clinic staff.
                    Sessions automatically expire after
                    inactivity.
                </span>

            </div>

            <div class="system-status">

                <span class="status-dot"></span>

                <span>
                    Secure clinic management portal
                </span>

            </div>

        </div>

    </section>

</div>

<script>
    const passwordInput =
            document.getElementById("password");

    const passwordToggle =
            document.getElementById("passwordToggle");

    passwordToggle.addEventListener(
        "click",
        function () {

            const passwordVisible =
                    passwordInput.type === "text";

            passwordInput.type =
                    passwordVisible
                    ? "password"
                    : "text";

            passwordToggle.textContent =
                    passwordVisible
                    ? "Show"
                    : "Hide";

            passwordToggle.setAttribute(
                "aria-label",
                passwordVisible
                ? "Show password"
                : "Hide password"
            );
        }
    );
</script>

</body>
</html>