package com.sunrisedental.api.model;

import java.math.BigDecimal;

public class BillItem {

    private int billItemId;
    private int billId;
    private String itemName;
    private BillItemType itemType;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public BillItem() {
        this.quantity = 1;
        this.unitPrice = BigDecimal.ZERO;
        this.totalPrice = BigDecimal.ZERO;
    }

    public BillItem(
            int billItemId,
            int billId,
            String itemName,
            BillItemType itemType,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice) {

        this.billItemId = billItemId;
        this.billId = billId;
        this.itemName = itemName;
        this.itemType = itemType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    public int getBillItemId() {
        return billItemId;
    }

    public void setBillItemId(int billItemId) {
        this.billItemId = billItemId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BillItemType getItemType() {
        return itemType;
    }

    public void setItemType(BillItemType itemType) {
        this.itemType = itemType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void calculateTotal() {

        if (unitPrice == null) {
            totalPrice = BigDecimal.ZERO;
            return;
        }

        totalPrice = unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }
}