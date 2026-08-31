package com.sunrisedental.api.service;

import com.sunrisedental.api.model.User;
import com.sunrisedental.api.repository.UserRepository;
import com.sunrisedental.api.security.PasswordHasher;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserManagementServiceTest {

    private InMemoryUserRepository repository;
    private UserManagementService service;

    @Before
    public void setUp() {

        repository =
                new InMemoryUserRepository();

        service =
                new UserManagementService(repository);
    }

    /*
     * ============================================================
     * CREATE USER
     * ============================================================
     */

    @Test
    public void createUserHashesPasswordAndRemovesHashFromResult()
            throws Exception {

        User createdUser =
                service.createUser(
                        "reception2",
                        "Secure@123",
                        "Reception Staff Two",
                        "RECEPTIONIST"
                );

        assertNotNull(createdUser);

        assertTrue(
                createdUser.getUserId() > 0
        );

        assertEquals(
                "reception2",
                createdUser.getUsername()
        );

        assertEquals(
                "Reception Staff Two",
                createdUser.getFullName()
        );

        assertEquals(
                "RECEPTIONIST",
                createdUser.getRole()
        );

        assertTrue(
                createdUser.isActive()
        );

        /*
         * Sensitive hash must not be exposed
         * by the service result.
         */
        assertNull(
                createdUser.getPasswordHash()
        );

        User storedUser =
                repository.findByUsername(
                        "reception2"
                )
                        .orElseThrow();

        assertNotNull(
                storedUser.getPasswordHash()
        );

        assertNotEquals(
                "Secure@123",
                storedUser.getPasswordHash()
        );

        assertTrue(
                PasswordHasher.verifyPassword(
                        "Secure@123",
                        storedUser.getPasswordHash()
                )
        );
    }

    @Test
    public void createUserRejectsDuplicateUsername()
            throws Exception {

        service.createUser(
                "reception2",
                "Secure@123",
                "Reception Staff Two",
                "RECEPTIONIST"
        );

        try {

            service.createUser(
                    "reception2",
                    "Another@123",
                    "Duplicate Reception",
                    "RECEPTIONIST"
            );

            fail(
                    "Expected duplicate username rejection."
            );

        } catch (IllegalArgumentException exception) {

            assertEquals(
                    "Username already exists.",
                    exception.getMessage()
            );
        }
    }

    @Test
    public void createUserRejectsInvalidRole()
            throws Exception {

        try {

            service.createUser(
                    "dentistuser",
                    "Secure@123",
                    "Dentist Account",
                    "DENTIST"
            );

            fail(
                    "Expected invalid role rejection."
            );

        } catch (IllegalArgumentException exception) {

            assertEquals(
                    "Role must be ADMIN or RECEPTIONIST.",
                    exception.getMessage()
            );
        }
    }

    /*
     * ============================================================
     * ADMIN PROTECTION
     * ============================================================
     */

    @Test
    public void cannotDeactivateFinalActiveAdmin()
            throws Exception {

        User admin =
                createStoredUser(
                        "admin",
                        "System Administrator",
                        "ADMIN",
                        true
                );

        try {

            service.updateActiveStatus(
                    admin.getUserId(),
                    false
            );

            fail(
                    "Expected final ADMIN protection."
            );

        } catch (IllegalArgumentException exception) {

            assertTrue(
                    exception.getMessage()
                            .contains(
                                    "final active ADMIN"
                            )
            );
        }

        User stillActive =
                repository.findById(
                        admin.getUserId()
                )
                        .orElseThrow();

        assertTrue(
                stillActive.isActive()
        );
    }

    @Test
    public void adminCanBeDeactivatedWhenAnotherActiveAdminExists()
            throws Exception {

        User firstAdmin =
                createStoredUser(
                        "admin1",
                        "Administrator One",
                        "ADMIN",
                        true
                );

        createStoredUser(
                "admin2",
                "Administrator Two",
                "ADMIN",
                true
        );

        User updated =
                service.updateActiveStatus(
                        firstAdmin.getUserId(),
                        false
                );

        assertFalse(
                updated.isActive()
        );
    }

    @Test
    public void cannotDemoteFinalActiveAdmin()
            throws Exception {

        User admin =
                createStoredUser(
                        "admin",
                        "System Administrator",
                        "ADMIN",
                        true
                );

        try {

            service.updateUserDetails(
                    admin.getUserId(),
                    "System Administrator",
                    "RECEPTIONIST"
            );

            fail(
                    "Expected final ADMIN role protection."
            );

        } catch (IllegalArgumentException exception) {

            assertTrue(
                    exception.getMessage()
                            .contains(
                                    "final active ADMIN"
                            )
            );
        }
    }

    /*
     * ============================================================
     * PASSWORD RESET
     * ============================================================
     */

    @Test
    public void resetPasswordStoresNewSecureHash()
            throws Exception {

        User user =
                createStoredUser(
                        "reception",
                        "Reception Staff",
                        "RECEPTIONIST",
                        true
                );

        service.resetPassword(
                user.getUserId(),
                "NewSecure@456"
        );

        User updated =
                repository.findById(
                        user.getUserId()
                )
                        .orElseThrow();

        assertNotNull(
                updated.getPasswordHash()
        );

        assertTrue(
                PasswordHasher.verifyPassword(
                        "NewSecure@456",
                        updated.getPasswordHash()
                )
        );
    }

    /*
     * ============================================================
     * TEST HELPERS
     * ============================================================
     */

    private User createStoredUser(
            String username,
            String fullName,
            String role,
            boolean active)
            throws SQLException {

        User user =
                new User();

        user.setUsername(username);

        user.setPasswordHash(
                PasswordHasher.hashPassword(
                        "Existing@123"
                )
        );

        user.setFullName(fullName);
        user.setRole(role);
        user.setActive(active);

        int generatedId =
                repository.create(user);

        return repository.findById(
                generatedId
        )
                .orElseThrow();
    }

    /*
     * ============================================================
     * IN-MEMORY TEST REPOSITORY
     * ============================================================
     */

    private static class InMemoryUserRepository
            implements UserRepository {

        private final List<User> users =
                new ArrayList<>();

        private int nextId = 1;

        @Override
        public Optional<User> findByUsername(
                String username) {

            return users.stream()
                    .filter(
                            user ->
                                    user.getUsername()
                                            .equals(username)
                    )
                    .findFirst()
                    .map(
                            this::copyUser
                    );
        }

        @Override
        public Optional<User> findById(
                int userId) {

            return users.stream()
                    .filter(
                            user ->
                                    user.getUserId()
                                            == userId
                    )
                    .findFirst()
                    .map(
                            this::copyUser
                    );
        }

        @Override
        public List<User> findAll() {

            List<User> result =
                    new ArrayList<>();

            for (User user : users) {
                result.add(
                        copyUser(user)
                );
            }

            return result;
        }

        @Override
        public boolean existsByUsername(
                String username) {

            return users.stream()
                    .anyMatch(
                            user ->
                                    user.getUsername()
                                            .equals(username)
                    );
        }

        @Override
        public int create(
                User user) {

            User stored =
                    copyUser(user);

            stored.setUserId(
                    nextId++
            );

            stored.setCreatedAt(
                    LocalDateTime.now()
            );

            users.add(stored);

            return stored.getUserId();
        }

        @Override
        public boolean updateDetails(
                int userId,
                String fullName,
                String role) {

            User user =
                    findInternal(userId);

            if (user == null) {
                return false;
            }

            user.setFullName(fullName);
            user.setRole(role);

            return true;
        }

        @Override
        public boolean updateActiveStatus(
                int userId,
                boolean active) {

            User user =
                    findInternal(userId);

            if (user == null) {
                return false;
            }

            user.setActive(active);

            return true;
        }

        @Override
        public boolean updatePasswordHash(
                int userId,
                String passwordHash) {

            User user =
                    findInternal(userId);

            if (user == null) {
                return false;
            }

            user.setPasswordHash(
                    passwordHash
            );

            return true;
        }

        private User findInternal(
                int userId) {

            return users.stream()
                    .filter(
                            user ->
                                    user.getUserId()
                                            == userId
                    )
                    .findFirst()
                    .orElse(null);
        }

        private User copyUser(
                User source) {

            return new User(
                    source.getUserId(),
                    source.getUsername(),
                    source.getPasswordHash(),
                    source.getFullName(),
                    source.getRole(),
                    source.isActive(),
                    source.getCreatedAt()
            );
        }
    }
}