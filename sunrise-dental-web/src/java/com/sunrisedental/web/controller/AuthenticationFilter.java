package com.sunrisedental.web.controller;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(
        urlPatterns = {

            "/dashboard",

            "/patients",
            "/patients/*",

            "/dentists",
            "/dentists/*",

            "/treatments",
            "/treatments/*",

            "/appointments",
            "/appointments/*",

            "/bills",
            "/bills/*",

            "/reports",
            "/reports/*",

            "/users",
            "/users/*",

            "/help"
        }
)
public class AuthenticationFilter implements Filter {

    @Override
    public void init(
            FilterConfig filterConfig)
            throws ServletException {

        // No initialization required.
    }


    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;


        /*
         * ========================================================
         * PROTECTED PAGE CACHE CONTROL
         * ========================================================
         *
         * Authenticated clinic pages must not be stored in the
         * browser cache. This reduces the possibility of protected
         * information being visible through the browser Back
         * button after logout.
         * ========================================================
         */

        httpResponse.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        httpResponse.setHeader(
                "Pragma",
                "no-cache"
        );

        httpResponse.setDateHeader(
                "Expires",
                0
        );


        /*
         * ========================================================
         * SESSION AUTHENTICATION
         * ========================================================
         *
         * Retrieve only an existing session.
         * Never create a session for an unauthenticated request.
         * ========================================================
         */

        HttpSession session =
                httpRequest.getSession(false);


        boolean authenticated =
                session != null
                && session.getAttribute(
                        "userId"
                ) != null
                && session.getAttribute(
                        "username"
                ) != null
                && session.getAttribute(
                        "role"
                ) != null;


        /*
         * ========================================================
         * UNAUTHENTICATED ACCESS
         * ========================================================
         */

        if (!authenticated) {

            httpResponse.sendRedirect(
                    httpRequest.getContextPath()
                    + "/login"
            );

            return;
        }


        /*
         * ========================================================
         * AUTHENTICATED REQUEST
         * ========================================================
         *
         * Authentication is satisfied here.
         *
         * Individual controllers still enforce authorization
         * rules such as ADMIN-only access to Reports and
         * Staff User Management.
         * ========================================================
         */

        chain.doFilter(
                request,
                response
        );
    }


    @Override
    public void destroy() {

        // No cleanup required.
    }
}