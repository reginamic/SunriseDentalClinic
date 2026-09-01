package com.sunrisedental.api.repository.impl;

import com.sunrisedental.api.config.DatabaseConnectionManager;
import com.sunrisedental.api.model.Dentist;
import com.sunrisedental.api.repository.DentistRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcDentistRepository implements DentistRepository {

    private final DatabaseConnectionManager connectionManager;

    public JdbcDentistRepository() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    @Override
    public List<Dentist> findAll() throws SQLException {

        String sql = """
                SELECT *
                FROM dentists
                ORDER BY dentist_id DESC
                """;

        List<Dentist> dentists = new ArrayList<>();

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                dentists.add(mapRow(resultSet));
            }
        }

        return dentists;
    }

    @Override
    public Optional<Dentist> findById(int dentistId)
            throws SQLException {

        String sql = """
                SELECT *
                FROM dentists
                WHERE dentist_id = ?
                """;

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, dentistId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Dentist> findByCode(String dentistCode)
            throws SQLException {

        String sql = """
                SELECT *
                FROM dentists
                WHERE dentist_code = ?
                """;

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, dentistCode);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Dentist> search(String keyword)
            throws SQLException {

        String sql = """
                SELECT *
                FROM dentists
                WHERE dentist_code LIKE ?
                   OR full_name LIKE ?
                   OR specialization LIKE ?
                   OR contact_number LIKE ?
                ORDER BY full_name
                """;

        List<Dentist> dentists = new ArrayList<>();

        String searchValue = "%" + keyword + "%";

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, searchValue);
            statement.setString(2, searchValue);
            statement.setString(3, searchValue);
            statement.setString(4, searchValue);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    dentists.add(mapRow(resultSet));
                }
            }
        }

        return dentists;
    }

    @Override
    public Dentist save(Dentist dentist)
            throws SQLException {

        String sql = """
                INSERT INTO dentists
                (
                    dentist_code,
                    full_name,
                    specialization,
                    contact_number,
                    email,
                    is_active
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    )
        ) {

            statement.setString(1, dentist.getDentistCode());
            statement.setString(2, dentist.getFullName());
            statement.setString(3, dentist.getSpecialization());
            statement.setString(4, dentist.getContactNumber());
            statement.setString(5, dentist.getEmail());
            statement.setBoolean(6, dentist.isActive());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Creating dentist failed. No rows affected."
                );
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    dentist.setDentistId(
                            generatedKeys.getInt(1)
                    );
                }
            }
        }

        return dentist;
    }

    @Override
    public boolean update(Dentist dentist)
            throws SQLException {

        String sql = """
                UPDATE dentists
                SET full_name = ?,
                    specialization = ?,
                    contact_number = ?,
                    email = ?,
                    is_active = ?
                WHERE dentist_id = ?
                """;

        try (
            Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, dentist.getFullName());
            statement.setString(2, dentist.getSpecialization());
            statement.setString(3, dentist.getContactNumber());
            statement.setString(4, dentist.getEmail());
            statement.setBoolean(5, dentist.isActive());
            statement.setInt(6, dentist.getDentistId());

            return statement.executeUpdate() > 0;
        }
    }

    private Dentist mapRow(ResultSet resultSet)
            throws SQLException {

        Dentist dentist = new Dentist();

        dentist.setDentistId(
                resultSet.getInt("dentist_id")
        );

        dentist.setDentistCode(
                resultSet.getString("dentist_code")
        );

        dentist.setFullName(
                resultSet.getString("full_name")
        );

        dentist.setSpecialization(
                resultSet.getString("specialization")
        );

        dentist.setContactNumber(
                resultSet.getString("contact_number")
        );

        dentist.setEmail(
                resultSet.getString("email")
        );

        dentist.setActive(
                resultSet.getBoolean("is_active")
        );

        if (resultSet.getTimestamp("created_at") != null) {
            dentist.setCreatedAt(
                    resultSet
                            .getTimestamp("created_at")
                            .toLocalDateTime()
            );
        }

        return dentist;
    }
}