package com.sunrisedental.api.service;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.AppointmentDetails;
import com.sunrisedental.api.model.AppointmentStatus;
import com.sunrisedental.api.model.Bill;
import com.sunrisedental.api.model.BillDetails;
import com.sunrisedental.api.model.BillItem;
import com.sunrisedental.api.model.BillStatus;
import com.sunrisedental.api.model.Treatment;

import com.sunrisedental.api.pattern.composite.BillCharge;
import com.sunrisedental.api.pattern.composite.BillingComponent;
import com.sunrisedental.api.pattern.composite.BillingGroup;

import com.sunrisedental.api.pattern.decorator.AdditionalChargeDecorator;
import com.sunrisedental.api.pattern.decorator.DiscountDecorator;

import com.sunrisedental.api.pattern.factory.BillItemFactory;

import com.sunrisedental.api.repository.AppointmentDetailsRepository;
import com.sunrisedental.api.repository.AppointmentRepository;
import com.sunrisedental.api.repository.BillRepository;
import com.sunrisedental.api.repository.TreatmentRepository;

import com.sunrisedental.api.repository.impl.JdbcAppointmentDetailsRepository;
import com.sunrisedental.api.repository.impl.JdbcAppointmentRepository;
import com.sunrisedental.api.repository.impl.JdbcBillRepository;
import com.sunrisedental.api.repository.impl.JdbcTreatmentRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


public class BillingService {

    private static final BigDecimal ZERO =
            BigDecimal.ZERO;


    private final BillRepository billRepository;

    private final AppointmentRepository appointmentRepository;

    private final TreatmentRepository treatmentRepository;

    private final AppointmentDetailsRepository
            appointmentDetailsRepository;

    private final BillItemFactory billItemFactory;


    // =========================================================
    // REAL APPLICATION CONSTRUCTOR
    // =========================================================

    public BillingService() {

        this(
                new JdbcBillRepository(),
                new JdbcAppointmentRepository(),
                new JdbcTreatmentRepository(),
                new JdbcAppointmentDetailsRepository(),
                new BillItemFactory()
        );
    }


    // =========================================================
    // BACKWARD-COMPATIBLE TEST CONSTRUCTOR
    // =========================================================

    /*
     * Existing automated tests can continue using this
     * constructor without modification.
     */
    public BillingService(
            BillRepository billRepository,
            AppointmentRepository appointmentRepository,
            TreatmentRepository treatmentRepository,
            BillItemFactory billItemFactory) {

        this(
                billRepository,
                appointmentRepository,
                treatmentRepository,
                new JdbcAppointmentDetailsRepository(),
                billItemFactory
        );
    }


    // =========================================================
    // FULL DEPENDENCY-INJECTION CONSTRUCTOR
    // =========================================================

    public BillingService(
            BillRepository billRepository,
            AppointmentRepository appointmentRepository,
            TreatmentRepository treatmentRepository,
            AppointmentDetailsRepository appointmentDetailsRepository,
            BillItemFactory billItemFactory) {

        this.billRepository =
                Objects.requireNonNull(
                        billRepository,
                        "BillRepository cannot be null."
                );


        this.appointmentRepository =
                Objects.requireNonNull(
                        appointmentRepository,
                        "AppointmentRepository cannot be null."
                );


        this.treatmentRepository =
                Objects.requireNonNull(
                        treatmentRepository,
                        "TreatmentRepository cannot be null."
                );


        this.appointmentDetailsRepository =
                Objects.requireNonNull(
                        appointmentDetailsRepository,
                        "AppointmentDetailsRepository cannot be null."
                );


        this.billItemFactory =
                Objects.requireNonNull(
                        billItemFactory,
                        "BillItemFactory cannot be null."
                );
    }


    // =========================================================
    // GET ALL BILLS
    // =========================================================

    public List<Bill> getAllBills()
            throws SQLException {

        return billRepository.findAll();
    }


    // =========================================================
    // GET BILL BY ID
    // =========================================================

    public Optional<Bill> getBillById(
            int billId)
            throws SQLException {

        if (billId <= 0) {

            throw new IllegalArgumentException(
                    "Bill ID must be greater than zero."
            );
        }


        return billRepository.findById(
                billId
        );
    }


