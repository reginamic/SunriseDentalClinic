package com.sunrisedental.api.service;

import com.sunrisedental.api.model.Treatment;
import com.sunrisedental.api.repository.TreatmentRepository;
import com.sunrisedental.api.repository.impl.JdbcTreatmentRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TreatmentService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final TreatmentRepository treatmentRepository;

    /*
     * Used by the real application.
     */
    public TreatmentService() {
        this(new JdbcTreatmentRepository());
    }

    /*
     * Dependency-injection constructor.
     * This allows us to inject a mock repository during JUnit testing.
     */
    public TreatmentService(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = Objects.requireNonNull(
                treatmentRepository,
                "TreatmentRepository cannot be null."
        );
    }

    public List<Treatment> getAllTreatments() throws SQLException {
        return treatmentRepository.findAll();
    }

    public Optional<Treatment> getTreatmentById(int treatmentId)
            throws SQLException {

        if (treatmentId <= 0) {
            throw new IllegalArgumentException(
                    "Treatment ID must be greater than zero."
            );
        }

        return treatmentRepository.findById(treatmentId);
    }

    public Optional<Treatment> getTreatmentByCode(String treatmentCode)
            throws SQLException {

        if (isBlank(treatmentCode)) {
            throw new IllegalArgumentException(
                    "Treatment code is required."
            );
        }

        return treatmentRepository.findByCode(
                treatmentCode.trim().toUpperCase()
        );
    }

    public List<Treatment> searchTreatments(String keyword)
            throws SQLException {

        if (isBlank(keyword)) {
            return treatmentRepository.findAll();
        }

        return treatmentRepository.search(keyword.trim());
    }

    public Treatment registerTreatment(Treatment treatment)
            throws SQLException {

        validateTreatment(treatment);

        normalizeTreatment(treatment);

        if (isBlank(treatment.getTreatmentCode())) {
            treatment.setTreatmentCode(
                    generateTreatmentCode()
            );
        } else {
            treatment.setTreatmentCode(
                    treatment.getTreatmentCode()
                            .trim()
                            .toUpperCase()
            );
        }

        if (treatmentRepository
                .findByCode(treatment.getTreatmentCode())
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Treatment code already exists."
            );
        }

        treatment.setActive(true);

        return treatmentRepository.save(treatment);
    }

    public boolean updateTreatment(Treatment treatment)
            throws SQLException {

        if (treatment == null) {
            throw new IllegalArgumentException(
                    "Treatment information is required."
            );
        }

        if (treatment.getTreatmentId() <= 0) {
            throw new IllegalArgumentException(
                    "Valid treatment ID is required."
            );
        }

        Treatment existingTreatment =
                treatmentRepository
                        .findById(treatment.getTreatmentId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Treatment not found."
                                )
                        );

        /*
         * Treatment code remains immutable during normal update.
         */
        treatment.setTreatmentCode(
                existingTreatment.getTreatmentCode()
        );

        normalizeTreatment(treatment);
        validateTreatment(treatment);

        return treatmentRepository.update(treatment);
    }

    private void validateTreatment(Treatment treatment) {

        if (treatment == null) {
            throw new IllegalArgumentException(
                    "Treatment information is required."
            );
        }

        if (isBlank(treatment.getTreatmentName())) {
            throw new IllegalArgumentException(
                    "Treatment name is required."
            );
        }

        if (treatment.getTreatmentName().trim().length() < 3) {
            throw new IllegalArgumentException(
                    "Treatment name must contain at least 3 characters."
            );
        }

        if (treatment.getTreatmentPrice() == null) {
            throw new IllegalArgumentException(
                    "Treatment price is required."
            );
        }

        if (treatment.getTreatmentPrice().compareTo(ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Treatment price cannot be negative."
            );
        }

        if (treatment.getConsultationFee() == null) {
            throw new IllegalArgumentException(
                    "Consultation fee is required."
            );
        }

        if (treatment.getConsultationFee().compareTo(ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Consultation fee cannot be negative."
            );
        }

        Integer duration =
                treatment.getEstimatedDurationMinutes();

        if (duration != null && duration <= 0) {
            throw new IllegalArgumentException(
                    "Estimated duration must be greater than zero."
            );
        }

        if (duration != null && duration > 480) {
            throw new IllegalArgumentException(
                    "Estimated duration cannot exceed 480 minutes."
            );
        }
    }

    private void normalizeTreatment(Treatment treatment) {

        treatment.setTreatmentName(
                treatment.getTreatmentName().trim()
        );

        if (treatment.getDescription() != null) {
            treatment.setDescription(
                    treatment.getDescription().trim()
            );
        }
    }

    private String generateTreatmentCode()
            throws SQLException {

        List<Treatment> treatments =
                treatmentRepository.findAll();

        int nextId = treatments.stream()
                .map(Treatment::getTreatmentId)
                .max(Comparator.naturalOrder())
                .orElse(0)
                + 1;

        String generatedCode;

        do {
            generatedCode =
                    String.format("TRT-%03d", nextId);

            nextId++;

        } while (treatmentRepository
                .findByCode(generatedCode)
                .isPresent());

        return generatedCode;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}