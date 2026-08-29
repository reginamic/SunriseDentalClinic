package com.sunrisedental.api.service;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.AppointmentStatus;
import com.sunrisedental.api.model.Bill;
import com.sunrisedental.api.model.BillStatus;
import com.sunrisedental.api.model.Treatment;

import com.sunrisedental.api.pattern.factory.BillItemFactory;

import com.sunrisedental.api.repository.AppointmentRepository;
import com.sunrisedental.api.repository.BillRepository;
import com.sunrisedental.api.repository.TreatmentRepository;

import org.junit.Test;

import java.lang.reflect.Proxy;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;


public class BillingServiceTest {

    // =========================================================
    // TEST 1
    // SCHEDULED APPOINTMENT MUST NOT BE BILLED
    // =========================================================

    @Test
    public void shouldRejectBillingForScheduledAppointment()
            throws Exception {

        Appointment appointment =
                createAppointment(
                        AppointmentStatus.SCHEDULED
                );


        BillingService service =
                createService(
                        appointment,
                        createTreatment(),
                        Optional.empty()
                );


        try {

            service.generateBill(
                    6,
                    1,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );


            fail(
                    "Expected scheduled appointment "
                    + "billing to be rejected."
            );


        } catch (IllegalArgumentException exception) {

            assertEquals(
                    "Only completed appointments can be billed.",
                    exception.getMessage()
            );
        }
    }


    // =========================================================
    // TEST 2
    // COMPLETED APPOINTMENT MUST CALCULATE CORRECT TOTAL
    // =========================================================

    @Test
    public void shouldGenerateCorrectBillForCompletedAppointment()
            throws Exception {

        Appointment appointment =
                createAppointment(
                        AppointmentStatus.COMPLETED
                );


        Treatment treatment =
                createTreatment();


        BillingService service =
                createService(
                        appointment,
                        treatment,
                        Optional.empty()
                );


        Bill bill =
                service.generateBill(
                        6,
                        1,
                        new BigDecimal(
                                "2000.00"
                        ),
                        new BigDecimal(
                                "500.00"
                        )
                );


        assertNotNull(
                bill
        );


        assertTrue(
                bill.getBillId() > 0
        );


        assertNotNull(
                bill.getBillNumber()
        );


        assertTrue(
                bill.getBillNumber()
                        .startsWith(
                                "BILL-"
                        )
        );


        assertEquals(
                0,
                bill.getSubtotal()
                        .compareTo(
                                new BigDecimal(
                                        "6500.00"
                                )
                        )
        );


        assertEquals(
                0,
                bill.getAdditionalCharges()
                        .compareTo(
                                new BigDecimal(
                                        "2000.00"
                                )
                        )
        );


        assertEquals(
                0,
                bill.getDiscountAmount()
                        .compareTo(
                                new BigDecimal(
                                        "500.00"
                                )
                        )
        );


        assertEquals(
                0,
                bill.getTotalAmount()
                        .compareTo(
                                new BigDecimal(
                                        "8000.00"
                                )
                        )
        );


        assertEquals(
                BillStatus.UNPAID,
                bill.getPaymentStatus()
        );


        /*
         * Treatment + consultation are persisted
         * as the two core bill items.
         */
        assertEquals(
                2,
                bill.getItems()
                        .size()
        );


        assertEquals(
                "Tooth Filling",
                bill.getItems()
                        .get(0)
                        .getItemName()
        );


        assertEquals(
                "Consultation Fee",
                bill.getItems()
                        .get(1)
                        .getItemName()
        );
    }


    // =========================================================
    // TEST 3
    // DISCOUNT MUST NOT EXCEED PAYABLE AMOUNT
    // =========================================================

    @Test
    public void shouldRejectDiscountGreaterThanPayableAmount()
            throws Exception {

        Appointment appointment =
                createAppointment(
                        AppointmentStatus.COMPLETED
                );


        BillingService service =
                createService(
                        appointment,
                        createTreatment(),
                        Optional.empty()
                );


        try {

            /*
             * Base amount:
             *
             * Treatment     = 5000
             * Consultation  = 1500
             * Total before discount = 6500
             *
             * Discount 7000 must therefore fail.
             */
            service.generateBill(
                    6,
                    1,
                    BigDecimal.ZERO,
                    new BigDecimal(
                            "7000.00"
                    )
            );


            fail(
                    "Expected excessive discount "
                    + "to be rejected."
            );


        } catch (IllegalArgumentException exception) {

            assertEquals(
                    "Discount amount cannot exceed "
                    + "the payable amount.",
                    exception.getMessage()
            );
        }
    }


    // =========================================================
    // TEST 4
    // DUPLICATE BILL MUST NOT BE GENERATED
    // =========================================================

    @Test
    public void shouldRejectDuplicateBillForAppointment()
            throws Exception {

        Bill existingBill =
                new Bill();


        existingBill.setBillId(
                3
        );


        existingBill.setBillNumber(
                "BILL-EXISTING"
        );


        existingBill.setAppointmentId(
                6
        );


        BillingService service =
                createService(
                        createAppointment(
                                AppointmentStatus.COMPLETED
                        ),
                        createTreatment(),
                        Optional.of(
                                existingBill
                        )
                );


        try {

            service.generateBill(
                    6,
                    1,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );


            fail(
                    "Expected duplicate billing "
                    + "to be rejected."
            );


        } catch (IllegalArgumentException exception) {

            assertEquals(
                    "A bill has already been generated "
                    + "for this appointment.",
                    exception.getMessage()
            );
        }
    }


