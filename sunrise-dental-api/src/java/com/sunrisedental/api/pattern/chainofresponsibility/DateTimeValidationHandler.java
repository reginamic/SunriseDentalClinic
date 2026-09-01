package com.sunrisedental.api.pattern.chainofresponsibility;

import com.sunrisedental.api.model.Appointment;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Validates the appointment date and time.
 *
 * New or rescheduled appointments cannot be scheduled
 * for a date/time that has already passed.
 */
public class DateTimeValidationHandler
        extends AppointmentValidationHandler {

    @Override
    protected void validate(
            AppointmentValidationContext context)
            throws SQLException {

        Appointment appointment =
                context.getAppointment();

        /*
         * Appointment date is mandatory.
         */
        if (appointment.getAppointmentDate() == null) {

            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }

        /*
         * Appointment time is mandatory.
         */
        if (appointment.getAppointmentTime() == null) {

            throw new IllegalArgumentException(
                    "Appointment time is required."
            );
        }

        /*
         * Combine the selected date and time so we can
         * reject appointments that are already in the past.
         */
        LocalDateTime appointmentDateTime =
                LocalDateTime.of(
                        appointment.getAppointmentDate(),
                        appointment.getAppointmentTime()
                );

        if (appointmentDateTime
                .isBefore(
                        LocalDateTime.now()
                )) {

            throw new IllegalArgumentException(
                    "Appointment date and time cannot be in the past."
            );
        }
    }
}