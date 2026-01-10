package com.ryan_dev.core_banking_service.adapters.out.message.dto;

import java.math.BigDecimal;
import java.util.UUID;


public record TransferEvent(UUID transactionId, String payerEmail, String payeeEmail, BigDecimal amount) {}