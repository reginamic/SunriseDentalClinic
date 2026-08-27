package com.sunrisedental.api.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnectionManager {

    private static DatabaseConnectionManager instance;

    private static final String URL =
            "jdbc:mysql://localhost:3306/sunrise_dental_db"
            + "?useSSL=false&allowPublicKeyRetrieval=true"
            + "&serverTimezone=Asia/Colombo";

    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // Private constructor prevents direct object creation.
    private DatabaseConnectionManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "MySQL JDBC Driver was not found.", e);
        }
    }

    // Singleton access point.
    public static synchronized DatabaseConnectionManager getInstance() {

        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }

        return instance;
    }

    // Provides a new database connection when required.
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USERNAME,
                PASSWORD
        );
    }
}