    // =========================================================
    // GET BILL BY NUMBER
    // =========================================================

    public Optional<Bill> getBillByNumber(
            String billNumber)
            throws SQLException {

        if (isBlank(billNumber)) {

            throw new IllegalArgumentException(
                    "Bill number is required."
            );
        }


        return billRepository.findByNumber(
                billNumber
                        .trim()
                        .toUpperCase()
        );
    }


    // =========================================================
    // GET BILL BY APPOINTMENT
    // =========================================================

    public Optional<Bill> getBillByAppointmentId(
            int appointmentId)
            throws SQLException {

        if (appointmentId <= 0) {

            throw new IllegalArgumentException(
                    "Appointment ID must be greater than zero."
            );
        }


        return billRepository.findByAppointmentId(
                appointmentId
        );
    }


    // =========================================================
    // GET ENRICHED BILL DETAILS BY ID
    // =========================================================

    public Optional<BillDetails> getBillDetailsById(
            int billId)
            throws SQLException {

        if (billId <= 0) {

            throw new IllegalArgumentException(
                    "Bill ID must be greater than zero."
            );
        }


        Optional<Bill> billOptional =
                billRepository.findById(
                        billId
                );


        if (billOptional.isEmpty()) {

            return Optional.empty();
        }


        return Optional.of(
                createBillDetails(
                        billOptional.get()
                )
        );
    }


    // =========================================================
    // GET ENRICHED BILL DETAILS BY BILL NUMBER
    // =========================================================

    public Optional<BillDetails> getBillDetailsByNumber(
            String billNumber)
            throws SQLException {

        if (isBlank(billNumber)) {

            throw new IllegalArgumentException(
                    "Bill number is required."
            );
        }


        Optional<Bill> billOptional =
                billRepository.findByNumber(
                        billNumber
                                .trim()
                                .toUpperCase()
                );


        if (billOptional.isEmpty()) {

            return Optional.empty();
        }


        return Optional.of(
                createBillDetails(
                        billOptional.get()
                )
        );
    }


    // =========================================================
    // GET ENRICHED BILL DETAILS BY APPOINTMENT
    // =========================================================

    public Optional<BillDetails> getBillDetailsByAppointmentId(
            int appointmentId)
            throws SQLException {

        if (appointmentId <= 0) {

            throw new IllegalArgumentException(
                    "Appointment ID must be greater than zero."
            );
        }


        Optional<Bill> billOptional =
                billRepository.findByAppointmentId(
                        appointmentId
                );


        if (billOptional.isEmpty()) {

            return Optional.empty();
        }


        return Optional.of(
                createBillDetails(
                        billOptional.get()
                )
        );
    }


    // =========================================================
    // GENERATE BILL
    // =========================================================

    public Bill generateBill(
            int appointmentId,
            int generatedBy,
            BigDecimal additionalCharge,
            BigDecimal discountAmount)
            throws SQLException {

        validateGenerationRequest(
                appointmentId,
                generatedBy,
                additionalCharge,
                discountAmount
        );


        BigDecimal safeAdditionalCharge =
                safeAmount(
                        additionalCharge
                );


        BigDecimal safeDiscount =
                safeAmount(
                        discountAmount
                );


        /*
         * BUSINESS RULE:
         * One appointment must have only one bill.
         */
        if (billRepository
                .findByAppointmentId(
                        appointmentId
                )
                .isPresent()) {

            throw new IllegalArgumentException(
                    "A bill has already been generated "
                    + "for this appointment."
            );
        }


        // =====================================================
        // LOAD APPOINTMENT
        // =====================================================

        Appointment appointment =
                appointmentRepository
                        .findById(
                                appointmentId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Appointment not found."
                                )
                        );


        /*
         * BUSINESS RULE:
         *
         * Final billing is allowed only after
         * the appointment has been completed.
         */
        if (appointment.getStatus()
                != AppointmentStatus.COMPLETED) {

            throw new IllegalArgumentException(
                    "Only completed appointments "
                    + "can be billed."
            );
        }


        // =====================================================
        // LOAD TREATMENT
        // =====================================================

