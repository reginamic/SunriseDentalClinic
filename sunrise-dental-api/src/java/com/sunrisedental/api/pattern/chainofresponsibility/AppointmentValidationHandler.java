package com.sunrisedental.api.pattern.chainofresponsibility;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Base handler for the Appointment Validation
 * Chain of Responsibility.
 *
 * Each concrete handler validates one business rule.
 * If validation succeeds, the request is automatically
 * passed to the next handler in the chain.
 *
 * If a handler throws an exception, processing stops
 * immediately and the appointment is rejected.
 */
public abstract class AppointmentValidationHandler {

    private AppointmentValidationHandler nextHandler;

    /**
     * Connects this handler to the next handler.
     *
     * Returning the supplied handler allows fluent
     * chain construction such as:
     *
     * patientHandler
     *     .setNext(dentistHandler)
     *     .setNext(treatmentHandler);
     *
     * @param nextHandler next validation handler
     * @return the supplied next handler
     */
    public AppointmentValidationHandler setNext(
            AppointmentValidationHandler nextHandler) {

        this.nextHandler =
                Objects.requireNonNull(
                        nextHandler,
                        "Next validation handler cannot be null."
                );

        return nextHandler;
    }

    /**
     * Executes this handler and, when successful,
     * forwards validation to the next handler.
     *
     * @param context shared appointment validation context
     */
    public final void handle(
            AppointmentValidationContext context)
            throws SQLException {

        Objects.requireNonNull(
                context,
                "Appointment validation context cannot be null."
        );

        /*
         * Each concrete handler performs exactly
         * one validation responsibility.
         */
        validate(
                context
        );

        /*
         * Continue through the chain only when
         * the current handler succeeds.
         */
        if (nextHandler != null) {

            nextHandler.handle(
                    context
            );
        }
    }

    /**
     * Validation rule implemented by each concrete handler.
     *
     * Throw IllegalArgumentException when the business
     * rule is violated.
     */
    protected abstract void validate(
            AppointmentValidationContext context)
            throws SQLException;
}