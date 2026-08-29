package com.sunrisedental.api.pattern.chainofresponsibility;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.repository.PatientRepository;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Validates that the patient selected for an appointment
 * exists in the system.
 *
 * This handler represents the first stage of the
 * Appointment Validation Chain of Responsibility.
 */
public class PatientValidationHandler
        extends AppointmentValidationHandler {

    private final PatientRepository patientRepository;

    public PatientValidationHandler(
            PatientRepository patientRepository) {

        this.patientRepository =
                Objects.requireNonNull(
                        patientRepository,
                        "PatientRepository cannot be null."
                );
    }

    @Override
    protected void validate(
            AppointmentValidationContext context)
            throws SQLException {

        Appointment appointment =
                context.getAppointment();

        int patientId =
                appointment.getPatientId();

        /*
         * Basic identifier validation.
         */
        if (patientId <= 0) {

            throw new IllegalArgumentException(
                    "Valid patient ID is required."
            );
        }

        /*
         * Patient must exist in the database.
         */
        if (patientRepository
                .findById(
                        patientId
                )
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Selected patient does not exist."
            );
        }
    }
}