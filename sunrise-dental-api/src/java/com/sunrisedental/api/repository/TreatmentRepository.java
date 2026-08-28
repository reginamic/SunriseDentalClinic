package com.sunrisedental.api.repository;

import com.sunrisedental.api.model.Treatment;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TreatmentRepository {

    List<Treatment> findAll() throws SQLException;

    Optional<Treatment> findById(int treatmentId) throws SQLException;

    Optional<Treatment> findByCode(String treatmentCode) throws SQLException;

    List<Treatment> search(String keyword) throws SQLException;

    Treatment save(Treatment treatment) throws SQLException;

    boolean update(Treatment treatment) throws SQLException;
}