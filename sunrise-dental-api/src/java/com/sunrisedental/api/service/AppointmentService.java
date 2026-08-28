package com.sunrisedental.api.service;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.AppointmentStatus;
import com.sunrisedental.api.model.Dentist;
import com.sunrisedental.api.model.Treatment;

import com.sunrisedental.api.repository.AppointmentRepository;
import com.sunrisedental.api.repository.DentistRepository;
import com.sunrisedental.api.repository.PatientRepository;
import com.sunrisedental.api.repository.TreatmentRepository;

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

public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentRepository treatmentRepository;

    /*
     * Used by the real application.
     */
    public AppointmentService() {
        this(
                new JdbcAppointmentRepository(),
                new JdbcPatientRepository(),
                new JdbcDentistRepository(),
                new JdbcTreatmentRepository()
        );
    }

    /*
     * Dependency-injection constructor.
     * This will allow repository mocks during unit testing.
     */
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DentistRepository dentistRepository,
            TreatmentRepository treatmentRepository) {

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
    }

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
                appointmentNumber.trim().toUpperCase()
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

    public Appointment createAppointment(
            Appointment appointment)
            throws SQLException {

        validateAppointment(appointment);

        validateRelatedEntities(appointment);

        ensureDentistAvailable(
                appointment,
                null
        );

        appointment.setAppointmentNumber(
                generateAppointmentNumber()
        );

        if (appointment.getStatus() == null) {
            appointment.setStatus(
                    AppointmentStatus.SCHEDULED
            );
        }

        return appointmentRepository.save(
                appointment
        );
    }

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

        /*
         * These values should not change during a normal update.
         */
        appointment.setAppointmentNumber(
                existingAppointment.getAppointmentNumber()
        );

        appointment.setCreatedBy(
                existingAppointment.getCreatedBy()
        );

        if (appointment.getStatus() == null) {
            appointment.setStatus(
                    existingAppointment.getStatus()
            );
        }

        validateAppointment(appointment);

        validateRelatedEntities(appointment);

        ensureDentistAvailable(
                appointment,
                appointment.getAppointmentId()
        );

        return appointmentRepository.update(
                appointment
        );
    }

    private void validateAppointment(
            Appointment appointment) {

        if (appointment == null) {
            throw new IllegalArgumentException(
                    "Appointment information is required."
            );
        }

        if (appointment.getPatientId() <= 0) {
            throw new IllegalArgumentException(
                    "Valid patient ID is required."
            );
        }

        if (appointment.getDentistId() <= 0) {
            throw new IllegalArgumentException(
                    "Valid dentist ID is required."
            );
        }

        if (appointment.getTreatmentId() <= 0) {
            throw new IllegalArgumentException(
                    "Valid treatment ID is required."
            );
        }

        if (appointment.getAppointmentDate() == null) {
            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }

        if (appointment.getAppointmentDate()
                .isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Appointment date cannot be in the past."
            );
        }

        if (appointment.getAppointmentTime() == null) {
            throw new IllegalArgumentException(
                    "Appointment time is required."
            );
        }

        if (appointment.getCreatedBy() <= 0) {
            throw new IllegalArgumentException(
                    "Valid creator user ID is required."
            );
        }

        if (appointment.getNotes() != null
                && appointment.getNotes().length() > 1000) {

            throw new IllegalArgumentException(
                    "Appointment notes cannot exceed 1000 characters."
            );
        }
    }

    private void validateRelatedEntities(
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

        Dentist dentist =
                dentistRepository
                        .findById(
                                appointment.getDentistId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Selected dentist does not exist."
                                )
                        );

        if (!dentist.isActive()) {
            throw new IllegalArgumentException(
                    "Selected dentist is inactive."
            );
        }

        Treatment treatment =
                treatmentRepository
                        .findById(
                                appointment.getTreatmentId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Selected treatment does not exist."
                                )
                        );

        if (!treatment.isActive()) {
            throw new IllegalArgumentException(
                    "Selected treatment is inactive."
            );
        }
    }

    private void ensureDentistAvailable(
            Appointment appointment,
            Integer excludeAppointmentId)
            throws SQLException {

        boolean alreadyBooked =
                appointmentRepository
                        .existsDentistBooking(
                                appointment.getDentistId(),
                                appointment.getAppointmentDate(),
                                appointment.getAppointmentTime(),
                                excludeAppointmentId
                        );

        if (alreadyBooked) {
            throw new IllegalArgumentException(
                    "The selected dentist is already booked "
                    + "for this date and time."
            );
        }
    }

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

        } while (appointmentRepository
                .findByNumber(appointmentNumber)
                .isPresent());

        return appointmentNumber;
    }

    private boolean isBlank(String value) {
        return value == null
                || value.trim().isEmpty();
    }
}