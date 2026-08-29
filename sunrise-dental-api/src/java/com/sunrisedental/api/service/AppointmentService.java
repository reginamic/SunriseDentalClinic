package com.sunrisedental.api.service;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.AppointmentDetails;
import com.sunrisedental.api.model.AppointmentStatus;

import com.sunrisedental.api.pattern.chainofresponsibility.AppointmentValidationContext;
import com.sunrisedental.api.pattern.chainofresponsibility.AppointmentValidationHandler;
import com.sunrisedental.api.pattern.chainofresponsibility.DateTimeValidationHandler;
import com.sunrisedental.api.pattern.chainofresponsibility.DentistAvailabilityHandler;
import com.sunrisedental.api.pattern.chainofresponsibility.DentistValidationHandler;
import com.sunrisedental.api.pattern.chainofresponsibility.PatientValidationHandler;
import com.sunrisedental.api.pattern.chainofresponsibility.TreatmentValidationHandler;

import com.sunrisedental.api.pattern.memento.AppointmentMemento;

import com.sunrisedental.api.repository.AppointmentDetailsRepository;
import com.sunrisedental.api.repository.AppointmentHistoryRepository;
import com.sunrisedental.api.repository.AppointmentRepository;
import com.sunrisedental.api.repository.DentistRepository;
import com.sunrisedental.api.repository.PatientRepository;
import com.sunrisedental.api.repository.TreatmentRepository;

