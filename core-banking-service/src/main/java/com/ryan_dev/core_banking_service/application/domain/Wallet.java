package com.ryan_dev.core_banking_service.application.domain;

import com.ryan_dev.core_banking_service.application.domain.exceptions.InsufficientBalanceException;
import com.ryan_dev.core_banking_service.application.domain.exceptions.InvalidTransactionAmountException;

import java.math.BigDecimal;
import java.util.UUID;

public class Wallet {
    private UUID id;
    private String fullName;
    private String cpfCnpj;
    private String email;
    private String password;
    private BigDecimal balance = BigDecimal.ZERO;
    private Long version = 0L;

    public void debit(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("INSUFFICIENT_FUNDS", "Insufficient funds to complete the transaction.");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionAmountException("INVALID_AMOUNT", "The amount to be credited must be greater than zero.");
        }
        this.balance = this.balance.add(amount);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Wallet() {
    }

    public Wallet(UUID id, String fullName, String cpfCnpj, String email, String password, BigDecimal balance, Long version) {
        this.id = id;
        this.fullName = fullName;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.version = version;
    }
    public Wallet(UUID id, String fullName, String cpfCnpj, String email, String password, BigDecimal balance) {
        this.id = id;
        this.fullName = fullName;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.password = password;
        this.balance = balance;
    }
}
