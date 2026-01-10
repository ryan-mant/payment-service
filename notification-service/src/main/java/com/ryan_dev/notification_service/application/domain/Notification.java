package com.ryan_dev.notification_service.application.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
    private UUID id;
    private UUID transactionId;
    private String email;
    private String message;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Notification() {
    }

    public Notification(UUID id, UUID transactionId, String email, String message, NotificationStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.transactionId = transactionId;
        this.email = email;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
