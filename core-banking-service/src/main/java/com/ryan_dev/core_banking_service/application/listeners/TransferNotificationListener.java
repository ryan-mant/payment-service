package com.ryan_dev.core_banking_service.application.listeners;

import com.ryan_dev.core_banking_service.adapters.out.message.dto.TransferEvent;
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
    
    public TransferNotificationListener(SendNotificationPort sendNotificationPort) {
        this.sendNotificationPort = sendNotificationPort;
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferSuccess(TransferEvent event) {
        sendNotificationPort.send(event);
        logger.info("Transfer notification sent for transaction ID: {}", event.transactionId());
    }
}