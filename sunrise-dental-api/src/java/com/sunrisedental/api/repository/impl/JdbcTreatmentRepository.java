package com.sunrisedental.api.repository.impl;

import com.sunrisedental.api.config.DatabaseConnectionManager;
import com.sunrisedental.api.model.Treatment;
import com.sunrisedental.api.repository.TreatmentRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTreatmentRepository implements TreatmentRepository {

    private final DatabaseConnectionManager connectionManager;

    public JdbcTreatmentRepository() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    @Override
    public List<Treatment> findAll() throws SQLException {

        String sql = """
                SELECT *
                FROM treatments
                ORDER BY treatment_id DESC
                """;

        List<Treatment> treatments = new ArrayList<>();

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                treatments.add(mapRow(resultSet));
            }
        }

        return treatments;
    }

    @Override
    public Optional<Treatment> findById(int treatmentId)
            throws SQLException {

        String sql = """
                SELECT *
                FROM treatments
                WHERE treatment_id = ?
                """;

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, treatmentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Treatment> findByCode(String treatmentCode)
            throws SQLException {

        String sql = """
                SELECT *
                FROM treatments
                WHERE treatment_code = ?
                """;

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, treatmentCode);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Treatment> search(String keyword)
            throws SQLException {

        String sql = """
                SELECT *
                FROM treatments
                WHERE treatment_code LIKE ?
                   OR treatment_name LIKE ?
                   OR description LIKE ?
                ORDER BY treatment_name
                """;

        List<Treatment> treatments = new ArrayList<>();

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
                    treatments.add(mapRow(resultSet));
                }
            }
        }

        return treatments;
    }

    @Override
    public Treatment save(Treatment treatment)
            throws SQLException {

        String sql = """
                INSERT INTO treatments
                (
                    treatment_code,
                    treatment_name,
                    description,
                    treatment_price,
                    consultation_fee,
                    estimated_duration_minutes,
                    is_active
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

            statement.setString(1, treatment.getTreatmentCode());
            statement.setString(2, treatment.getTreatmentName());
            statement.setString(3, treatment.getDescription());
            statement.setBigDecimal(4, treatment.getTreatmentPrice());
            statement.setBigDecimal(5, treatment.getConsultationFee());

            if (treatment.getEstimatedDurationMinutes() != null) {
                statement.setInt(
                        6,
                        treatment.getEstimatedDurationMinutes()
                );
            } else {
                statement.setNull(
                        6,
                        java.sql.Types.INTEGER
                );
            }

            statement.setBoolean(7, treatment.isActive());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Creating treatment failed. No rows affected."
                );
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    treatment.setTreatmentId(
                            generatedKeys.getInt(1)
                    );
                }
            }
        }

        return treatment;
    }

    @Override
    public boolean update(Treatment treatment)
            throws SQLException {

        String sql = """
                UPDATE treatments
                SET treatment_name = ?,
                    description = ?,
                    treatment_price = ?,
                    consultation_fee = ?,
                    estimated_duration_minutes = ?,
                    is_active = ?
                WHERE treatment_id = ?
                """;

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, treatment.getTreatmentName());
            statement.setString(2, treatment.getDescription());
            statement.setBigDecimal(3, treatment.getTreatmentPrice());
            statement.setBigDecimal(4, treatment.getConsultationFee());

            if (treatment.getEstimatedDurationMinutes() != null) {
                statement.setInt(
                        5,
                        treatment.getEstimatedDurationMinutes()
                );
            } else {
                statement.setNull(
                        5,
                        java.sql.Types.INTEGER
                );
            }

            statement.setBoolean(6, treatment.isActive());
            statement.setInt(7, treatment.getTreatmentId());

            return statement.executeUpdate() > 0;
        }
    }

    private Treatment mapRow(ResultSet resultSet)
            throws SQLException {

        Treatment treatment = new Treatment();

        treatment.setTreatmentId(
                resultSet.getInt("treatment_id")
        );

        treatment.setTreatmentCode(
                resultSet.getString("treatment_code")
        );

        treatment.setTreatmentName(
                resultSet.getString("treatment_name")
        );

        treatment.setDescription(
                resultSet.getString("description")
        );

        treatment.setTreatmentPrice(
                resultSet.getBigDecimal("treatment_price")
        );

        treatment.setConsultationFee(
                resultSet.getBigDecimal("consultation_fee")
        );

        int duration =
                resultSet.getInt(
                        "estimated_duration_minutes"
                );

        if (resultSet.wasNull()) {
            treatment.setEstimatedDurationMinutes(null);
        } else {
            treatment.setEstimatedDurationMinutes(duration);
        }

        treatment.setActive(
                resultSet.getBoolean("is_active")
        );

        if (resultSet.getTimestamp("created_at") != null) {
            treatment.setCreatedAt(
                    resultSet
                            .getTimestamp("created_at")
                            .toLocalDateTime()
            );
        }

        return treatment;
    }
}