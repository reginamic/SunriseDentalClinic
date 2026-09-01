package com.sunrisedental.api.controller;

import com.sunrisedental.api.config.DatabaseConnectionManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DatabaseHealthServlet", urlPatterns = {"/api/db-health"})
public class DatabaseHealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (
            Connection connection =
                DatabaseConnectionManager
                    .getInstance()
                    .getConnection();

            PrintWriter out = response.getWriter()
        ) {

            if (connection != null && !connection.isClosed()) {

                response.setStatus(HttpServletResponse.SC_OK);

                String databaseName =
                        connection.getCatalog();

                String databaseProduct =
                        connection.getMetaData()
                                  .getDatabaseProductName();

                out.print("{");
                out.print("\"status\":\"UP\",");
                out.print("\"database\":\"" + databaseName + "\",");
                out.print("\"databaseProduct\":\"" + databaseProduct + "\",");
                out.print("\"connection\":\"SUCCESS\"");
                out.print("}");

            } else {

                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                out.print(
                    "{\"status\":\"DOWN\","
                    + "\"connection\":\"FAILED\"}"
                );
            }

        } catch (SQLException e) {

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            try (PrintWriter out = response.getWriter()) {

                out.print(
                    "{\"status\":\"DOWN\","
                    + "\"connection\":\"FAILED\","
                    + "\"message\":\"Database connection error\"}"
                );
            }

            System.err.println(
                    "Database connection failed: "
                    + e.getMessage()
            );
        }
    }
}