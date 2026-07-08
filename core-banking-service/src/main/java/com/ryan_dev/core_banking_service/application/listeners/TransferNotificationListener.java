package com.ryan_dev.core_banking_service.application.listeners;

import com.ryan_dev.core_banking_service.adapters.out.message.dto.TransferEvent;
import com.ryan_dev.core_banking_service.application.ports.out.OutboxRepositoryPort;
import com.ryan_dev.core_banking_service.application.ports.out.SendNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransferNotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(TransferNotificationListener.class);
    private final SendNotificationPort sendNotificationPort;
    private final OutboxRepositoryPort outboxRepositoryPort;
    
    public TransferNotificationListener(SendNotificationPort sendNotificationPort, OutboxRepositoryPort outboxRepositoryPort) {
        this.sendNotificationPort = sendNotificationPort;
        this.outboxRepositoryPort = outboxRepositoryPort;
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferSuccess(TransferEvent event) {
        try {
            sendNotificationPort.send(event);
            outboxRepositoryPort.delete(event.transactionId());
            logger.info("Transfer notification sent and outbox cleared for transaction ID: {}", event.transactionId());
        } catch (Exception e) {
            logger.error("Failed to send instant notification for transaction ID: {}. It will be handled by the outbox scheduler.", event.transactionId(), e);
        }
    }
}