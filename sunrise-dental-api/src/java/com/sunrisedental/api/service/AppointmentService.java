package com.sunrisedental.api.service;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.AppointmentStatus;

import com.sunrisedental.api.pattern.chainofresponsibility.AppointmentValidationContext;
import com.sunrisedental.api.pattern.chainofresponsibility.AppointmentValidationHandler;
import com.sunrisedental.api.pattern.chainofresponsibility.DateTimeValidationHandler;
import com.sunrisedental.api.pattern.chainofresponsibility.DentistAvailabilityHandler;
import com.sunrisedental.api.pattern.chainofresponsibility.DentistValidationHandler;
import com.sunrisedental.api.pattern.chainofresponsibility.PatientValidationHandler;
import com.sunrisedental.api.pattern.chainofresponsibility.TreatmentValidationHandler;

import com.sunrisedental.api.pattern.memento.AppointmentMemento;

import com.sunrisedental.api.repository.AppointmentHistoryRepository;
import com.sunrisedental.api.repository.AppointmentRepository;
import com.sunrisedental.api.repository.DentistRepository;
import com.sunrisedental.api.repository.PatientRepository;
import com.sunrisedental.api.repository.TreatmentRepository;

import com.sunrisedental.api.repository.impl.JdbcAppointmentHistoryRepository;
import com.sunrisedental.api.repository.impl.JdbcAppointmentRepository;
import com.sunrisedental.api.repository.impl.JdbcDentistRepository;
import com.sunrisedental.api.repository.impl.JdbcPatientRepository;
import com.sunrisedental.api.repository.impl.JdbcTreatmentRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentRepository treatmentRepository;
    private final AppointmentHistoryRepository appointmentHistoryRepository;

    /*
     * Root handler of the Appointment
     * Chain of Responsibility.
     */
    private final AppointmentValidationHandler
            schedulingValidationChain;

    /*
     * =========================================================
     * CONSTRUCTORS
     * =========================================================
     */

    /**
     * Used by the real application.
     */
    public AppointmentService() {

        this(
                new JdbcAppointmentRepository(),
                new JdbcPatientRepository(),
                new JdbcDentistRepository(),
                new JdbcTreatmentRepository(),
                new JdbcAppointmentHistoryRepository()
        );
    }

    /**
     * Backward-compatible dependency-injection constructor.
     *
     * Existing tests/callers that inject the four core
     * repositories can continue to compile.
     */
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DentistRepository dentistRepository,
            TreatmentRepository treatmentRepository) {

        this(
                appointmentRepository,
                patientRepository,
                dentistRepository,
                treatmentRepository,
                new JdbcAppointmentHistoryRepository()
        );
    }

    /**
     * Full dependency-injection constructor.
     *
     * Repository interfaces are injected so the service
     * and its validation chain can later be unit tested
     * using mocks or fake repositories.
     */
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DentistRepository dentistRepository,
            TreatmentRepository treatmentRepository,
            AppointmentHistoryRepository appointmentHistoryRepository) {

        this.appointmentRepository =
                Objects.requireNonNull(
                        appointmentRepository,
                        "AppointmentRepository cannot be null."
                );

        this.patientRepository =
                Objects.requireNonNull(
                        patientRepository,
                        "PatientRepository cannot be null."
                );

        this.dentistRepository =
                Objects.requireNonNull(
                        dentistRepository,
                        "DentistRepository cannot be null."
                );

        this.treatmentRepository =
                Objects.requireNonNull(
                        treatmentRepository,
                        "TreatmentRepository cannot be null."
                );

        this.appointmentHistoryRepository =
                Objects.requireNonNull(
                        appointmentHistoryRepository,
                        "AppointmentHistoryRepository cannot be null."
                );

        /*
         * Build the Chain of Responsibility once.
         */
        this.schedulingValidationChain =
                buildSchedulingValidationChain();
    }

    /*
     * =========================================================
     * READ OPERATIONS
     * =========================================================
     */

    public List<Appointment> getAllAppointments()
            throws SQLException {

        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(
            int appointmentId)
            throws SQLException {

        if (appointmentId <= 0) {

            throw new IllegalArgumentException(
                    "Appointment ID must be greater than zero."
            );
        }

        return appointmentRepository.findById(
                appointmentId
        );
    }

    public Optional<Appointment> getAppointmentByNumber(
            String appointmentNumber)
            throws SQLException {

        if (isBlank(appointmentNumber)) {

            throw new IllegalArgumentException(
                    "Appointment number is required."
            );
        }

        return appointmentRepository.findByNumber(
                appointmentNumber
                        .trim()
                        .toUpperCase()
        );
    }

    public List<Appointment> getAppointmentsByDate(
            java.time.LocalDate appointmentDate)
            throws SQLException {

        if (appointmentDate == null) {

            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }

        return appointmentRepository.findByDate(
                appointmentDate
        );
    }

    public List<Appointment> getAppointmentsByPatient(
            int patientId)
            throws SQLException {

        if (patientId <= 0) {

            throw new IllegalArgumentException(
                    "Valid patient ID is required."
            );
        }

        return appointmentRepository.findByPatientId(
                patientId
        );
    }

    /*
     * =========================================================
     * CREATE APPOINTMENT
     * =========================================================
     */

    public Appointment createAppointment(
            Appointment appointment)
            throws SQLException {

        /*
         * Validate fields that belong to the service itself.
         *
         * Patient/dentist/treatment/date/time/conflict rules
         * are intentionally handled by the Chain.
         */
        validateCommonAppointmentData(
                appointment
        );

        /*
         * All newly registered appointments begin as scheduled
         * unless no explicit status has been supplied.
         */
        if (appointment.getStatus() == null) {

            appointment.setStatus(
                    AppointmentStatus.SCHEDULED
            );
        }

        /*
         * CHAIN OF RESPONSIBILITY
         *
         * Patient
         *   ↓
         * Dentist
         *   ↓
         * Treatment
         *   ↓
         * Date/Time
         *   ↓
         * Dentist Availability
         */
        runSchedulingValidationChain(
                appointment,
                null
        );

        /*
         * Appointment number is generated only after
         * all business validation succeeds.
         */
        appointment.setAppointmentNumber(
                generateAppointmentNumber()
        );

        return appointmentRepository.save(
                appointment
        );
    }

    /*
     * =========================================================
     * TEMPORARY BACKWARD-COMPATIBLE UPDATE
     * =========================================================
     */

    /**
     * Maintained temporarily for older internal callers.
     *
     * The actual web/API workflow uses the overload that
     * supplies changedBy explicitly.
     */
    @Deprecated
    public boolean updateAppointment(
            Appointment appointment)
            throws SQLException {

        if (appointment == null) {

            throw new IllegalArgumentException(
                    "Appointment information is required."
            );
        }

        if (appointment.getAppointmentId() <= 0) {

            throw new IllegalArgumentException(
                    "Valid appointment ID is required."
            );
        }

        Appointment existingAppointment =
                appointmentRepository
                        .findById(
                                appointment.getAppointmentId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Appointment not found."
                                )
                        );

        return updateAppointment(
                appointment,
                existingAppointment.getCreatedBy()
        );
    }

    /*
     * =========================================================
     * UPDATE / RESCHEDULE / CANCEL
     * =========================================================
     */

    public boolean updateAppointment(
            Appointment appointment,
            int changedBy)
            throws SQLException {

        if (appointment == null) {

            throw new IllegalArgumentException(
                    "Appointment information is required."
            );
        }

        if (appointment.getAppointmentId() <= 0) {

            throw new IllegalArgumentException(
                    "Valid appointment ID is required."
            );
        }

        if (changedBy <= 0) {

            throw new IllegalArgumentException(
                    "Valid user ID is required for appointment changes."
            );
        }

        /*
         * Load the existing persistent state first.
         */
        Appointment existingAppointment =
                appointmentRepository
                        .findById(
                                appointment.getAppointmentId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Appointment not found."
                                )
                        );

        /*
         * =====================================================
         * MEMENTO PATTERN
         * =====================================================
         *
         * Capture the immutable OLD state before any
         * appointment information is modified.
         */
        AppointmentMemento previousState =
                new AppointmentMemento(
                        existingAppointment
                );

        /*
         * Appointment number is immutable.
         */
        appointment.setAppointmentNumber(
                existingAppointment
                        .getAppointmentNumber()
        );

        /*
         * Preserve original appointment creator.
         *
         * changedBy represents the current user performing
         * this particular modification.
         */
        appointment.setCreatedBy(
                existingAppointment
                        .getCreatedBy()
        );

        /*
         * Preserve existing status if PUT did not supply one.
         */
        if (appointment.getStatus() == null) {

            appointment.setStatus(
                    existingAppointment
                            .getStatus()
            );
        }

        /*
         * Validate appointment lifecycle.
         */
        validateStatusTransition(
                existingAppointment,
                appointment
        );

        /*
         * Validate service-level fields.
         */
        validateCommonAppointmentData(
                appointment
        );

        boolean schedulingDetailsChanged =
                hasSchedulingDetailsChanged(
                        existingAppointment,
                        appointment
                );

        boolean cancelling =
                existingAppointment.getStatus()
                        != AppointmentStatus.CANCELLED
                && appointment.getStatus()
                        == AppointmentStatus.CANCELLED;

        /*
         * Cancellation preserves the original schedule.
         *
         * A request cannot move an appointment and cancel
         * it simultaneously.
         */
        if (cancelling
                && schedulingDetailsChanged) {

            throw new IllegalArgumentException(
                    "Appointment schedule details cannot be changed "
                    + "while cancelling the appointment."
            );
        }

        /*
         * =====================================================
         * CHAIN OF RESPONSIBILITY DURING RESCHEDULING
         * =====================================================
         *
         * If any scheduling field changes, the entire
         * validation chain runs again.
         */
        if (schedulingDetailsChanged
                && appointment.getStatus()
                        != AppointmentStatus.CANCELLED) {

            runSchedulingValidationChain(
                    appointment,
                    appointment.getAppointmentId()
            );

        } else if (!cancelling
                && appointment.getStatus()
                        != AppointmentStatus.CANCELLED) {

            /*
             * If scheduling information did not change,
             * there is no reason to perform another conflict
             * query.
             *
             * We only verify that referenced records
             * still exist.
             *
             * This allows legitimate status changes such as
             * SCHEDULED → COMPLETED even if a dentist or
             * treatment is later deactivated.
             */
            validateReferencedEntitiesExist(
                    appointment
            );
        }

        /*
         * Avoid meaningless history records when nothing
         * actually changed.
         */
        boolean appointmentChanged =
                hasAppointmentChanged(
                        existingAppointment,
                        appointment
                );

        if (!appointmentChanged) {

            return true;
        }

        /*
         * Persist the NEW appointment state.
         */
        boolean updated =
                appointmentRepository.update(
                        appointment
                );

        if (!updated) {

            return false;
        }

        /*
         * Determine history event type.
         */
        String changeType =
                cancelling
                        ? "CANCEL"
                        : "UPDATE";

        /*
         * =====================================================
         * MEMENTO CARETAKER / HISTORY
         * =====================================================
         *
         * Store the OLD state after the new state has been
         * successfully persisted.
         */
        appointmentHistoryRepository.save(
                previousState,
                changedBy,
                changeType
        );

        return true;
    }

    /*
     * =========================================================
     * CHAIN OF RESPONSIBILITY CONFIGURATION
     * =========================================================
     */

    /**
     * Builds the validation pipeline.
     *
     * Each handler has exactly one primary responsibility.
     */
    private AppointmentValidationHandler
            buildSchedulingValidationChain() {

        AppointmentValidationHandler patientHandler =
                new PatientValidationHandler(
                        patientRepository
                );

        AppointmentValidationHandler dentistHandler =
                new DentistValidationHandler(
                        dentistRepository
                );

        AppointmentValidationHandler treatmentHandler =
                new TreatmentValidationHandler(
                        treatmentRepository
                );

        AppointmentValidationHandler dateTimeHandler =
                new DateTimeValidationHandler();

        AppointmentValidationHandler availabilityHandler =
                new DentistAvailabilityHandler(
                        appointmentRepository
                );

        /*
         * Construct the chain:
         *
         * Patient
         *   → Dentist
         *   → Treatment
         *   → Date/Time
         *   → Availability
         */
        patientHandler
                .setNext(
                        dentistHandler
                )
                .setNext(
                        treatmentHandler
                )
                .setNext(
                        dateTimeHandler
                )
                .setNext(
                        availabilityHandler
                );

        return patientHandler;
    }

    /*
     * =========================================================
     * EXECUTE VALIDATION CHAIN
     * =========================================================
     */

    private void runSchedulingValidationChain(
            Appointment appointment,
            Integer excludeAppointmentId)
            throws SQLException {

        AppointmentValidationContext context =
                new AppointmentValidationContext(
                        appointment,
                        excludeAppointmentId
                );

        schedulingValidationChain.handle(
                context
        );
    }

    /*
     * =========================================================
     * SERVICE-LEVEL VALIDATION
     * =========================================================
     */

    /**
     * Validates fields that are not responsibilities of the
     * scheduling Chain.
     *
     * Patient, dentist, treatment, date/time and conflict
     * validation are intentionally NOT duplicated here.
     */
    private void validateCommonAppointmentData(
            Appointment appointment) {

        if (appointment == null) {

            throw new IllegalArgumentException(
                    "Appointment information is required."
            );
        }

        /*
         * createdBy identifies the original user who
         * registered the appointment.
         */
        if (appointment.getCreatedBy() <= 0) {

            throw new IllegalArgumentException(
                    "Valid creator user ID is required."
            );
        }

        if (appointment.getNotes() != null
                && appointment
                        .getNotes()
                        .length() > 1000) {

            throw new IllegalArgumentException(
                    "Appointment notes cannot exceed 1000 characters."
            );
        }
    }

    /*
     * =========================================================
     * NON-SCHEDULING REFERENCE VALIDATION
     * =========================================================
     */

    /**
     * Used when schedule information has not changed.
     *
     * We verify existence only rather than requiring
     * dentist/treatment active status.
     *
     * Historical appointments may legitimately refer to
     * entities that were later deactivated.
     */
    private void validateReferencedEntitiesExist(
            Appointment appointment)
            throws SQLException {

        if (patientRepository
                .findById(
                        appointment.getPatientId()
                )
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Selected patient does not exist."
            );
        }

        if (dentistRepository
                .findById(
                        appointment.getDentistId()
                )
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Selected dentist does not exist."
            );
        }

        if (treatmentRepository
                .findById(
                        appointment.getTreatmentId()
                )
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Selected treatment does not exist."
            );
        }
    }

    /*
     * =========================================================
     * STATUS LIFECYCLE VALIDATION
     * =========================================================
     */

    private void validateStatusTransition(
            Appointment existingAppointment,
            Appointment proposedAppointment) {

        AppointmentStatus existingStatus =
                existingAppointment.getStatus();

        AppointmentStatus proposedStatus =
                proposedAppointment.getStatus();

        if (existingStatus == null
                || proposedStatus == null) {

            return;
        }

        /*
         * Cancelled appointments remain historical records.
         * They cannot simply be reopened.
         */
        if (existingStatus
                == AppointmentStatus.CANCELLED
                && proposedStatus
                        != AppointmentStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "A cancelled appointment cannot be reopened."
            );
        }

        /*
         * Completed appointments are also final clinical
         * records and cannot return to SCHEDULED/CANCELLED.
         */
        if (existingStatus
                == AppointmentStatus.COMPLETED
                && proposedStatus
                        != AppointmentStatus.COMPLETED) {

            throw new IllegalArgumentException(
                    "A completed appointment cannot be returned "
                    + "to another status."
            );
        }
    }

    /*
     * =========================================================
     * CHANGE DETECTION
     * =========================================================
     */

    private boolean hasSchedulingDetailsChanged(
            Appointment existingAppointment,
            Appointment proposedAppointment) {

        return existingAppointment.getPatientId()
                != proposedAppointment.getPatientId()

                || existingAppointment.getDentistId()
                != proposedAppointment.getDentistId()

                || existingAppointment.getTreatmentId()
                != proposedAppointment.getTreatmentId()

                || !Objects.equals(
                        existingAppointment.getAppointmentDate(),
                        proposedAppointment.getAppointmentDate()
                )

                || !Objects.equals(
                        existingAppointment.getAppointmentTime(),
                        proposedAppointment.getAppointmentTime()
                );
    }

    private boolean hasAppointmentChanged(
            Appointment existingAppointment,
            Appointment proposedAppointment) {

        return hasSchedulingDetailsChanged(
                existingAppointment,
                proposedAppointment
        )

                || existingAppointment.getStatus()
                != proposedAppointment.getStatus()

                || !Objects.equals(
                        existingAppointment.getNotes(),
                        proposedAppointment.getNotes()
                );
    }

    /*
     * =========================================================
     * APPOINTMENT NUMBER GENERATION
     * =========================================================
     */

    private String generateAppointmentNumber()
            throws SQLException {

        String appointmentNumber;

        do {

            String uniquePart =
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 8)
                            .toUpperCase();

            appointmentNumber =
                    "APT-" + uniquePart;

        } while (
                appointmentRepository
                        .findByNumber(
                                appointmentNumber
                        )
                        .isPresent()
        );

        return appointmentNumber;
    }

    /*
     * =========================================================
     * GENERAL HELPERS
     * =========================================================
     */

    private boolean isBlank(
            String value) {

        return value == null
                || value
                        .trim()
                        .isEmpty();
    }
}