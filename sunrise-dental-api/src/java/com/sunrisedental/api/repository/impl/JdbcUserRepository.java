package com.sunrisedental.api.repository.impl;

import com.sunrisedental.api.config.DatabaseConnectionManager;
import com.sunrisedental.api.model.User;
import com.sunrisedental.api.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Optional;

public class JdbcUserRepository implements UserRepository {

    private final DatabaseConnectionManager connectionManager;

    public JdbcUserRepository() {
        this.connectionManager =
                DatabaseConnectionManager.getInstance();
    }

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