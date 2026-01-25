package com.ryan_dev.core_banking_service.application.domain.exceptions;


public class InvalidTransactionAmountException extends BusinessException {
    public InvalidTransactionAmountException(String code, String message) {
        super(code, message);
    }
}
