package com.sunrisedental.api.repository.impl;

import com.sunrisedental.api.config.DatabaseConnectionManager;
import com.sunrisedental.api.model.Patient;
import com.sunrisedental.api.repository.PatientRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPatientRepository implements PatientRepository {

    private final DatabaseConnectionManager connectionManager;

    public JdbcPatientRepository() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    @Override
    public List<Patient> findAll() throws SQLException {

        String sql = """
                SELECT *
                FROM patients
                ORDER BY patient_id DESC
                """;

        List<Patient> patients = new ArrayList<>();

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                patients.add(mapRow(resultSet));
            }
        }

        return patients;
    }

    @Override
    public Optional<Patient> findById(int patientId) throws SQLException {

        String sql = """
                SELECT *
                FROM patients
                WHERE patient_id = ?
                """;

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, patientId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Patient> findByCode(String patientCode) throws SQLException {

        String sql = """
                SELECT *
                FROM patients
                WHERE patient_code = ?
                """;

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, patientCode);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Patient> search(String keyword) throws SQLException {

        String sql = """
                SELECT *
                FROM patients
                WHERE patient_code LIKE ?
                   OR full_name LIKE ?
                   OR contact_number LIKE ?
                ORDER BY full_name
                """;

        List<Patient> patients = new ArrayList<>();

        String searchValue = "%" + keyword + "%";

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, searchValue);
            statement.setString(2, searchValue);
            statement.setString(3, searchValue);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    patients.add(mapRow(resultSet));
                }
            }
        }

        return patients;
    }

    @Override
    public Patient save(Patient patient) throws SQLException {

        String sql = """
                INSERT INTO patients
                (
                    patient_code,
                    full_name,
                    address,
                    contact_number,
                    email,
                    date_of_birth,
                    gender
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    )
        ) {

            statement.setString(1, patient.getPatientCode());
            statement.setString(2, patient.getFullName());
            statement.setString(3, patient.getAddress());
            statement.setString(4, patient.getContactNumber());
            statement.setString(5, patient.getEmail());

            if (patient.getDateOfBirth() != null) {
                statement.setDate(
                        6,
                        Date.valueOf(patient.getDateOfBirth())
                );
            } else {
                statement.setNull(6, java.sql.Types.DATE);
            }

            statement.setString(7, patient.getGender());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Creating patient failed. No rows affected."
                );
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    patient.setPatientId(generatedKeys.getInt(1));
                }
            }
        }

        return patient;
    }

    @Override
    public boolean update(Patient patient) throws SQLException {

        String sql = """
                UPDATE patients
                SET full_name = ?,
                    address = ?,
                    contact_number = ?,
                    email = ?,
                    date_of_birth = ?,
                    gender = ?
                WHERE patient_id = ?
                """;

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, patient.getFullName());
            statement.setString(2, patient.getAddress());
            statement.setString(3, patient.getContactNumber());
            statement.setString(4, patient.getEmail());

            if (patient.getDateOfBirth() != null) {
                statement.setDate(
                        5,
                        Date.valueOf(patient.getDateOfBirth())
                );
            } else {
                statement.setNull(5, java.sql.Types.DATE);
            }

            statement.setString(6, patient.getGender());
            statement.setInt(7, patient.getPatientId());

            return statement.executeUpdate() > 0;
        }
    }

    private Patient mapRow(ResultSet resultSet) throws SQLException {

        Patient patient = new Patient();

        patient.setPatientId(
                resultSet.getInt("patient_id")
        );

        patient.setPatientCode(
                resultSet.getString("patient_code")
        );

        patient.setFullName(
                resultSet.getString("full_name")
        );

        patient.setAddress(
                resultSet.getString("address")
        );

        patient.setContactNumber(
                resultSet.getString("contact_number")
        );

        patient.setEmail(
                resultSet.getString("email")
        );

        Date dateOfBirth =
                resultSet.getDate("date_of_birth");

        if (dateOfBirth != null) {
            patient.setDateOfBirth(
                    dateOfBirth.toLocalDate()
            );
        }

        patient.setGender(
                resultSet.getString("gender")
        );

        if (resultSet.getTimestamp("created_at") != null) {
            patient.setCreatedAt(
                    resultSet
                            .getTimestamp("created_at")
                            .toLocalDateTime()
            );
        }

        if (resultSet.getTimestamp("updated_at") != null) {
            patient.setUpdatedAt(
                    resultSet
                            .getTimestamp("updated_at")
                            .toLocalDateTime()
            );
        }

        return patient;
    }
}