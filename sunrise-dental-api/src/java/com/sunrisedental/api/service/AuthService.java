package com.sunrisedental.api.service;

import com.sunrisedental.api.model.User;
import com.sunrisedental.api.repository.UserRepository;
import com.sunrisedental.api.repository.impl.JdbcUserRepository;
import com.sunrisedental.api.security.PasswordHasher;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository;

    /*
     * Used by the real application.
     */
    public AuthService() {
        this(new JdbcUserRepository());
    }

    /*
     * Dependency-injection constructor.
     * Useful later for JUnit / Mockito testing.
     */
    public AuthService(UserRepository userRepository) {

        this.userRepository =
                Objects.requireNonNull(
                        userRepository,
                        "UserRepository cannot be null."
                );
    }

    public User authenticate(
            String username,
            String password)
            throws SQLException {

        validateCredentials(
                username,
                password
        );

        Optional<User> optionalUser =
                userRepository.findByUsername(
                        username.trim()
                );

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid username or password."
            );
        }

        User user =
                optionalUser.get();

        if (!user.isActive()) {
            throw new IllegalArgumentException(
                    "This user account is inactive."
            );
        }

        boolean passwordMatches =
                PasswordHasher.verifyPassword(
                        password,
                        user.getPasswordHash()
                );

        if (!passwordMatches) {
            throw new IllegalArgumentException(
                    "Invalid username or password."
            );
        }

        /*
         * Never allow the password hash to leave
         * the authentication service unnecessarily.
         */
        user.setPasswordHash(null);

        return user;
    }

    private void validateCredentials(
            String username,
            String password) {

        if (username == null
                || username.isBlank()) {

            throw new IllegalArgumentException(
                    "Username is required."
            );
        }

        if (password == null
                || password.isBlank()) {

            throw new IllegalArgumentException(
                    "Password is required."
            );
        }
    }
}