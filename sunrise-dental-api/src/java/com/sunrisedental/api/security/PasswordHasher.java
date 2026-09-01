package com.sunrisedental.api.security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {

    private static final String ALGORITHM =
            "PBKDF2WithHmacSHA256";

    private static final int ITERATIONS =
            210_000;

    private static final int SALT_LENGTH =
            16;

    private static final int KEY_LENGTH =
            256;

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private PasswordHasher() {
        // Utility class - prevent object creation.
    }

    public static String hashPassword(String password) {

        validatePassword(password);

        byte[] salt =
                new byte[SALT_LENGTH];

        SECURE_RANDOM.nextBytes(salt);

        byte[] hash =
                generateHash(
                        password.toCharArray(),
                        salt,
                        ITERATIONS
                );

        return ITERATIONS
                + ":"
                + Base64.getEncoder()
                        .encodeToString(salt)
                + ":"
                + Base64.getEncoder()
                        .encodeToString(hash);
    }

    public static boolean verifyPassword(
            String password,
            String storedHash) {

        if (password == null
                || storedHash == null
                || storedHash.isBlank()) {

            return false;
        }

        try {

            String[] parts =
                    storedHash.split(":");

            if (parts.length != 3) {
                return false;
            }

            int iterations =
                    Integer.parseInt(parts[0]);

            byte[] salt =
                    Base64.getDecoder()
                            .decode(parts[1]);

            byte[] expectedHash =
                    Base64.getDecoder()
                            .decode(parts[2]);

            byte[] actualHash =
                    generateHash(
                            password.toCharArray(),
                            salt,
                            iterations
                    );

            return MessageDigest.isEqual(
                    expectedHash,
                    actualHash
            );

        } catch (IllegalArgumentException exception) {

            return false;
        }
    }

    private static byte[] generateHash(
            char[] password,
            byte[] salt,
            int iterations) {

        PBEKeySpec specification =
                new PBEKeySpec(
                        password,
                        salt,
                        iterations,
                        KEY_LENGTH
                );

        try {

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            ALGORITHM
                    );

            return factory
                    .generateSecret(specification)
                    .getEncoded();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to securely process password.",
                    exception
            );

        } finally {

            specification.clearPassword();
        }
    }

    private static void validatePassword(
            String password) {

        if (password == null
                || password.isBlank()) {

            throw new IllegalArgumentException(
                    "Password is required."
            );
        }

        if (password.length() < 8) {

            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters."
            );
        }
    }
}