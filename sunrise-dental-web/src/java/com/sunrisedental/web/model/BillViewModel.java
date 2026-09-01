package com.sunrisedental.web.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public class BillViewModel {

    private int billId;

    private String billNumber;

    private int appointmentId;

    private BigDecimal subtotal;

    private BigDecimal additionalCharges;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private String paymentStatus;

    private int generatedBy;

    private LocalDateTime generatedAt;

    private List<BillItemViewModel> items;


    public BillViewModel() {
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
        this.subtotal = subtotal;
    }


    public BigDecimal getAdditionalCharges() {
        return additionalCharges;
    }


    public void setAdditionalCharges(
            BigDecimal additionalCharges) {

        this.additionalCharges =
                additionalCharges;
    }


    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }


    public void setDiscountAmount(
            BigDecimal discountAmount) {

        this.discountAmount =
                discountAmount;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }


    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount =
                totalAmount;
    }


    public String getPaymentStatus() {
        return paymentStatus;
    }


    public void setPaymentStatus(
            String paymentStatus) {

        this.paymentStatus =
                paymentStatus;
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


    public void setGeneratedAt(
            LocalDateTime generatedAt) {

        this.generatedAt =
                generatedAt;
    }


    public List<BillItemViewModel> getItems() {
        return items;
    }


    public void setItems(
            List<BillItemViewModel> items) {

        this.items = items;
    }


    // =========================================================
    // DISPLAY HELPERS
    // =========================================================

    public boolean isPaid() {

        return paymentStatus != null
                && paymentStatus.equalsIgnoreCase(
                        "PAID"
                );
    }


    public boolean isUnpaid() {

        return paymentStatus != null
                && paymentStatus.equalsIgnoreCase(
                        "UNPAID"
                );
    }


    // =========================================================
    // NESTED BILL ITEM VIEW MODEL
    // =========================================================

    public static class BillItemViewModel {

        private int billItemId;

        private String itemName;

        private String itemType;

        private int quantity;

        private BigDecimal unitPrice;

        private BigDecimal totalPrice;


        public BillItemViewModel() {
        }


        public int getBillItemId() {
            return billItemId;
        }


        public void setBillItemId(
                int billItemId) {

            this.billItemId =
                    billItemId;
        }


        public String getItemName() {
            return itemName;
        }


        public void setItemName(
                String itemName) {

            this.itemName =
                    itemName;
        }


        public String getItemType() {
            return itemType;
        }


        public void setItemType(
                String itemType) {

            this.itemType =
                    itemType;
        }


        public int getQuantity() {
            return quantity;
        }


        public void setQuantity(
                int quantity) {

            this.quantity =
                    quantity;
        }


        public BigDecimal getUnitPrice() {
            return unitPrice;
        }


        public void setUnitPrice(
                BigDecimal unitPrice) {

            this.unitPrice =
                    unitPrice;
        }


        public BigDecimal getTotalPrice() {
            return totalPrice;
        }


        public void setTotalPrice(
                BigDecimal totalPrice) {

            this.totalPrice =
                    totalPrice;
        }
    }
}