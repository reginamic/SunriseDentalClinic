package com.sunrisedental.api.service;

import com.sunrisedental.api.model.Appointment;
import com.sunrisedental.api.model.Bill;
import com.sunrisedental.api.model.BillItem;
import com.sunrisedental.api.model.BillStatus;
import com.sunrisedental.api.model.Treatment;

import com.sunrisedental.api.pattern.composite.BillCharge;
import com.sunrisedental.api.pattern.composite.BillingComponent;
import com.sunrisedental.api.pattern.composite.BillingGroup;

import com.sunrisedental.api.pattern.decorator.AdditionalChargeDecorator;
import com.sunrisedental.api.pattern.decorator.DiscountDecorator;

import com.sunrisedental.api.pattern.factory.BillItemFactory;

import com.sunrisedental.api.repository.AppointmentRepository;
import com.sunrisedental.api.repository.BillRepository;
import com.sunrisedental.api.repository.TreatmentRepository;

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
    private final BillItemFactory billItemFactory;

    /*
     * Used by the real application.
     */
    public BillingService() {
        this(
                new JdbcBillRepository(),
                new JdbcAppointmentRepository(),
                new JdbcTreatmentRepository(),
                new BillItemFactory()
        );
    }

    /*
     * Dependency-injection constructor.
     * Useful for JUnit / Mockito tests.
     */
    public BillingService(
            BillRepository billRepository,
            AppointmentRepository appointmentRepository,
            TreatmentRepository treatmentRepository,
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

        this.billItemFactory =
                Objects.requireNonNull(
                        billItemFactory,
                        "BillItemFactory cannot be null."
                );
    }

    public List<Bill> getAllBills()
            throws SQLException {

        return billRepository.findAll();
    }

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

    public Optional<Bill> getBillByNumber(
            String billNumber)
            throws SQLException {

        if (isBlank(billNumber)) {
            throw new IllegalArgumentException(
                    "Bill number is required."
            );
        }

        return billRepository.findByNumber(
                billNumber.trim().toUpperCase()
        );
    }

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
                safeAmount(additionalCharge);

        BigDecimal safeDiscount =
                safeAmount(discountAmount);

        /*
         * One appointment must have only one bill.
         */
        if (billRepository
                .findByAppointmentId(appointmentId)
                .isPresent()) {

            throw new IllegalArgumentException(
                    "A bill has already been generated "
                    + "for this appointment."
            );
        }

        Appointment appointment =
                appointmentRepository
                        .findById(appointmentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Appointment not found."
                                )
                        );

        Treatment treatment =
                treatmentRepository
                        .findById(
                                appointment.getTreatmentId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Treatment for this appointment "
                                        + "was not found."
                                )
                        );

        /*
         * FACTORY PATTERN
         *
         * Centralized creation of correctly configured
         * treatment and consultation BillItems.
         */
        BillItem treatmentItem =
                billItemFactory
                        .createTreatmentCharge(
                                treatment.getTreatmentName(),
                                treatment.getTreatmentPrice()
                        );

        BillItem consultationItem =
                billItemFactory
                        .createConsultationCharge(
                                treatment.getConsultationFee()
                        );

        /*
         * COMPOSITE PATTERN
         *
         * Individual charges become leaves inside
         * one billing group.
         */
        BillingGroup baseCharges =
                new BillingGroup(
                        "Base Treatment Charges"
                );

        baseCharges.add(
                new BillCharge(treatmentItem)
        );

        baseCharges.add(
                new BillCharge(consultationItem)
        );

        BigDecimal subtotal =
                baseCharges.getAmount();

        /*
         * Start with the Composite result.
         */
        BillingComponent finalCalculation =
                baseCharges;

        /*
         * DECORATOR PATTERN
         *
         * Dynamically add an optional additional charge.
         */
        if (safeAdditionalCharge.compareTo(ZERO) > 0) {

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
                            additionalItem.getItemName(),
                            additionalItem.getTotalPrice()
                    );
        }

        /*
         * DECORATOR PATTERN
         *
         * Dynamically apply an optional discount.
         */
        if (safeDiscount.compareTo(ZERO) > 0) {

            finalCalculation =
                    new DiscountDecorator(
                            finalCalculation,
                            "Bill Discount",
                            safeDiscount
                    );
        }

        Bill bill = new Bill();

        bill.setBillNumber(
                generateBillNumber()
        );

        bill.setAppointmentId(
                appointmentId
        );

        /*
         * These two items are persisted as the core
         * treatment and consultation breakdown.
         */
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
                finalCalculation.getAmount()
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
                .findById(billId)
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
                && additionalCharge.compareTo(ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Additional charge cannot be negative."
            );
        }

        if (discountAmount != null
                && discountAmount.compareTo(ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Discount amount cannot be negative."
            );
        }
    }

    private BigDecimal safeAmount(
            BigDecimal amount) {

        return amount == null
                ? ZERO
                : amount;
    }

    private String generateBillNumber() {

        String uniquePart =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 8)
                        .toUpperCase();

        return "BILL-" + uniquePart;
    }

    private boolean isBlank(
            String value) {

        return value == null
                || value.trim().isEmpty();
    }
}