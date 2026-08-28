package com.sunrisedental.api.service;

import com.sunrisedental.api.model.Dentist;
import com.sunrisedental.api.repository.DentistRepository;
import com.sunrisedental.api.repository.impl.JdbcDentistRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DentistService {

    private final DentistRepository dentistRepository;

    public DentistService() {
        this.dentistRepository = new JdbcDentistRepository();
    }

    public List<Dentist> getAllDentists()
            throws SQLException {

        return dentistRepository.findAll();
    }

    public Optional<Dentist> getDentistById(int dentistId)
            throws SQLException {

        if (dentistId <= 0) {
            throw new IllegalArgumentException(
                    "Dentist ID must be greater than zero."
            );
        }

        return dentistRepository.findById(dentistId);
    }

    public Optional<Dentist> getDentistByCode(String dentistCode)
            throws SQLException {

        if (dentistCode == null
                || dentistCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Dentist code is required."
            );
        }

        return dentistRepository.findByCode(
                dentistCode.trim()
        );
    }

    public List<Dentist> searchDentists(String keyword)
            throws SQLException {

        if (keyword == null || keyword.isBlank()) {
            return getAllDentists();
        }

        return dentistRepository.search(
                keyword.trim()
        );
    }

    public Dentist registerDentist(Dentist dentist)
            throws SQLException {

        validateDentist(dentist);

        if (dentist.getDentistCode() == null
                || dentist.getDentistCode().isBlank()) {

            dentist.setDentistCode(
                    generateDentistCode()
            );
        }

        Optional<Dentist> existingDentist =
                dentistRepository.findByCode(
                        dentist.getDentistCode()
                );

        if (existingDentist.isPresent()) {
            throw new IllegalArgumentException(
                    "Dentist code already exists."
            );
        }

        dentist.setActive(true);

        return dentistRepository.save(dentist);
    }

    public boolean updateDentist(Dentist dentist)
            throws SQLException {

        if (dentist.getDentistId() <= 0) {
            throw new IllegalArgumentException(
                    "Valid dentist ID is required."
            );
        }

        validateDentist(dentist);

        Optional<Dentist> existingDentist =
                dentistRepository.findById(
                        dentist.getDentistId()
                );

        if (existingDentist.isEmpty()) {
            throw new IllegalArgumentException(
                    "Dentist was not found."
            );
        }

        return dentistRepository.update(dentist);
    }

    private void validateDentist(Dentist dentist) {

        if (dentist == null) {
            throw new IllegalArgumentException(
                    "Dentist information is required."
            );
        }

        if (dentist.getFullName() == null
                || dentist.getFullName().isBlank()) {

            throw new IllegalArgumentException(
                    "Dentist name is required."
            );
        }

        if (dentist.getFullName().trim().length() < 3) {
            throw new IllegalArgumentException(
                    "Dentist name must contain at least 3 characters."
            );
        }

        if (dentist.getSpecialization() == null
                || dentist.getSpecialization().isBlank()) {

            throw new IllegalArgumentException(
                    "Dentist specialization is required."
            );
        }

        if (dentist.getContactNumber() != null
                && !dentist.getContactNumber().isBlank()
                && !dentist.getContactNumber()
                        .matches("^[0-9]{10}$")) {

            throw new IllegalArgumentException(
                    "Dentist contact number must contain exactly 10 digits."
            );
        }

        if (dentist.getEmail() != null
                && !dentist.getEmail().isBlank()
                && !dentist.getEmail()
                        .matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            throw new IllegalArgumentException(
                    "Invalid dentist email address."
            );
        }
    }

    private String generateDentistCode()
            throws SQLException {

        int nextNumber =
                dentistRepository
                        .findAll()
                        .stream()
                        .mapToInt(Dentist::getDentistId)
                        .max()
                        .orElse(0) + 1;

        return String.format(
                "DEN-%03d",
                nextNumber
        );
    }
}