package com.sunrisedental.api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bill {

    private int billId;
    private String billNumber;
    private int appointmentId;

    private BigDecimal subtotal;
    private BigDecimal additionalCharges;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    private BillStatus paymentStatus;

    private int generatedBy;
    private LocalDateTime generatedAt;

    private final List<BillItem> items;

    public Bill() {
        this.subtotal = BigDecimal.ZERO;
        this.additionalCharges = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.paymentStatus = BillStatus.UNPAID;
        this.items = new ArrayList<>();
    }

    public Bill(
            int billId,
            String billNumber,
            int appointmentId,
            BigDecimal subtotal,
            BigDecimal additionalCharges,
            BigDecimal discountAmount,
            BigDecimal totalAmount,
            BillStatus paymentStatus,
            int generatedBy,
            LocalDateTime generatedAt) {

        this.billId = billId;
        this.billNumber = billNumber;
        this.appointmentId = appointmentId;
        this.subtotal = subtotal;
        this.additionalCharges = additionalCharges;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.generatedBy = generatedBy;
        this.generatedAt = generatedAt;
        this.items = new ArrayList<>();
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = safeAmount(subtotal);
    }

    public BigDecimal getAdditionalCharges() {
        return additionalCharges;
    }

    public void setAdditionalCharges(BigDecimal additionalCharges) {
        this.additionalCharges = safeAmount(additionalCharges);
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = safeAmount(discountAmount);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = safeAmount(totalAmount);
    }

    public BillStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(BillStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(int generatedBy) {
        this.generatedBy = generatedBy;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public void addItem(BillItem item) {

        if (item == null) {
            throw new IllegalArgumentException(
                    "Bill item cannot be null."
            );
        }

        items.add(item);
    }

    public List<BillItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void calculateTotals() {

        subtotal = items.stream()
                .map(BillItem::getTotalPrice)
                .filter(amount -> amount != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        totalAmount = subtotal
                .add(additionalCharges)
                .subtract(discountAmount);

        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null
                ? BigDecimal.ZERO
                : amount;
    }
}