import com.sunrisedental.api.repository.impl.JdbcAppointmentDetailsRepository;
import com.sunrisedental.api.repository.impl.JdbcAppointmentHistoryRepository;
import com.sunrisedental.api.repository.impl.JdbcAppointmentRepository;
import com.sunrisedental.api.repository.impl.JdbcDentistRepository;
import com.sunrisedental.api.repository.impl.JdbcPatientRepository;
import com.sunrisedental.api.repository.impl.JdbcTreatmentRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Business service for Appointment Management.
 *
 * Responsibilities:
 * - appointment creation
 * - appointment searching
 * - rescheduling
 * - cancellation/completion lifecycle
 * - Chain of Responsibility validation
 * - Memento history creation
 * - enriched appointment-detail retrieval
 */
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final PatientRepository patientRepository;

    private final DentistRepository dentistRepository;

    private final TreatmentRepository treatmentRepository;

    private final AppointmentHistoryRepository
            appointmentHistoryRepository;

    /*
     * Read-only repository backed by
     * vw_appointment_details.
     */
    private final AppointmentDetailsRepository
            appointmentDetailsRepository;

    /*
     * Root handler of the Appointment
     * Chain of Responsibility.
     */
    private final AppointmentValidationHandler
            schedulingValidationChain;


    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Used by the real application.
     */
    public AppointmentService() {

        this(
                new JdbcAppointmentRepository(),
                new JdbcPatientRepository(),
                new JdbcDentistRepository(),
                new JdbcTreatmentRepository(),
                new JdbcAppointmentHistoryRepository(),
                new JdbcAppointmentDetailsRepository()
        );
    }


    /**
     * Backward-compatible dependency-injection constructor.
     *
     * Existing tests/callers using four repositories
     * continue to compile.
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
                new JdbcAppointmentHistoryRepository(),
                new JdbcAppointmentDetailsRepository()
        );
    }


    /**
     * Backward-compatible five-repository constructor.
     *
     * Existing tests created for Memento/history
     * continue to compile.
     */
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DentistRepository dentistRepository,
            TreatmentRepository treatmentRepository,
            AppointmentHistoryRepository
                    appointmentHistoryRepository) {

        this(
                appointmentRepository,
                patientRepository,
                dentistRepository,
                treatmentRepository,
                appointmentHistoryRepository,
                new JdbcAppointmentDetailsRepository()
        );
    }


    /**
     * Full dependency-injection constructor.
     *
     * All repositories can be replaced with mocks/fakes
     * during automated unit testing.
     */
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DentistRepository dentistRepository,
            TreatmentRepository treatmentRepository,
            AppointmentHistoryRepository
                    appointmentHistoryRepository,
            AppointmentDetailsRepository
                    appointmentDetailsRepository) {

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

        this.appointmentDetailsRepository =
                Objects.requireNonNull(
                        appointmentDetailsRepository,
                        "AppointmentDetailsRepository cannot be null."
                );

        /*
         * Build Chain of Responsibility once.
         */
        this.schedulingValidationChain =
                buildSchedulingValidationChain();
    }


    // =========================================================
    // BASIC APPOINTMENT READ OPERATIONS
    // =========================================================

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
            LocalDate appointmentDate)
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


    // =========================================================
    // ENRICHED APPOINTMENT DETAIL READ OPERATIONS
    // =========================================================

    /**
     * Returns all appointments with complete patient,
     * dentist and treatment information.
     *
     * Data comes from vw_appointment_details.
     */
    public List<AppointmentDetails>
            getAllAppointmentDetails()
            throws SQLException {

        return appointmentDetailsRepository
                .findAll();
    }


    /**
     * Returns complete appointment information
     * using the internal database ID.
     */
    public Optional<AppointmentDetails>
            getAppointmentDetailsById(
                    int appointmentId)
            throws SQLException {

        if (appointmentId <= 0) {

            throw new IllegalArgumentException(
                    "Appointment ID must be greater than zero."
            );
        }

        return appointmentDetailsRepository
                .findById(
                        appointmentId
                );
    }


    /**
     * Returns complete appointment information
     * using the public appointment number.
     */
    public Optional<AppointmentDetails>
            getAppointmentDetailsByNumber(
                    String appointmentNumber)
            throws SQLException {

        if (isBlank(appointmentNumber)) {

            throw new IllegalArgumentException(
                    "Appointment number is required."
            );
        }

        return appointmentDetailsRepository
                .findByAppointmentNumber(
                        appointmentNumber
                                .trim()
                                .toUpperCase()
                );
    }


    /**
     * Returns complete appointment information
     * for a selected date.
     */
    public List<AppointmentDetails>
            getAppointmentDetailsByDate(
                    LocalDate appointmentDate)
            throws SQLException {

        if (appointmentDate == null) {

            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }

        return appointmentDetailsRepository
                .findByDate(
                        appointmentDate
                );
    }


    /**
     * Returns complete appointment information
     * for one patient.
     */
    public List<AppointmentDetails>
            getAppointmentDetailsByPatient(
                    int patientId)
            throws SQLException {

        if (patientId <= 0) {

            throw new IllegalArgumentException(
                    "Valid patient ID is required."
            );
        }

        return appointmentDetailsRepository
                .findByPatientId(
                        patientId
                );
    }


    // =========================================================
    // CREATE APPOINTMENT
    // =========================================================

    public Appointment createAppointment(
            Appointment appointment)
            throws SQLException {

        /*
         * Service-level validation.
         *
         * Scheduling-specific validation is delegated
         * to the Chain of Responsibility.
         */
        validateCommonAppointmentData(
                appointment
        );

        /*
         * New appointments default to SCHEDULED.
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
         * Generate number only after validation passes.
         */
        appointment.setAppointmentNumber(
                generateAppointmentNumber()
        );

        return appointmentRepository.save(
                appointment
        );
    }


    // =========================================================
    // BACKWARD-COMPATIBLE UPDATE
    // =========================================================

    /**
     * Temporary compatibility method for older callers.
     *
     * New Web/API operations should use the overload
     * supplying changedBy explicitly.
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
                                appointment
                                        .getAppointmentId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Appointment not found."
                                )
                        );

        return updateAppointment(
                appointment,
                existingAppointment
                        .getCreatedBy()
        );
    }


    // =========================================================
    // UPDATE / RESCHEDULE / CANCEL / COMPLETE
    // =========================================================

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
         * Load current persistent state.
         */
        Appointment existingAppointment =
                appointmentRepository
                        .findById(
                                appointment
                                        .getAppointmentId()
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
         * Capture OLD state before modification.
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
         * Preserve original creator.
         */
        appointment.setCreatedBy(
                existingAppointment
                        .getCreatedBy()
        );


        /*
         * Preserve status when omitted.
         */
        if (appointment.getStatus() == null) {

            appointment.setStatus(
                    existingAppointment
                            .getStatus()
            );
        }


        /*
         * Lifecycle validation.
         */
        validateStatusTransition(
                existingAppointment,
                appointment
        );


        /*
         * Service-level validation.
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
         * A cancellation must preserve the existing
         * appointment schedule.
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
             * No scheduling change:
             * avoid unnecessary overlap query.
             *
             * Only verify references still exist.
             */
            validateReferencedEntitiesExist(
                    appointment
            );
        }


        /*
         * Do not create meaningless Memento/history
         * records for an unchanged appointment.
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
         * Persist the NEW state.
         */
        boolean updated =
                appointmentRepository.update(
                        appointment
                );

        if (!updated) {

            return false;
        }


        String changeType =
                cancelling
                        ? "CANCEL"
                        : "UPDATE";


        /*
         * =====================================================
         * MEMENTO CARETAKER
         * =====================================================
         *
         * Store the OLD appointment snapshot.
         */
        appointmentHistoryRepository.save(
                previousState,
                changedBy,
                changeType
        );

        return true;
    }


    // =========================================================
    // CHAIN OF RESPONSIBILITY CONFIGURATION
    // =========================================================

    /**
     * Builds the scheduling validation pipeline.
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

        AppointmentValidationHandler
                availabilityHandler =
                new DentistAvailabilityHandler(
                        appointmentRepository
                );


        /*
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


    // =========================================================
    // EXECUTE VALIDATION CHAIN
    // =========================================================

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


    // =========================================================
    // SERVICE-LEVEL VALIDATION
    // =========================================================

    private void validateCommonAppointmentData(
            Appointment appointment) {

        if (appointment == null) {

            throw new IllegalArgumentException(
                    "Appointment information is required."
            );
        }

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


    // =========================================================
    // NON-SCHEDULING REFERENCE VALIDATION
    // =========================================================

    /**
     * Used for status/notes updates where scheduling
     * information itself has not changed.
     *
     * Active status is deliberately not required here,
     * because historical appointments may reference a
     * dentist/treatment that was later deactivated.
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


    // =========================================================
    // STATUS LIFECYCLE VALIDATION
    // =========================================================

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
         * Cancelled records remain final historical records.
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
         * Completed appointments also remain final.
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


    // =========================================================
    // CHANGE DETECTION
    // =========================================================

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
                        existingAppointment
                                .getAppointmentDate(),

                        proposedAppointment
                                .getAppointmentDate()
                )

                || !Objects.equals(
                        existingAppointment
                                .getAppointmentTime(),

                        proposedAppointment
                                .getAppointmentTime()
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


    // =========================================================
    // APPOINTMENT NUMBER GENERATION
    // =========================================================

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


    // =========================================================
    // GENERAL HELPERS
    // =========================================================

    private boolean isBlank(
            String value) {

        return value == null
                || value
                        .trim()
                        .isEmpty();
    }
}