package com.sunrisedental.api.service;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.AppointmentStatus;
import com.sunrisedental.api.model.Dentist;
import com.sunrisedental.api.model.Patient;
import com.sunrisedental.api.model.Treatment;

import com.sunrisedental.api.repository.AppointmentHistoryRepository;
import com.sunrisedental.api.repository.AppointmentRepository;
import com.sunrisedental.api.repository.DentistRepository;
import com.sunrisedental.api.repository.PatientRepository;
import com.sunrisedental.api.repository.TreatmentRepository;

import org.junit.Test;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.Assert.*;

public class AppointmentServiceValidationTest {


    // =========================================================
    // TEST 1
    // DOUBLE BOOKING MUST BE REJECTED
    // =========================================================

    @Test
    public void shouldRejectOverlappingDentistAppointment()
            throws Exception {

        AppointmentRepository appointmentRepository =
                createAppointmentRepository(
                        true
                );


        PatientRepository patientRepository =
                createPatientRepository();


        DentistRepository dentistRepository =
                createDentistRepository();


        TreatmentRepository treatmentRepository =
                createTreatmentRepository();


        AppointmentHistoryRepository historyRepository =
                createHistoryRepository();


        AppointmentService service =
                new AppointmentService(
                        appointmentRepository,
                        patientRepository,
                        dentistRepository,
                        treatmentRepository,
                        historyRepository
                );


        Appointment appointment =
                createValidAppointment();


        try {

            service.createAppointment(
                    appointment
            );


            fail(
                    "Expected overlapping appointment "
                    + "to be rejected."
            );


        } catch (IllegalArgumentException exception) {

            assertTrue(
                    exception
                            .getMessage()
                            .toLowerCase()
                            .contains(
                                    "overlap"
                            )
                    ||
                    exception
                            .getMessage()
                            .toLowerCase()
                            .contains(
                                    "book"
                            )
            );
        }
    }


    // =========================================================
    // TEST 2
    // AVAILABLE SLOT MUST BE ACCEPTED
    // =========================================================

    @Test
    public void shouldCreateAppointmentWhenDentistIsAvailable()
            throws Exception {

        AppointmentRepository appointmentRepository =
                createAppointmentRepository(
                        false
                );


        AppointmentService service =
                new AppointmentService(
                        appointmentRepository,
                        createPatientRepository(),
                        createDentistRepository(),
                        createTreatmentRepository(),
                        createHistoryRepository()
                );


        Appointment appointment =
                createValidAppointment();


        Appointment result =
                service.createAppointment(
                        appointment
                );


        assertNotNull(
                result
        );


        assertNotNull(
                result.getAppointmentNumber()
        );


        assertTrue(
                result
                        .getAppointmentNumber()
                        .startsWith(
                                "APT-"
                        )
        );


        assertEquals(
                AppointmentStatus.SCHEDULED,
                result.getStatus()
        );
    }


    // =========================================================
    // VALID TEST APPOINTMENT
    // =========================================================

    private Appointment createValidAppointment() {

        Appointment appointment =
                new Appointment();


        appointment.setPatientId(
                1
        );


        appointment.setDentistId(
                1
        );


        appointment.setTreatmentId(
                1
        );


        appointment.setAppointmentDate(
                LocalDate.now()
                        .plusDays(
                                30
                        )
        );


        appointment.setAppointmentTime(
                LocalTime.of(
                        10,
                        0
                )
        );


        appointment.setStatus(
                AppointmentStatus.SCHEDULED
        );


        appointment.setNotes(
                "Automated appointment validation test"
        );


        appointment.setCreatedBy(
                1
        );


        return appointment;
    }


    // =========================================================
    // FAKE APPOINTMENT REPOSITORY
    // =========================================================

