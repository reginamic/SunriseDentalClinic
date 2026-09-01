package com.sunrisedental.api.pattern.chainofresponsibility;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.Dentist;
import com.sunrisedental.api.repository.DentistRepository;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Validates that the selected dentist exists
 * and is active.
 *
 * Inactive dentists cannot be assigned to
 * new or rescheduled appointments.
 */
public class DentistValidationHandler
        extends AppointmentValidationHandler {

    private final DentistRepository dentistRepository;

    public DentistValidationHandler(
            DentistRepository dentistRepository) {

        this.dentistRepository =
                Objects.requireNonNull(
                        dentistRepository,
                        "DentistRepository cannot be null."
                );
    }

    @Override
    protected void validate(
            AppointmentValidationContext context)
            throws SQLException {

        Appointment appointment =
                context.getAppointment();

        int dentistId =
                appointment.getDentistId();

        /*
         * Basic identifier validation.
         */
        if (dentistId <= 0) {

            throw new IllegalArgumentException(
                    "Valid dentist ID is required."
            );
        }

        /*
         * Dentist must exist.
         */
        Dentist dentist =
                dentistRepository
                        .findById(
                                dentistId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Selected dentist does not exist."
                                )
                        );

        /*
         * Only active dentists may receive
         * new/rescheduled appointments.
         */
        if (!dentist.isActive()) {

            throw new IllegalArgumentException(
                    "Selected dentist is inactive."
            );
        }
    }
}