package com.ryan_dev.core_banking_service.application.ports.in.transfer.commands;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferCommand(
        UUID id,
        UUID payerId,
        UUID payeeId,
        BigDecimal amount
) {
    public TransferCommand {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}