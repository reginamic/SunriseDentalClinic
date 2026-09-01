package com.sunrisedental.api.pattern.chainofresponsibility;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.Treatment;

import java.util.Objects;

/**
 * Holds the data shared between handlers in the
 * Appointment Chain of Responsibility.
 *
 * Each validation handler reads or enriches this context
 * before passing it to the next handler.
 */
public class AppointmentValidationContext {

    private final Appointment appointment;

    /*
     * During an update/reschedule, the current appointment
     * must be excluded from conflict detection.
     *
     * Null means a new appointment is being created.
     */
    private final Integer excludeAppointmentId;

    /*
     * TreatmentValidationHandler stores the validated treatment
     * here so the availability handler can use its duration
     * without querying it again.
     */
    private Treatment validatedTreatment;

    public AppointmentValidationContext(
            Appointment appointment,
            Integer excludeAppointmentId) {

        this.appointment =
                Objects.requireNonNull(
                        appointment,
                        "Appointment cannot be null."
                );

        this.excludeAppointmentId =
                excludeAppointmentId;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public Integer getExcludeAppointmentId() {
        return excludeAppointmentId;
    }

    public Treatment getValidatedTreatment() {
        return validatedTreatment;
    }

    public void setValidatedTreatment(
            Treatment validatedTreatment) {

        this.validatedTreatment =
                Objects.requireNonNull(
                        validatedTreatment,
                        "Validated treatment cannot be null."
                );
    }
}