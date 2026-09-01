package com.sunrisedental.api.repository;

import com.sunrisedental.api.model.Bill;
import com.sunrisedental.api.model.BillStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BillRepository {

    List<Bill> findAll() throws SQLException;

    Optional<Bill> findById(int billId)
            throws SQLException;

    Optional<Bill> findByNumber(String billNumber)
            throws SQLException;

    Optional<Bill> findByAppointmentId(int appointmentId)
            throws SQLException;

    Bill save(Bill bill)
            throws SQLException;

    boolean updatePaymentStatus(
            int billId,
            BillStatus paymentStatus)
            throws SQLException;
}