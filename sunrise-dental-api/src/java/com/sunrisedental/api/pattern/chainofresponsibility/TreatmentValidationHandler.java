package com.sunrisedental.api.pattern.chainofresponsibility;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.Treatment;
import com.sunrisedental.api.repository.TreatmentRepository;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Validates that the selected treatment exists,
 * is active, and has a valid estimated duration.
 *
 * The validated Treatment is stored in the shared
 * AppointmentValidationContext so later handlers
 * can reuse it without another database lookup.
 */
public class TreatmentValidationHandler
        extends AppointmentValidationHandler {

    private final TreatmentRepository treatmentRepository;

    public TreatmentValidationHandler(
            TreatmentRepository treatmentRepository) {

        this.treatmentRepository =
                Objects.requireNonNull(
                        treatmentRepository,
                        "TreatmentRepository cannot be null."
                );
    }

    @Override
    protected void validate(
            AppointmentValidationContext context)
            throws SQLException {

        Appointment appointment =
                context.getAppointment();

        int treatmentId =
                appointment.getTreatmentId();

        /*
         * Basic identifier validation.
         */
        if (treatmentId <= 0) {

            throw new IllegalArgumentException(
                    "Valid treatment ID is required."
            );
        }

        /*
         * Treatment must exist.
         */
        Treatment treatment =
                treatmentRepository
                        .findById(
                                treatmentId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Selected treatment does not exist."
                                )
                        );

        /*
         * Only active treatments may be used
         * for new/rescheduled appointments.
         */
        if (!treatment.isActive()) {

            throw new IllegalArgumentException(
                    "Selected treatment is inactive."
            );
        }

        /*
         * A valid duration is required for
         * duration-aware overlap checking.
         */
        Integer durationMinutes =
                treatment
                        .getEstimatedDurationMinutes();

        if (durationMinutes == null
                || durationMinutes <= 0) {

            throw new IllegalArgumentException(
                    "Selected treatment does not have "
                    + "a valid estimated duration."
            );
        }

        /*
         * Store the validated Treatment so the
         * availability handler can reuse it.
         */
        context.setValidatedTreatment(
                treatment
        );
    }
}