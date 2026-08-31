package com.sunrisedental.api.model;

import java.time.LocalDateTime;

public class AuditLog {

    private int auditId;

    /*
     * Nullable because some system-generated audit records
     * may not belong to an authenticated user.
     */
    private Integer userId;

    private String actionType;

    private String entityType;

    private Integer entityId;

    private String description;

    private String ipAddress;

    private LocalDateTime createdAt;

    public AuditLog() {
    }

    public AuditLog(
            int auditId,
            Integer userId,
            String actionType,
            String entityType,
            Integer entityId,
            String description,
            String ipAddress,
            LocalDateTime createdAt) {

        this.auditId =
                auditId;

        this.userId =
                userId;

        this.actionType =
                actionType;

        this.entityType =
                entityType;

        this.entityId =
                entityId;

        this.description =
                description;

        this.ipAddress =
                ipAddress;

        this.createdAt =
                createdAt;
    }

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(
            int auditId) {

        this.auditId =
                auditId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(
            Integer userId) {

        this.userId =
                userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(
            String actionType) {

        this.actionType =
                actionType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(
            String entityType) {

        this.entityType =
                entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(
            Integer entityId) {

        this.entityId =
                entityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description =
                description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(
            String ipAddress) {

        this.ipAddress =
                ipAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt =
                createdAt;
    }
}