package com.ryan_dev.core_banking_service.application.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transfer {
    private UUID id;
    private Wallet payer;
    private Wallet payee;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public Transfer(UUID id, Wallet payer, Wallet payee, BigDecimal amount) {
        this.id = id;
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }

    public Transfer() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Wallet getPayer() {
        return payer;
    }

    public void setPayer(Wallet payer) {
        this.payer = payer;
    }

    public Wallet getPayee() {
        return payee;
    }

    public void setPayee(Wallet payee) {
        this.payee = payee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}