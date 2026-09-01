package com.sunrisedental.api.repository;

import com.sunrisedental.api.model.Dentist;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DentistRepository {

    List<Dentist> findAll() throws SQLException;

    Optional<Dentist> findById(int dentistId) throws SQLException;

    Optional<Dentist> findByCode(String dentistCode) throws SQLException;

    List<Dentist> search(String keyword) throws SQLException;

    Dentist save(Dentist dentist) throws SQLException;

    boolean update(Dentist dentist) throws SQLException;
}