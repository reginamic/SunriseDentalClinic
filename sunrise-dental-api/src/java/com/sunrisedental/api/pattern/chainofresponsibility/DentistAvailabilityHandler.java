package com.sunrisedental.api.pattern.chainofresponsibility;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.Treatment;
import com.sunrisedental.api.repository.AppointmentRepository;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Final scheduling handler in the Appointment
 * Chain of Responsibility.
 *
 * Checks whether the selected dentist already has an
 * overlapping appointment for the proposed time period.
 *
 * Conflict detection is duration-aware and ignores the
 * appointment currently being edited during rescheduling.
 */
public class DentistAvailabilityHandler
        extends AppointmentValidationHandler {

    private final AppointmentRepository appointmentRepository;

    public DentistAvailabilityHandler(
            AppointmentRepository appointmentRepository) {

        this.appointmentRepository =
                Objects.requireNonNull(
                        appointmentRepository,
                        "AppointmentRepository cannot be null."
                );
    }

    @Override
    protected void validate(
            AppointmentValidationContext context)
            throws SQLException {

        Appointment appointment =
                context.getAppointment();

        /*
         * TreatmentValidationHandler should already have
         * loaded and validated the Treatment.
         */
        Treatment treatment =
                context.getValidatedTreatment();

        if (treatment == null) {

            throw new IllegalStateException(
                    "Treatment must be validated before "
                    + "dentist availability is checked."
            );
        }

        Integer durationMinutes =
                treatment.getEstimatedDurationMinutes();

        if (durationMinutes == null
                || durationMinutes <= 0) {

            throw new IllegalArgumentException(
                    "Selected treatment does not have "
                    + "a valid estimated duration."
            );
        }

        boolean alreadyBooked =
                appointmentRepository
                        .existsDentistBooking(
                                appointment.getDentistId(),
                                appointment.getAppointmentDate(),
                                appointment.getAppointmentTime(),
                                durationMinutes,
                                context.getExcludeAppointmentId()
                        );

        if (alreadyBooked) {

            throw new IllegalArgumentException(
                    "The selected dentist has another "
                    + "appointment that overlaps this time period."
            );
        }
    }
}