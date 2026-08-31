package com.sunrisedental.api.service;

import com.sunrisedental.api.model.User;
import com.sunrisedental.api.repository.UserRepository;
import com.sunrisedental.api.repository.impl.JdbcUserRepository;
import com.sunrisedental.api.security.PasswordHasher;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class UserManagementService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_RECEPTIONIST = "RECEPTIONIST";

    private final UserRepository userRepository;

    public UserManagementService() {
        this(new JdbcUserRepository());
    }

    public UserManagementService(
            UserRepository userRepository) {

        if (userRepository == null) {
            throw new IllegalArgumentException(
                    "User repository is required."
            );
        }

        this.userRepository = userRepository;
    }

    /*
     * ============================================================
     * READ OPERATIONS
     * ============================================================
     */

    public List<User> getAllUsers()
            throws SQLException {

        List<User> users =
                userRepository.findAll();

        /*
         * Password hashes must never be returned to
         * controllers, JSP pages or API clients.
         */
        for (User user : users) {
            removePasswordHash(user);
        }

        return users;
    }

    public Optional<User> getUserById(
            int userId)
            throws SQLException {

        validateUserId(userId);

        Optional<User> user =
                userRepository.findById(userId);

        user.ifPresent(
                this::removePasswordHash
        );

        return user;
    }

    /*
     * ============================================================
     * CREATE USER
     * ============================================================
     */

    public User createUser(
            String username,
            String plainPassword,
            String fullName,
            String role)
            throws SQLException {

        String normalizedUsername =
                normalizeUsername(username);

        String normalizedFullName =
                normalizeFullName(fullName);

        String normalizedRole =
                normalizeRole(role);

        validateUsername(normalizedUsername);
        validateFullName(normalizedFullName);

        if (userRepository.existsByUsername(
                normalizedUsername)) {

            throw new IllegalArgumentException(
                    "Username already exists."
            );
        }

        /*
         * PasswordHasher performs password validation
         * before generating the secure PBKDF2 hash.
         */
        String passwordHash =
                PasswordHasher.hashPassword(
                        plainPassword
                );

        User user = new User();

        user.setUsername(
                normalizedUsername
        );

        user.setPasswordHash(
                passwordHash
        );

        user.setFullName(
                normalizedFullName
        );

        user.setRole(
                normalizedRole
        );

        /*
         * Newly created staff accounts are active
         * unless an administrator later deactivates them.
         */
        user.setActive(true);

        int generatedUserId =
                userRepository.create(user);

        Optional<User> createdUser =
                userRepository.findById(
                        generatedUserId
                );

        if (createdUser.isEmpty()) {
            throw new SQLException(
                    "User account was created but could not be reloaded."
            );
        }

        User result =
                createdUser.get();

        removePasswordHash(result);

        return result;
    }

    /*
     * ============================================================
     * UPDATE USER DETAILS
     * ============================================================
     */

    public User updateUserDetails(
            int userId,
            String fullName,
            String role)
            throws SQLException {

        validateUserId(userId);

        String normalizedFullName =
                normalizeFullName(fullName);

        String normalizedRole =
                normalizeRole(role);

        validateFullName(
                normalizedFullName
        );

        User existingUser =
                requireUser(userId);

        /*
         * Protect the system from accidentally removing
         * the final active ADMIN role.
         */
        if (ROLE_ADMIN.equals(
                existingUser.getRole())
                && !ROLE_ADMIN.equals(
                        normalizedRole)
                && existingUser.isActive()) {

            ensureAnotherActiveAdministrator(
                    userId
            );
        }

        boolean updated =
                userRepository.updateDetails(
                        userId,
                        normalizedFullName,
                        normalizedRole
                );

        if (!updated) {
            throw new SQLException(
                    "Unable to update user account."
            );
        }

        User updatedUser =
                requireUser(userId);

        removePasswordHash(
                updatedUser
        );

        return updatedUser;
    }

    /*
     * ============================================================
     * ACTIVATE / DEACTIVATE USER
     * ============================================================
     */

    public User updateActiveStatus(
            int userId,
            boolean active)
            throws SQLException {

        validateUserId(userId);

        User existingUser =
                requireUser(userId);

        /*
         * Never allow the last active administrator
         * account to be deactivated.
         */
        if (!active
                && existingUser.isActive()
                && ROLE_ADMIN.equals(
                        existingUser.getRole())) {

            ensureAnotherActiveAdministrator(
                    userId
            );
        }

        boolean updated =
                userRepository.updateActiveStatus(
                        userId,
                        active
                );

        if (!updated) {
            throw new SQLException(
                    "Unable to update account status."
            );
        }

        User updatedUser =
                requireUser(userId);

        removePasswordHash(
                updatedUser
        );

        return updatedUser;
    }

    /*
     * ============================================================
     * RESET PASSWORD
     * ============================================================
     */

    public void resetPassword(
            int userId,
            String newPlainPassword)
            throws SQLException {

        validateUserId(userId);

        requireUser(userId);

        /*
         * The plain password exists only in memory.
         * Only the secure PBKDF2 hash is passed to
         * the repository.
         */
        String passwordHash =
                PasswordHasher.hashPassword(
                        newPlainPassword
                );

        boolean updated =
                userRepository.updatePasswordHash(
                        userId,
                        passwordHash
                );

        if (!updated) {
            throw new SQLException(
                    "Unable to reset user password."
            );
        }
    }

    /*
     * ============================================================
     * BUSINESS RULE HELPERS
     * ============================================================
     */

    private User requireUser(
            int userId)
            throws SQLException {

        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "User account was not found."
                        )
                );
    }

    private void ensureAnotherActiveAdministrator(
            int excludedUserId)
            throws SQLException {

        boolean anotherActiveAdmin =
                userRepository.findAll()
                        .stream()
                        .anyMatch(
                                user ->
                                        user.getUserId()
                                                != excludedUserId
                                        && user.isActive()
                                        && ROLE_ADMIN.equals(
                                                user.getRole()
                                        )
                        );

        if (!anotherActiveAdmin) {
            throw new IllegalArgumentException(
                    "The final active ADMIN account cannot be removed or deactivated."
            );
        }
    }

    /*
     * ============================================================
     * VALIDATION
     * ============================================================
     */

    private void validateUserId(
            int userId) {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Valid user ID is required."
            );
        }
    }

    private void validateUsername(
            String username) {

        if (username == null
                || username.isBlank()) {

            throw new IllegalArgumentException(
                    "Username is required."
            );
        }

        if (username.length() < 3
                || username.length() > 50) {

            throw new IllegalArgumentException(
                    "Username must contain between 3 and 50 characters."
            );
        }

        if (!username.matches(
                "[a-z0-9._-]+")) {

            throw new IllegalArgumentException(
                    "Username may contain only lowercase letters, numbers, dots, underscores and hyphens."
            );
        }
    }

    private void validateFullName(
            String fullName) {

        if (fullName == null
                || fullName.isBlank()) {

            throw new IllegalArgumentException(
                    "Full name is required."
            );
        }

        if (fullName.length() > 100) {
            throw new IllegalArgumentException(
                    "Full name must not exceed 100 characters."
            );
        }
    }

    private String normalizeUsername(
            String username) {

        if (username == null) {
            return null;
        }

        return username
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private String normalizeFullName(
            String fullName) {

        if (fullName == null) {
            return null;
        }

        return fullName.trim();
    }

    private String normalizeRole(
            String role) {

        if (role == null
                || role.isBlank()) {

            throw new IllegalArgumentException(
                    "User role is required."
            );
        }

        String normalizedRole =
                role.trim()
                        .toUpperCase(Locale.ROOT);

        if (!ROLE_ADMIN.equals(
                normalizedRole)
                && !ROLE_RECEPTIONIST.equals(
                        normalizedRole)) {

            throw new IllegalArgumentException(
                    "Role must be ADMIN or RECEPTIONIST."
            );
        }

        return normalizedRole;
    }

    /*
     * ============================================================
     * SECURITY
     * ============================================================
     */

    private void removePasswordHash(
            User user) {

        if (user != null) {
            user.setPasswordHash(null);
        }
    }
}