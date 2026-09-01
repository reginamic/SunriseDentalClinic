package com.sunrisedental.api.repository;

import com.sunrisedental.api.model.ClinicReport;
import java.sql.SQLException;
import java.time.LocalDate;

public interface ReportRepository {

    ClinicReport generateClinicReport(
            LocalDate fromDate,
            LocalDate toDate
    ) throws SQLException;
}