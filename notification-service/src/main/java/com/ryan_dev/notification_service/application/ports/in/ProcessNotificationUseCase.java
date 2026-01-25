package com.ryan_dev.notification_service.application.ports.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProcessNotificationUseCase {
    void processNotification(UUID transactionId, String payerEmail, String payeeEmail, BigDecimal amount);
}
