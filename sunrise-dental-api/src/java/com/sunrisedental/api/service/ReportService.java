package com.sunrisedental.api.service;

import com.sunrisedental.api.model.ClinicReport;
import com.sunrisedental.api.repository.ReportRepository;
import com.sunrisedental.api.repository.impl.JdbcReportRepository;

import java.sql.SQLException;
import java.time.LocalDate;

public class ReportService {

    private final ReportRepository reportRepository;

    /*
     * Production constructor.
     *
     * The service creates the JDBC repository used by the
     * deployed REST API.
     */
    public ReportService() {
        this(new JdbcReportRepository());
    }

    /*
     * Dependency-injection constructor.
     *
     * This allows automated tests to provide a test double
     * instead of connecting to the real database.
     */
    public ReportService(
            ReportRepository reportRepository) {

        if (reportRepository == null) {
            throw new IllegalArgumentException(
                    "Report repository is required."
            );
        }

        this.reportRepository = reportRepository;
    }

    /*
     * Generate the complete clinic management report for
     * the requested inclusive date range.
     */
    public ClinicReport generateClinicReport(
            LocalDate fromDate,
            LocalDate toDate)
            throws SQLException {

        validateDateRange(
                fromDate,
                toDate
        );

        return reportRepository.generateClinicReport(
                fromDate,
                toDate
        );
    }

    /*
     * Business validation is intentionally kept in the
     * service layer rather than the controller or repository.
     */
    private void validateDateRange(
            LocalDate fromDate,
            LocalDate toDate) {

        if (fromDate == null) {
            throw new IllegalArgumentException(
                    "From date is required."
            );
        }

        if (toDate == null) {
            throw new IllegalArgumentException(
                    "To date is required."
            );
        }

        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "From date cannot be after to date."
            );
        }
    }
}