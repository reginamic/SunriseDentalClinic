package com.sunrisedental.api.service;

import com.sunrisedental.api.model.ClinicReport;
import com.sunrisedental.api.repository.ReportRepository;

import java.sql.SQLException;
import java.time.LocalDate;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class ReportServiceTest {

    /*
     * ============================================================
     * TEST 1
     * Valid date range should be forwarded to repository
     * and the generated report should be returned.
     * ============================================================
     */
    @Test
    public void shouldGenerateReportForValidDateRange()
            throws SQLException {

        LocalDate fromDate =
                LocalDate.of(
                        2026,
                        1,
                        1
                );

        LocalDate toDate =
                LocalDate.of(
                        2026,
                        12,
                        31
                );

        ClinicReport expectedReport =
                new ClinicReport();

        expectedReport.setFromDate(
                fromDate.toString()
        );

        expectedReport.setToDate(
                toDate.toString()
        );

        expectedReport.setTotalAppointments(
                4
        );

        RecordingReportRepository repository =
                new RecordingReportRepository(
                        expectedReport
                );

        ReportService service =
                new ReportService(
                        repository
                );

        ClinicReport actualReport =
                service.generateClinicReport(
                        fromDate,
                        toDate
                );

        assertSame(
                expectedReport,
                actualReport
        );

        assertEquals(
                fromDate,
                repository.getReceivedFromDate()
        );

        assertEquals(
                toDate,
                repository.getReceivedToDate()
        );
    }

    /*
     * ============================================================
     * TEST 2
     * Missing from-date must be rejected by service validation.
     * ============================================================
     */
    @Test
    public void shouldRejectMissingFromDate()
            throws SQLException {

        ReportService service =
                new ReportService(
                        new RecordingReportRepository(
                                new ClinicReport()
                        )
                );

        try {

            service.generateClinicReport(
                    null,
                    LocalDate.of(
                            2026,
                            12,
                            31
                    )
            );

            fail(
                    "Expected IllegalArgumentException."
            );

        } catch (IllegalArgumentException exception) {

            assertEquals(
                    "From date is required.",
                    exception.getMessage()
            );
        }
    }

    /*
     * ============================================================
     * TEST 3
     * Missing to-date must be rejected.
     * ============================================================
     */
    @Test
    public void shouldRejectMissingToDate()
            throws SQLException {

        ReportService service =
                new ReportService(
                        new RecordingReportRepository(
                                new ClinicReport()
                        )
                );

        try {

            service.generateClinicReport(
                    LocalDate.of(
                            2026,
                            1,
                            1
                    ),
                    null
            );

            fail(
                    "Expected IllegalArgumentException."
            );

        } catch (IllegalArgumentException exception) {

            assertEquals(
                    "To date is required.",
                    exception.getMessage()
            );
        }
    }

    /*
     * ============================================================
     * TEST 4
     * From-date cannot occur after to-date.
     * ============================================================
     */
    @Test
    public void shouldRejectReversedDateRange()
            throws SQLException {

        ReportService service =
                new ReportService(
                        new RecordingReportRepository(
                                new ClinicReport()
                        )
                );

        try {

            service.generateClinicReport(
                    LocalDate.of(
                            2030,
                            12,
                            31
                    ),
                    LocalDate.of(
                            2026,
                            1,
                            1
                    )
            );

            fail(
                    "Expected IllegalArgumentException."
            );

        } catch (IllegalArgumentException exception) {

            assertEquals(
                    "From date cannot be after to date.",
                    exception.getMessage()
            );
        }
    }

    /*
     * ============================================================
     * Repository test double
     * ============================================================
     *
     * No MySQL connection is required.
     * This isolates ReportService business validation.
     */
    private static class RecordingReportRepository
            implements ReportRepository {

        private final ClinicReport report;

        private LocalDate receivedFromDate;
        private LocalDate receivedToDate;

        RecordingReportRepository(
                ClinicReport report) {

            this.report =
                    report;
        }

        @Override
        public ClinicReport generateClinicReport(
                LocalDate fromDate,
                LocalDate toDate)
                throws SQLException {

            this.receivedFromDate =
                    fromDate;

            this.receivedToDate =
                    toDate;

            return report;
        }

        LocalDate getReceivedFromDate() {
            return receivedFromDate;
        }

        LocalDate getReceivedToDate() {
            return receivedToDate;
        }
    }
}