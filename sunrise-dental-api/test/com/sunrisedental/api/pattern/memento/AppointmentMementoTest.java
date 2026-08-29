package com.sunrisedental.api.pattern.memento;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.AppointmentStatus;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class AppointmentMementoTest {

    @Test
    public void shouldCaptureCompleteAppointmentState() {

        Appointment appointment =
                new Appointment();

        appointment.setAppointmentId(10);
        appointment.setAppointmentNumber("APT-TEST-001");

        appointment.setPatientId(1);
        appointment.setDentistId(2);
        appointment.setTreatmentId(3);

        appointment.setAppointmentDate(
                LocalDate.of(
                        2030,
                        6,
                        15
                )
        );

        appointment.setAppointmentTime(
                LocalTime.of(
                        10,
                        30
                )
        );

        appointment.setStatus(
                AppointmentStatus.SCHEDULED
        );

        appointment.setNotes(
                "Original appointment state"
        );

        appointment.setCreatedBy(1);


        AppointmentMemento memento =
                new AppointmentMemento(
                        appointment
                );


        assertEquals(
                10,
                memento.getAppointmentId()
        );

        assertEquals(
                "APT-TEST-001",
                memento.getAppointmentNumber()
        );

        assertEquals(
                1,
                memento.getPatientId()
        );

        assertEquals(
                2,
                memento.getDentistId()
        );

        assertEquals(
                3,
                memento.getTreatmentId()
        );

        assertEquals(
                LocalDate.of(
                        2030,
                        6,
                        15
                ),
                memento.getAppointmentDate()
        );

        assertEquals(
                LocalTime.of(
                        10,
                        30
                ),
                memento.getAppointmentTime()
        );

        assertEquals(
                AppointmentStatus.SCHEDULED,
                memento.getStatus()
        );

        assertEquals(
                "Original appointment state",
                memento.getNotes()
        );
    }


    @Test
    public void shouldPreserveOldStateAfterAppointmentChanges() {

        Appointment appointment =
                new Appointment();

        appointment.setAppointmentId(20);
        appointment.setAppointmentNumber(
                "APT-TEST-002"
        );

        appointment.setPatientId(1);
        appointment.setDentistId(1);
        appointment.setTreatmentId(2);

        appointment.setAppointmentDate(
                LocalDate.of(
                        2030,
                        7,
                        10
                )
        );

        appointment.setAppointmentTime(
                LocalTime.of(
                        9,
                        0
                )
        );

        appointment.setStatus(
                AppointmentStatus.SCHEDULED
        );

        appointment.setNotes(
                "Before reschedule"
        );

        appointment.setCreatedBy(1);


        AppointmentMemento memento =
                new AppointmentMemento(
                        appointment
                );


        // Change the live appointment
        appointment.setDentistId(5);

        appointment.setTreatmentId(4);

        appointment.setAppointmentDate(
                LocalDate.of(
                        2030,
                        7,
                        12
                )
        );

        appointment.setAppointmentTime(
                LocalTime.of(
                        14,
                        30
                )
        );

        appointment.setNotes(
                "After reschedule"
        );


        // Memento must keep OLD values
        assertEquals(
                1,
                memento.getDentistId()
        );

        assertEquals(
                2,
                memento.getTreatmentId()
        );

        assertEquals(
                LocalDate.of(
                        2030,
                        7,
                        10
                ),
                memento.getAppointmentDate()
        );

        assertEquals(
                LocalTime.of(
                        9,
                        0
                ),
                memento.getAppointmentTime()
        );

        assertEquals(
                AppointmentStatus.SCHEDULED,
                memento.getStatus()
        );

        assertEquals(
                "Before reschedule",
                memento.getNotes()
        );


        // Prove live object changed
        assertEquals(
                5,
                appointment.getDentistId()
        );

        assertEquals(
                "After reschedule",
                appointment.getNotes()
        );
    }
}