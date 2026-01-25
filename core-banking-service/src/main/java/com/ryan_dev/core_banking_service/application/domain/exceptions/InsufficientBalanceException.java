package com.ryan_dev.core_banking_service.application.domain.exceptions;


public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException(String code, String message) {
        super(code, message);
    }
}