    private AppointmentRepository createAppointmentRepository(
            boolean bookingConflict) {

        return (AppointmentRepository)
                Proxy.newProxyInstance(
                        AppointmentRepository.class
                                .getClassLoader(),

                        new Class<?>[]{
                            AppointmentRepository.class
                        },

                        (proxy, method, args) -> {

                            String methodName =
                                    method.getName();


                            if ("existsDentistBooking"
                                    .equals(
                                            methodName
                                    )) {

                                return bookingConflict;
                            }


                            if ("findByNumber"
                                    .equals(
                                            methodName
                                    )) {

                                return Optional.empty();
                            }


                            if ("findById"
                                    .equals(
                                            methodName
                                    )) {

                                return Optional.empty();
                            }


                            if ("save"
                                    .equals(
                                            methodName
                                    )) {

                                return args[0];
                            }


                            if ("update"
                                    .equals(
                                            methodName
                                    )) {

                                return true;
                            }


                            return defaultValue(
                                    method.getReturnType()
                            );
                        }
                );
    }


    // =========================================================
    // FAKE PATIENT REPOSITORY
    // =========================================================

    private PatientRepository createPatientRepository() {

        Patient patient =
                new Patient();


        return (PatientRepository)
                Proxy.newProxyInstance(
                        PatientRepository.class
                                .getClassLoader(),

                        new Class<?>[]{
                            PatientRepository.class
                        },

                        (proxy, method, args) -> {

                            if ("findById"
                                    .equals(
                                            method.getName()
                                    )) {

                                return Optional.of(
                                        patient
                                );
                            }


                            return defaultValue(
                                    method.getReturnType()
                            );
                        }
                );
    }


    // =========================================================
    // FAKE DENTIST REPOSITORY
    // =========================================================

    private DentistRepository createDentistRepository() {

        Dentist dentist =
                new Dentist();


        dentist.setDentistId(
                1
        );


        dentist.setActive(
                true
        );


        return (DentistRepository)
                Proxy.newProxyInstance(
                        DentistRepository.class
                                .getClassLoader(),

                        new Class<?>[]{
                            DentistRepository.class
                        },

                        (proxy, method, args) -> {

                            if ("findById"
                                    .equals(
                                            method.getName()
                                    )) {

                                return Optional.of(
                                        dentist
                                );
                            }


                            return defaultValue(
                                    method.getReturnType()
                            );
                        }
                );
    }


    // =========================================================
    // FAKE TREATMENT REPOSITORY
    // =========================================================

    private TreatmentRepository createTreatmentRepository() {

        Treatment treatment =
                new Treatment();


        treatment.setTreatmentId(
                1
        );


        treatment.setActive(
                true
        );


        /*
         * Duration is important because the production
         * scheduling logic performs duration-aware
         * overlap checking.
         */
        treatment.setEstimatedDurationMinutes(
                45
        );


        return (TreatmentRepository)
                Proxy.newProxyInstance(
                        TreatmentRepository.class
                                .getClassLoader(),

                        new Class<?>[]{
                            TreatmentRepository.class
                        },

                        (proxy, method, args) -> {

                            if ("findById"
                                    .equals(
                                            method.getName()
                                    )) {

                                return Optional.of(
                                        treatment
                                );
                            }


                            return defaultValue(
                                    method.getReturnType()
                            );
                        }
                );
    }


    // =========================================================
    // FAKE HISTORY REPOSITORY
    // =========================================================

    private AppointmentHistoryRepository createHistoryRepository() {

        return (AppointmentHistoryRepository)
                Proxy.newProxyInstance(
                        AppointmentHistoryRepository.class
                                .getClassLoader(),

                        new Class<?>[]{
                            AppointmentHistoryRepository.class
                        },

                        (proxy, method, args) ->
                                defaultValue(
                                        method.getReturnType()
                                )
                );
    }


    // =========================================================
    // DEFAULT VALUE HELPER
    // =========================================================

    private Object defaultValue(
            Class<?> returnType) {

        if (returnType == void.class) {
            return null;
        }


        if (!returnType.isPrimitive()) {
            return null;
        }


        if (returnType == boolean.class) {
            return false;
        }


        if (returnType == int.class) {
            return 0;
        }


        if (returnType == long.class) {
            return 0L;
        }


        if (returnType == double.class) {
            return 0.0;
        }


        if (returnType == float.class) {
            return 0.0f;
        }


        if (returnType == short.class) {
            return (short) 0;
        }


        if (returnType == byte.class) {
            return (byte) 0;
        }


        if (returnType == char.class) {
            return '\0';
        }


        return null;
    }
}