    // =========================================================
    // TEST SERVICE
    // =========================================================

    private BillingService createService(
            Appointment appointment,
            Treatment treatment,
            Optional<Bill> existingBill) {

        BillRepository billRepository =
                createBillRepository(
                        existingBill
                );


        AppointmentRepository appointmentRepository =
                createAppointmentRepository(
                        appointment
                );


        TreatmentRepository treatmentRepository =
                createTreatmentRepository(
                        treatment
                );


        return new BillingService(
                billRepository,
                appointmentRepository,
                treatmentRepository,
                new BillItemFactory()
        );
    }


    // =========================================================
    // BILL REPOSITORY TEST DOUBLE
    // =========================================================

    private BillRepository createBillRepository(
            Optional<Bill> existingBill) {

        return (BillRepository)
                Proxy.newProxyInstance(
                        BillRepository.class
                                .getClassLoader(),

                        new Class<?>[]{
                            BillRepository.class
                        },

                        (proxy, method, arguments) -> {

                            String methodName =
                                    method.getName();


                            if ("findByAppointmentId"
                                    .equals(
                                            methodName
                                    )) {

                                return existingBill;
                            }


                            if ("findById"
                                    .equals(
                                            methodName
                                    )
                                    || "findByNumber"
                                            .equals(
                                                    methodName
                                            )) {

                                return Optional.empty();
                            }


                            if ("findAll"
                                    .equals(
                                            methodName
                                    )) {

                                return Collections.emptyList();
                            }


                            if ("save"
                                    .equals(
                                            methodName
                                    )) {

                                Bill bill =
                                        (Bill)
                                                arguments[0];


                                /*
                                 * Simulate the database-generated
                                 * primary key.
                                 */
                                bill.setBillId(
                                        100
                                );


                                return bill;
                            }


                            if ("updatePaymentStatus"
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
    // APPOINTMENT REPOSITORY TEST DOUBLE
    // =========================================================

    private AppointmentRepository createAppointmentRepository(
            Appointment appointment) {

        return (AppointmentRepository)
                Proxy.newProxyInstance(
                        AppointmentRepository.class
                                .getClassLoader(),

                        new Class<?>[]{
                            AppointmentRepository.class
                        },

                        (proxy, method, arguments) -> {

                            if ("findById"
                                    .equals(
                                            method.getName()
                                    )) {

                                return Optional.of(
                                        appointment
                                );
                            }


                            return defaultValue(
                                    method.getReturnType()
                            );
                        }
                );
    }


    // =========================================================
    // TREATMENT REPOSITORY TEST DOUBLE
    // =========================================================

    private TreatmentRepository createTreatmentRepository(
            Treatment treatment) {

        return (TreatmentRepository)
                Proxy.newProxyInstance(
                        TreatmentRepository.class
                                .getClassLoader(),

                        new Class<?>[]{
                            TreatmentRepository.class
                        },

                        (proxy, method, arguments) -> {

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
    // TEST APPOINTMENT
    // =========================================================

    private Appointment createAppointment(
            AppointmentStatus status) {

        Appointment appointment =
                new Appointment();


        appointment.setAppointmentId(
                6
        );


        appointment.setAppointmentNumber(
                "APT-TEST-BILLING"
        );


        appointment.setPatientId(
                1
        );


        appointment.setDentistId(
                4
        );


        appointment.setTreatmentId(
                3
        );


        appointment.setStatus(
                status
        );


        appointment.setCreatedBy(
                1
        );


        return appointment;
    }


    // =========================================================
    // TEST TREATMENT
    // =========================================================

    private Treatment createTreatment() {

        Treatment treatment =
                new Treatment();


        treatment.setTreatmentId(
                3
        );


        treatment.setTreatmentCode(
                "TRT-003"
        );


        treatment.setTreatmentName(
                "Tooth Filling"
        );


        treatment.setTreatmentPrice(
                new BigDecimal(
                        "5000.00"
                )
        );


        treatment.setConsultationFee(
                new BigDecimal(
                        "1500.00"
                )
        );


        treatment.setEstimatedDurationMinutes(
                45
        );


        treatment.setActive(
                true
        );


        return treatment;
    }


    // =========================================================
    // GENERIC PROXY DEFAULT VALUE
    // =========================================================

    private Object defaultValue(
            Class<?> returnType) {

        if (returnType == void.class) {
            return null;
        }


        if (returnType == boolean.class) {
            return false;
        }


        if (returnType == byte.class) {
            return (byte) 0;
        }


        if (returnType == short.class) {
            return (short) 0;
        }


        if (returnType == int.class) {
            return 0;
        }


        if (returnType == long.class) {
            return 0L;
        }


        if (returnType == float.class) {
            return 0F;
        }


        if (returnType == double.class) {
            return 0D;
        }


        if (returnType == char.class) {
            return '\0';
        }


        if (Optional.class.isAssignableFrom(
                returnType
        )) {

            return Optional.empty();
        }


        if (List.class.isAssignableFrom(
                returnType
        )) {

            return Collections.emptyList();
        }


        return null;
    }
}