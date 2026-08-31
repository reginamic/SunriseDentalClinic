package com.sunrisedental.api.repository;

import com.sunrisedental.api.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    /*
     * ============================================================
     * READ OPERATIONS
     * ============================================================
     */

    Optional<User> findByUsername(
            String username)
            throws SQLException;

    Optional<User> findById(
            int userId)
            throws SQLException;

    /**
     * Returns all application staff accounts.
     *
     * Used by ADMIN user-management functionality.
     */
    List<User> findAll()
            throws SQLException;

    /**
     * Checks whether a username already exists.
     *
     * Used before creating a new staff account.
     */
    boolean existsByUsername(
            String username)
            throws SQLException;

    /*
     * ============================================================
     * CREATE OPERATION
     * ============================================================
     */

    /**
     * Creates a new application user.
     *
     * The supplied User must contain a secure password hash,
     * not the plain-text password.
     *
     * @return generated database user ID
     */
    int create(
            User user)
            throws SQLException;

    /*
     * ============================================================
     * UPDATE OPERATIONS
     * ============================================================
     */

    /**
     * Updates the editable staff identity and authorization role.
     *
     * Username is intentionally not changed here because it is
     * the stable login identifier for the account.
     */
    boolean updateDetails(
            int userId,
            String fullName,
            String role)
            throws SQLException;

    /**
     * Activates or deactivates a staff account.
     *
     * Accounts are preserved rather than physically deleted so
     * historical appointments, bills and audit records can still
     * reference the original user.
     */
    boolean updateActiveStatus(
            int userId,
            boolean active)
            throws SQLException;

    /**
     * Replaces the stored password hash.
     *
     * The repository never receives or stores a plain-text
     * password.
     */
    boolean updatePasswordHash(
            int userId,
            String passwordHash)
            throws SQLException;
}