        Treatment treatment =
                treatmentRepository
                        .findById(
                                appointment
                                        .getTreatmentId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Treatment for this appointment "
                                        + "was not found."
                                )
                        );


        // =====================================================
        // FACTORY PATTERN
        // =====================================================

        BillItem treatmentItem =
                billItemFactory
                        .createTreatmentCharge(
                                treatment
                                        .getTreatmentName(),
                                treatment
                                        .getTreatmentPrice()
                        );


        BillItem consultationItem =
                billItemFactory
                        .createConsultationCharge(
                                treatment
                                        .getConsultationFee()
                        );


        // =====================================================
        // COMPOSITE PATTERN
        // =====================================================

        BillingGroup baseCharges =
                new BillingGroup(
                        "Base Treatment Charges"
                );


        baseCharges.add(
                new BillCharge(
                        treatmentItem
                )
        );


        baseCharges.add(
                new BillCharge(
                        consultationItem
                )
        );


        BigDecimal subtotal =
                baseCharges.getAmount();


        BigDecimal amountBeforeDiscount =
                subtotal.add(
                        safeAdditionalCharge
                );


        /*
         * BUSINESS RULE:
         *
         * Discount must never create
         * a negative payable amount.
         */
        if (safeDiscount.compareTo(
                amountBeforeDiscount
        ) > 0) {

            throw new IllegalArgumentException(
                    "Discount amount cannot exceed "
                    + "the payable amount."
            );
        }


        BillingComponent finalCalculation =
                baseCharges;


        // =====================================================
        // DECORATOR — ADDITIONAL CHARGE
        // =====================================================

        if (safeAdditionalCharge.compareTo(
                ZERO
        ) > 0) {

            BillItem additionalItem =
                    billItemFactory
                            .createAdditionalCharge(
                                    "Additional Clinic Charge",
                                    1,
                                    safeAdditionalCharge
                            );


            finalCalculation =
                    new AdditionalChargeDecorator(
                            finalCalculation,
                            additionalItem
                                    .getItemName(),
                            additionalItem
                                    .getTotalPrice()
                    );
        }


        // =====================================================
        // DECORATOR — DISCOUNT
        // =====================================================

        if (safeDiscount.compareTo(
                ZERO
        ) > 0) {

            finalCalculation =
                    new DiscountDecorator(
                            finalCalculation,
                            "Bill Discount",
                            safeDiscount
                    );
        }


        // =====================================================
        // BUILD BILL
        // =====================================================

        Bill bill =
                new Bill();


        bill.setBillNumber(
                generateBillNumber()
        );


        bill.setAppointmentId(
                appointmentId
        );


        bill.addItem(
                treatmentItem
        );


        bill.addItem(
                consultationItem
        );


        bill.setSubtotal(
                subtotal
        );


        bill.setAdditionalCharges(
                safeAdditionalCharge
        );


        bill.setDiscountAmount(
                safeDiscount
        );


        bill.setTotalAmount(
                finalCalculation
                        .getAmount()
        );


        bill.setPaymentStatus(
                BillStatus.UNPAID
        );


        bill.setGeneratedBy(
                generatedBy
        );


        return billRepository.save(
                bill
        );
    }


    // =========================================================
    // UPDATE PAYMENT STATUS
    // =========================================================

    public boolean updatePaymentStatus(
            int billId,
            BillStatus paymentStatus)
            throws SQLException {

        if (billId <= 0) {

            throw new IllegalArgumentException(
                    "Valid bill ID is required."
            );
        }


        if (paymentStatus == null) {

            throw new IllegalArgumentException(
                    "Payment status is required."
            );
        }


        if (billRepository
                .findById(
                        billId
                )
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Bill not found."
            );
        }


        return billRepository
                .updatePaymentStatus(
                        billId,
                        paymentStatus
                );
    }


    // =========================================================
    // BUILD ENRICHED RECEIPT DTO
    // =========================================================

    private BillDetails createBillDetails(
            Bill bill)
            throws SQLException {

        AppointmentDetails appointmentDetails =
                appointmentDetailsRepository
                        .findById(
                                bill.getAppointmentId()
                        )
                        .orElseThrow(() ->
                                new SQLException(
                                        "Appointment details could not "
                                        + "be found for bill "
                                        + bill.getBillNumber()
                                        + "."
                                )
                        );


        BillDetails details =
                new BillDetails();


        // -----------------------------------------------------
        // Bill information
        // -----------------------------------------------------

        details.setBillId(
                bill.getBillId()
        );


        details.setBillNumber(
                bill.getBillNumber()
        );


        details.setSubtotal(
                bill.getSubtotal()
        );


        details.setAdditionalCharges(
                bill.getAdditionalCharges()
        );


        details.setDiscountAmount(
                bill.getDiscountAmount()
        );


        details.setTotalAmount(
                bill.getTotalAmount()
        );


        details.setPaymentStatus(
                bill.getPaymentStatus()
        );


        details.setGeneratedBy(
                bill.getGeneratedBy()
        );


        details.setGeneratedAt(
                bill.getGeneratedAt()
        );


        for (BillItem item :
                bill.getItems()) {

            details.addItem(
                    item
            );
        }


        // -----------------------------------------------------
        // Appointment information
        // -----------------------------------------------------

        details.setAppointmentId(
                appointmentDetails
                        .getAppointmentId()
        );


        details.setAppointmentNumber(
                appointmentDetails
                        .getAppointmentNumber()
        );


        details.setAppointmentDate(
                appointmentDetails
                        .getAppointmentDate()
        );


        details.setAppointmentTime(
                appointmentDetails
                        .getAppointmentTime()
        );


        details.setAppointmentStatus(
                appointmentDetails
                        .getStatus()
        );


        // -----------------------------------------------------
        // Patient information
        // -----------------------------------------------------

        details.setPatientCode(
                appointmentDetails
                        .getPatientCode()
        );


        details.setPatientName(
                appointmentDetails
                        .getPatientName()
        );


        details.setPatientAddress(
                appointmentDetails
                        .getPatientAddress()
        );


        details.setPatientContactNumber(
                appointmentDetails
                        .getPatientContactNumber()
        );


        details.setPatientEmail(
                appointmentDetails
                        .getPatientEmail()
        );


        // -----------------------------------------------------
        // Dentist information
        // -----------------------------------------------------

        details.setDentistCode(
                appointmentDetails
                        .getDentistCode()
        );


        details.setDentistName(
                appointmentDetails
                        .getDentistName()
        );


        details.setDentistSpecialization(
                appointmentDetails
                        .getDentistSpecialization()
        );


        // -----------------------------------------------------
        // Treatment information
        // -----------------------------------------------------

        details.setTreatmentCode(
                appointmentDetails
                        .getTreatmentCode()
        );


        details.setTreatmentName(
                appointmentDetails
                        .getTreatmentName()
        );


        return details;
    }


    // =========================================================
    // GENERATION VALIDATION
    // =========================================================

    private void validateGenerationRequest(
            int appointmentId,
            int generatedBy,
            BigDecimal additionalCharge,
            BigDecimal discountAmount) {

        if (appointmentId <= 0) {

            throw new IllegalArgumentException(
                    "Valid appointment ID is required."
            );
        }


        if (generatedBy <= 0) {

            throw new IllegalArgumentException(
                    "Valid generator user ID is required."
            );
        }


        if (additionalCharge != null
                && additionalCharge
                        .compareTo(
                                ZERO
                        ) < 0) {

            throw new IllegalArgumentException(
                    "Additional charge cannot be negative."
            );
        }


        if (discountAmount != null
                && discountAmount
                        .compareTo(
                                ZERO
                        ) < 0) {

            throw new IllegalArgumentException(
                    "Discount amount cannot be negative."
            );
        }
    }


    // =========================================================
    // SAFE MONEY VALUE
    // =========================================================

    private BigDecimal safeAmount(
            BigDecimal amount) {

        return amount == null
                ? ZERO
                : amount;
    }


    // =========================================================
    // UNIQUE BILL NUMBER
    // =========================================================

    private String generateBillNumber() {

        String uniquePart =
                UUID.randomUUID()
                        .toString()
                        .replace(
                                "-",
                                ""
                        )
                        .substring(
                                0,
                                8
                        )
                        .toUpperCase();


        return "BILL-"
                + uniquePart;
    }


    // =========================================================
    // STRING HELPER
    // =========================================================

    private boolean isBlank(
            String value) {

        return value == null
                || value
                        .trim()
                        .isEmpty();
    }
}