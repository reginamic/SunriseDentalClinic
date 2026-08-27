package com.sunrisedental.api.repository;

import com.sunrisedental.api.model.Patient;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PatientRepository {

    List<Patient> findAll() throws SQLException;

    Optional<Patient> findById(int patientId) throws SQLException;

    Optional<Patient> findByCode(String patientCode) throws SQLException;

    List<Patient> search(String keyword) throws SQLException;

    Patient save(Patient patient) throws SQLException;

    boolean update(Patient patient) throws SQLException;
}