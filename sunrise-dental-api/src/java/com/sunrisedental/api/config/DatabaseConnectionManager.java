package com.sunrisedental.api.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnectionManager {

    private static DatabaseConnectionManager instance;

    /*
     * ============================================================
     * DATABASE CONFIGURATION
     * ============================================================
     *
     * Production/deployment values can be supplied using:
     *
     * Environment variables:
     *   SUNRISE_DB_URL
     *   SUNRISE_DB_USERNAME
     *   SUNRISE_DB_PASSWORD
     *
     * or Java system properties:
     *   sunrise.db.url
     *   sunrise.db.username
     *   sunrise.db.password
     *
     * Local development defaults preserve compatibility with the
     * existing WampServer/MySQL environment.
     * ============================================================
     */

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/sunrise_dental_db"
            + "?useSSL=false&allowPublicKeyRetrieval=true"
            + "&serverTimezone=Asia/Colombo";

    private static final String DEFAULT_USERNAME =
            "root";

    private static final String DEFAULT_PASSWORD =
            "";


    private final String databaseUrl;
    private final String databaseUsername;
    private final String databasePassword;


    private DatabaseConnectionManager() {

        loadJdbcDriver();

        databaseUrl =
                resolveConfiguration(
                        "sunrise.db.url",
                        "SUNRISE_DB_URL",
                        DEFAULT_URL
                );

        databaseUsername =
                resolveConfiguration(
                        "sunrise.db.username",
                        "SUNRISE_DB_USERNAME",
                        DEFAULT_USERNAME
                );

        databasePassword =
                resolveConfiguration(
                        "sunrise.db.password",
                        "SUNRISE_DB_PASSWORD",
                        DEFAULT_PASSWORD
                );
    }


    /*
     * ============================================================
     * SINGLETON ACCESS
     * ============================================================
     */

    public static synchronized
            DatabaseConnectionManager getInstance() {

        if (instance == null) {

            instance =
                    new DatabaseConnectionManager();
        }

        return instance;
    }


    /*
     * ============================================================
     * DATABASE CONNECTION
     * ============================================================
     */

    public Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                databaseUrl,
                databaseUsername,
                databasePassword
        );
    }


    /*
     * ============================================================
     * JDBC DRIVER
     * ============================================================
     */

    private void loadJdbcDriver() {

        try {

            Class.forName(
                    "com.mysql.cj.jdbc.Driver"
            );

        } catch (ClassNotFoundException exception) {

            throw new IllegalStateException(
                    "MySQL JDBC Driver was not found.",
                    exception
            );
        }
    }


    /*
     * ============================================================
     * CONFIGURATION RESOLUTION
     * ============================================================
     *
     * Priority:
     *
     * 1. Java system property
     * 2. Operating-system environment variable
     * 3. Local development default
     *
     * This allows deployment configuration to be changed without
     * recompiling the application.
     * ============================================================
     */

    private String resolveConfiguration(
            String systemPropertyName,
            String environmentVariableName,
            String defaultValue) {

        String systemProperty =
                System.getProperty(
                        systemPropertyName
                );

        if (systemProperty != null
                && !systemProperty.isBlank()) {

            return systemProperty.trim();
        }


        String environmentValue =
                System.getenv(
                        environmentVariableName
                );

        if (environmentValue != null
                && !environmentValue.isBlank()) {

            return environmentValue.trim();
        }


        return defaultValue;
    }
}