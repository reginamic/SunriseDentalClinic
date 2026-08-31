package com.sunrisedental.api.repository.impl;

import com.sunrisedental.api.config.DatabaseConnectionManager;
import com.sunrisedental.api.model.User;
import com.sunrisedental.api.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {

    private final DatabaseConnectionManager connectionManager;

    public JdbcUserRepository() {
        this.connectionManager =
                DatabaseConnectionManager.getInstance();
    }

    /*
     * ============================================================
     * FIND USER BY USERNAME
     * ============================================================
     */
    @Override
    public Optional<User> findByUsername(
            String username)
            throws SQLException {

        String sql = """
                SELECT
                    user_id,
                    username,
                    password_hash,
                    full_name,
                    role,
                    is_active,
                    created_at
                FROM users
                WHERE username = ?
                LIMIT 1
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    username
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapRow(resultSet)
                    );
                }
            }
        }

        return Optional.empty();
    }

    /*
     * ============================================================
     * FIND USER BY ID
     * ============================================================
     */
    @Override
    public Optional<User> findById(
            int userId)
            throws SQLException {

        String sql = """
                SELECT
                    user_id,
                    username,
                    password_hash,
                    full_name,
                    role,
                    is_active,
                    created_at
                FROM users
                WHERE user_id = ?
                LIMIT 1
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    userId
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapRow(resultSet)
                    );
                }
            }
        }

        return Optional.empty();
    }

    /*
     * ============================================================
     * FIND ALL USERS
     * ============================================================
     */
    @Override
    public List<User> findAll()
            throws SQLException {

        String sql = """
                SELECT
                    user_id,
                    username,
                    password_hash,
                    full_name,
                    role,
                    is_active,
                    created_at
                FROM users
                ORDER BY
                    is_active DESC,
                    full_name ASC,
                    username ASC
                """;

        List<User> users =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet resultSet =
                    statement.executeQuery()
        ) {

            while (resultSet.next()) {
                users.add(
                        mapRow(resultSet)
                );
            }
        }

        return users;
    }

    /*
     * ============================================================
     * CHECK USERNAME EXISTS
     * ============================================================
     */
    @Override
    public boolean existsByUsername(
            String username)
            throws SQLException {

        String sql = """
                SELECT 1
                FROM users
                WHERE username = ?
                LIMIT 1
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    username
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                return resultSet.next();
            }
        }
    }

    /*
     * ============================================================
     * CREATE USER
     * ============================================================
     */
    @Override
    public int create(
            User user)
            throws SQLException {

        String sql = """
                INSERT INTO users
                    (
                        username,
                        password_hash,
                        full_name,
                        role,
                        is_active
                    )
                VALUES
                    (?, ?, ?, ?, ?)
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    )
        ) {

            statement.setString(
                    1,
                    user.getUsername()
            );

            statement.setString(
                    2,
                    user.getPasswordHash()
            );

            statement.setString(
                    3,
                    user.getFullName()
            );

            statement.setString(
                    4,
                    user.getRole()
            );

            statement.setBoolean(
                    5,
                    user.isActive()
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Unable to create user account."
                );
            }

            try (
                ResultSet generatedKeys =
                        statement.getGeneratedKeys()
            ) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new SQLException(
                    "User account was created but no generated ID was returned."
            );
        }
    }

    /*
     * ============================================================
     * UPDATE USER DETAILS
     * ============================================================
     */
    @Override
    public boolean updateDetails(
            int userId,
            String fullName,
            String role)
            throws SQLException {

        String sql = """
                UPDATE users
                SET
                    full_name = ?,
                    role = ?
                WHERE user_id = ?
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    fullName
            );

            statement.setString(
                    2,
                    role
            );

            statement.setInt(
                    3,
                    userId
            );

            return statement.executeUpdate() == 1;
        }
    }

    /*
     * ============================================================
     * ACTIVATE / DEACTIVATE USER
     * ============================================================
     */
    @Override
    public boolean updateActiveStatus(
            int userId,
            boolean active)
            throws SQLException {

        String sql = """
                UPDATE users
                SET is_active = ?
                WHERE user_id = ?
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setBoolean(
                    1,
                    active
            );

            statement.setInt(
                    2,
                    userId
            );

            return statement.executeUpdate() == 1;
        }
    }

    /*
     * ============================================================
     * UPDATE PASSWORD HASH
     * ============================================================
     */
    @Override
    public boolean updatePasswordHash(
            int userId,
            String passwordHash)
            throws SQLException {

        String sql = """
                UPDATE users
                SET password_hash = ?
                WHERE user_id = ?
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    passwordHash
            );

            statement.setInt(
                    2,
                    userId
            );

            return statement.executeUpdate() == 1;
        }
    }

    /*
     * ============================================================
     * ROW MAPPING
     * ============================================================
     */
    private User mapRow(
            ResultSet resultSet)
            throws SQLException {

        User user = new User();

        user.setUserId(
                resultSet.getInt(
                        "user_id"
                )
        );

        user.setUsername(
                resultSet.getString(
                        "username"
                )
        );

        user.setPasswordHash(
                resultSet.getString(
                        "password_hash"
                )
        );

        user.setFullName(
                resultSet.getString(
                        "full_name"
                )
        );

        user.setRole(
                resultSet.getString(
                        "role"
                )
        );

        user.setActive(
                resultSet.getBoolean(
                        "is_active"
                )
        );

        if (resultSet.getTimestamp(
                "created_at") != null) {

            user.setCreatedAt(
                    resultSet
                            .getTimestamp(
                                    "created_at"
                            )
                            .toLocalDateTime()
            );
        }

        return user;
    }
}