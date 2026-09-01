<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    HttpSession currentSession =
            request.getSession(false);

    boolean authenticated =
            currentSession != null
            && currentSession.getAttribute("userId") != null
            && currentSession.getAttribute("username") != null
            && currentSession.getAttribute("role") != null;

    if (authenticated) {

        response.sendRedirect(
                request.getContextPath()
                + "/dashboard"
        );

    } else {

        response.sendRedirect(
                request.getContextPath()
                + "/login"
        );
    }
%>