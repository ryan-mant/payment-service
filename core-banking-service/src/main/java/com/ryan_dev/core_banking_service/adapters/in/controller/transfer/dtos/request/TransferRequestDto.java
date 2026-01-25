package com.ryan_dev.core_banking_service.adapters.in.controller.transfer.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequestDto(
        @NotNull(message = "id cannot be null")
        UUID id,
        @NotNull(message = "payerId cannot be null")
        UUID payerId,
        @NotNull(message = "payeeId cannot be null")
        UUID payeeId,
        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.01", message = "Amount value cannot be lower than 0.0")
        BigDecimal amount
) {
}
