package com.ryan_dev.notification_service.adapters.in.message.dto;

import java.math.BigDecimal;
import java.util.UUID;


public record TransferEvent(UUID transactionId, String payerEmail, String payeeEmail, BigDecimal amount) {}