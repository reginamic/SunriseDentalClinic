package com.sunrisedental.api.service;

import com.sunrisedental.api.model.Patient;
import com.sunrisedental.api.repository.PatientRepository;
import com.sunrisedental.api.repository.impl.JdbcPatientRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService() {
        this.patientRepository = new JdbcPatientRepository();
    }

    public List<Patient> getAllPatients() throws SQLException {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(int patientId)
            throws SQLException {

        if (patientId <= 0) {
            throw new IllegalArgumentException(
                    "Patient ID must be greater than zero."
            );
        }

        return patientRepository.findById(patientId);
    }

    public Optional<Patient> getPatientByCode(String patientCode)
            throws SQLException {

        if (patientCode == null || patientCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Patient code is required."
            );
        }

        return patientRepository.findByCode(
                patientCode.trim()
        );
    }

    public List<Patient> searchPatients(String keyword)
            throws SQLException {

        if (keyword == null || keyword.isBlank()) {
            return getAllPatients();
        }

        return patientRepository.search(
                keyword.trim()
        );
    }

    public Patient registerPatient(Patient patient)
            throws SQLException {

        validatePatient(patient);

        if (patient.getPatientCode() == null
                || patient.getPatientCode().isBlank()) {

            patient.setPatientCode(
                    generatePatientCode()
            );
        }

        Optional<Patient> existingPatient =
                patientRepository.findByCode(
                        patient.getPatientCode()
                );

        if (existingPatient.isPresent()) {
            throw new IllegalArgumentException(
                    "Patient code already exists."
            );
        }

        return patientRepository.save(patient);
    }

    public boolean updatePatient(Patient patient)
            throws SQLException {

        if (patient.getPatientId() <= 0) {
            throw new IllegalArgumentException(
                    "Valid patient ID is required."
            );
        }

        validatePatient(patient);

        Optional<Patient> existingPatient =
                patientRepository.findById(
                        patient.getPatientId()
                );

        if (existingPatient.isEmpty()) {
            throw new IllegalArgumentException(
                    "Patient was not found."
            );
        }

        return patientRepository.update(patient);
    }

    private void validatePatient(Patient patient) {

        if (patient == null) {
            throw new IllegalArgumentException(
                    "Patient information is required."
            );
        }

        if (patient.getFullName() == null
                || patient.getFullName().isBlank()) {

            throw new IllegalArgumentException(
                    "Patient name is required."
            );
        }

        if (patient.getFullName().trim().length() < 3) {
            throw new IllegalArgumentException(
                    "Patient name must contain at least 3 characters."
            );
        }

        if (patient.getAddress() == null
                || patient.getAddress().isBlank()) {

            throw new IllegalArgumentException(
                    "Patient address is required."
            );
        }

        if (patient.getContactNumber() == null
                || !patient.getContactNumber()
                        .matches("^[0-9]{10}$")) {

            throw new IllegalArgumentException(
                    "Contact number must contain exactly 10 digits."
            );
        }

        if (patient.getEmail() != null
                && !patient.getEmail().isBlank()
                && !patient.getEmail()
                        .matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            throw new IllegalArgumentException(
                    "Invalid email address."
            );
        }

        if (patient.getGender() != null
                && !patient.getGender().isBlank()) {

            String gender =
                    patient.getGender().toUpperCase();

            if (!gender.equals("MALE")
                    && !gender.equals("FEMALE")
                    && !gender.equals("OTHER")) {

                throw new IllegalArgumentException(
                        "Gender must be MALE, FEMALE or OTHER."
                );
            }

            patient.setGender(gender);
        }
    }

    private String generatePatientCode()
            throws SQLException {

        int nextNumber =
                patientRepository
                        .findAll()
                        .stream()
                        .mapToInt(Patient::getPatientId)
                        .max()
                        .orElse(0) + 1;

        return String.format(
                "PAT-%04d",
                nextNumber
        );
    }
}