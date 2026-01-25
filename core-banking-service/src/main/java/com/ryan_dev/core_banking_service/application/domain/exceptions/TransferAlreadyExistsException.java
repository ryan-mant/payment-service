package com.ryan_dev.core_banking_service.application.domain.exceptions;

public class TransferAlreadyExistsException extends RuntimeException {

    private final String code;
    public TransferAlreadyExistsException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
