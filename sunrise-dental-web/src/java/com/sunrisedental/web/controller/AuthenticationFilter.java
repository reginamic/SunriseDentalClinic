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
            "/bills/*"
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
         * Prevent authenticated clinic pages
         * from being stored in browser cache.
         *
         * This helps prevent users from seeing
         * protected pages through the browser
         * Back button after logout.
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
         * Retrieve the existing session only.
         *
         * Do not create a new session for an
         * unauthenticated request.
         */
        HttpSession session =
                httpRequest.getSession(false);

        boolean authenticated =
                session != null
                && session.getAttribute("userId") != null
                && session.getAttribute("username") != null
                && session.getAttribute("role") != null;

        /*
         * Block unauthenticated access.
         */
        if (!authenticated) {

            httpResponse.sendRedirect(
                    httpRequest.getContextPath()
                    + "/login"
            );

            return;
        }

        /*
         * The user has a valid authenticated
         * session, so continue to the requested
         * controller.
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