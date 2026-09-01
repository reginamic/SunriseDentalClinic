package com.sunrisedental.api.repository.impl;

import com.sunrisedental.api.config.DatabaseConnectionManager;
import com.sunrisedental.api.model.ClinicReport;
import com.sunrisedental.api.repository.ReportRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class JdbcReportRepository implements ReportRepository {

    private final DatabaseConnectionManager connectionManager;

    public JdbcReportRepository() {
        this.connectionManager =
                DatabaseConnectionManager.getInstance();
    }

    @Override
    public ClinicReport generateClinicReport(
            LocalDate fromDate,
            LocalDate toDate)
            throws SQLException {

        ClinicReport report =
                new ClinicReport();

        report.setFromDate(
                fromDate.toString()
        );

        report.setToDate(
                toDate.toString()
        );

        try (
            Connection connection =
                    connectionManager.getConnection()
        ) {

            loadAppointmentSummary(
                    connection,
                    fromDate,
                    toDate,
                    report
            );

            loadFinancialSummary(
                    connection,
                    fromDate,
                    toDate,
                    report
            );

            report.setDentistWorkload(
                    loadDentistWorkload(
                            connection,
                            fromDate,
                            toDate
                    )
            );

            report.setTreatmentDemand(
                    loadTreatmentDemand(
                            connection,
                            fromDate,
                            toDate
                    )
            );
        }

        return report;
    }

    /*
     * ------------------------------------------------------------
     * Appointment activity report
     * ------------------------------------------------------------
     */
    private void loadAppointmentSummary(
            Connection connection,
            LocalDate fromDate,
            LocalDate toDate,
            ClinicReport report)
            throws SQLException {

        String sql = """
                SELECT
                    COUNT(*) AS total_appointments,

                    SUM(
                        CASE
                            WHEN LOWER(status) = 'scheduled'
                            THEN 1
                            ELSE 0
                        END
                    ) AS scheduled_appointments,

                    SUM(
                        CASE
                            WHEN LOWER(status) = 'completed'
                            THEN 1
                            ELSE 0
                        END
                    ) AS completed_appointments,

                    SUM(
                        CASE
                            WHEN LOWER(status) = 'cancelled'
                            THEN 1
                            ELSE 0
                        END
                    ) AS cancelled_appointments

                FROM appointments

                WHERE appointment_date
                    BETWEEN ? AND ?
                """;

        try (
            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setDate(
                    1,
                    Date.valueOf(fromDate)
            );

            statement.setDate(
                    2,
                    Date.valueOf(toDate)
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    report.setTotalAppointments(
                            resultSet.getInt(
                                    "total_appointments"
                            )
                    );

                    report.setScheduledAppointments(
                            resultSet.getInt(
                                    "scheduled_appointments"
                            )
                    );

                    report.setCompletedAppointments(
                            resultSet.getInt(
                                    "completed_appointments"
                            )
                    );

                    report.setCancelledAppointments(
                            resultSet.getInt(
                                    "cancelled_appointments"
                            )
                    );
                }
            }
        }
    }

    /*
     * ------------------------------------------------------------
     * Revenue and payment report
     * ------------------------------------------------------------
     */
    private void loadFinancialSummary(
            Connection connection,
            LocalDate fromDate,
            LocalDate toDate,
            ClinicReport report)
            throws SQLException {

        String sql = """
                SELECT

                    COALESCE(
                        SUM(b.total_amount),
                        0
                    ) AS total_billed,

                    COALESCE(
                        SUM(
                            CASE
                                WHEN UPPER(b.payment_status) = 'PAID'
                                THEN b.total_amount
                                ELSE 0
                            END
                        ),
                        0
                    ) AS total_paid,

                    COALESCE(
                        SUM(
                            CASE
                                WHEN UPPER(b.payment_status) = 'UNPAID'
                                THEN b.total_amount
                                ELSE 0
                            END
                        ),
                        0
                    ) AS outstanding_amount

                FROM bills b

                INNER JOIN appointments a
                    ON a.appointment_id =
                       b.appointment_id

                WHERE a.appointment_date
                    BETWEEN ? AND ?
                """;

        try (
            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setDate(
                    1,
                    Date.valueOf(fromDate)
            );

            statement.setDate(
                    2,
                    Date.valueOf(toDate)
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    report.setTotalBilled(
                            safeBigDecimal(
                                    resultSet.getBigDecimal(
                                            "total_billed"
                                    )
                            )
                    );

                    report.setTotalPaid(
                            safeBigDecimal(
                                    resultSet.getBigDecimal(
                                            "total_paid"
                                    )
                            )
                    );

                    report.setOutstandingAmount(
                            safeBigDecimal(
                                    resultSet.getBigDecimal(
                                            "outstanding_amount"
                                    )
                            )
                    );
                }
            }
        }
    }

    /*
     * ------------------------------------------------------------
     * Dentist workload report
     * ------------------------------------------------------------
     *
     * IMPORTANT:
     * The dentists table uses full_name.
     * It is aliased to dentist_name for the report model.
     */
    private List<ClinicReport.DentistWorkload>
            loadDentistWorkload(
                    Connection connection,
                    LocalDate fromDate,
                    LocalDate toDate)
                    throws SQLException {

        String sql = """
                SELECT
                    d.dentist_id,
                    d.full_name AS dentist_name,
                    d.specialization,

                    COUNT(a.appointment_id)
                        AS total_appointments,

                    SUM(
                        CASE
                            WHEN LOWER(a.status) = 'completed'
                            THEN 1
                            ELSE 0
                        END
                    ) AS completed_appointments,

                    SUM(
                        CASE
                            WHEN LOWER(a.status) = 'cancelled'
                            THEN 1
                            ELSE 0
                        END
                    ) AS cancelled_appointments

                FROM dentists d

                INNER JOIN appointments a
                    ON a.dentist_id =
                       d.dentist_id

                WHERE a.appointment_date
                    BETWEEN ? AND ?

                GROUP BY
                    d.dentist_id,
                    d.full_name,
                    d.specialization

                ORDER BY
                    total_appointments DESC,
                    d.full_name ASC
                """;

        List<ClinicReport.DentistWorkload>
                workload =
                new ArrayList<>();

        try (
            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setDate(
                    1,
                    Date.valueOf(fromDate)
            );

            statement.setDate(
                    2,
                    Date.valueOf(toDate)
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    ClinicReport.DentistWorkload item =
                            new ClinicReport.DentistWorkload();

                    item.setDentistId(
                            resultSet.getInt(
                                    "dentist_id"
                            )
                    );

                    item.setDentistName(
                            resultSet.getString(
                                    "dentist_name"
                            )
                    );

                    item.setSpecialization(
                            resultSet.getString(
                                    "specialization"
                            )
                    );

                    item.setTotalAppointments(
                            resultSet.getInt(
                                    "total_appointments"
                            )
                    );

                    item.setCompletedAppointments(
                            resultSet.getInt(
                                    "completed_appointments"
                            )
                    );

                    item.setCancelledAppointments(
                            resultSet.getInt(
                                    "cancelled_appointments"
                            )
                    );

                    workload.add(item);
                }
            }
        }

        return workload;
    }

    /*
     * ------------------------------------------------------------
     * Treatment demand report
     * ------------------------------------------------------------
     */
    private List<ClinicReport.TreatmentDemand>
            loadTreatmentDemand(
                    Connection connection,
                    LocalDate fromDate,
                    LocalDate toDate)
                    throws SQLException {

        String sql = """
                SELECT
                    t.treatment_id,
                    t.treatment_code,
                    t.treatment_name,

                    COUNT(a.appointment_id)
                        AS appointment_count,

                    COALESCE(
                        SUM(b.total_amount),
                        0
                    ) AS billed_amount

                FROM treatments t

                INNER JOIN appointments a
                    ON a.treatment_id =
                       t.treatment_id

                LEFT JOIN bills b
                    ON b.appointment_id =
                       a.appointment_id

                WHERE a.appointment_date
                    BETWEEN ? AND ?

                  AND LOWER(a.status)
                    <> 'cancelled'

                GROUP BY
                    t.treatment_id,
                    t.treatment_code,
                    t.treatment_name

                ORDER BY
                    appointment_count DESC,
                    billed_amount DESC,
                    t.treatment_name ASC
                """;

        List<ClinicReport.TreatmentDemand>
                treatmentDemand =
                new ArrayList<>();

        try (
            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setDate(
                    1,
                    Date.valueOf(fromDate)
            );

            statement.setDate(
                    2,
                    Date.valueOf(toDate)
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    ClinicReport.TreatmentDemand item =
                            new ClinicReport.TreatmentDemand();

                    item.setTreatmentId(
                            resultSet.getInt(
                                    "treatment_id"
                            )
                    );

                    item.setTreatmentCode(
                            resultSet.getString(
                                    "treatment_code"
                            )
                    );

                    item.setTreatmentName(
                            resultSet.getString(
                                    "treatment_name"
                            )
                    );

                    item.setAppointmentCount(
                            resultSet.getInt(
                                    "appointment_count"
                            )
                    );

                    item.setBilledAmount(
                            safeBigDecimal(
                                    resultSet.getBigDecimal(
                                            "billed_amount"
                                    )
                            )
                    );

                    treatmentDemand.add(item);
                }
            }
        }

        return treatmentDemand;
    }

    private BigDecimal safeBigDecimal(
            BigDecimal value) {

        return value != null
                ? value
                : BigDecimal.ZERO;
    }
}