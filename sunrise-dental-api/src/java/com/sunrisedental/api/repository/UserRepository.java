package com.sunrisedental.api.repository;

import com.sunrisedental.api.model.User;

import java.sql.SQLException;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username)
            throws SQLException;

    Optional<User> findById(int userId)
            throws SQLException;
}