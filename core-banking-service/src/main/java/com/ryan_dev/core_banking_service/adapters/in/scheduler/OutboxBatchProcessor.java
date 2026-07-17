package com.ryan_dev.core_banking_service.adapters.in.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryan_dev.core_banking_service.adapters.out.message.dto.TransferEvent;
import com.ryan_dev.core_banking_service.application.domain.OutboxEvent;
import com.ryan_dev.core_banking_service.application.ports.out.OutboxRepositoryPort;
import com.ryan_dev.core_banking_service.application.ports.out.SendNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxBatchProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OutboxBatchProcessor.class);
    private final OutboxRepositoryPort outboxRepositoryPort;
    private final SendNotificationPort sendNotificationPort;
    private final ObjectMapper objectMapper;

    public OutboxBatchProcessor(OutboxRepositoryPort outboxRepositoryPort,
                                SendNotificationPort sendNotificationPort,
                                ObjectMapper objectMapper) {
        this.outboxRepositoryPort = outboxRepositoryPort;
        this.sendNotificationPort = sendNotificationPort;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public boolean processNextBatch(int batchSize) {
        List<OutboxEvent> pendingEvents = outboxRepositoryPort.findPendingEventsBatch(batchSize);
        if (pendingEvents.isEmpty()) {
            return false;
        }

        logger.info("Processing outbox batch of {} events via SKIP LOCKED...", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            try {
                TransferEvent transferEvent = objectMapper.readValue(event.getPayload(), TransferEvent.class);
                sendNotificationPort.send(transferEvent);
                outboxRepositoryPort.delete(event.getId());
                logger.debug("Outbox event published and cleared for aggregate ID: {}", event.getAggregateId());
            } catch (Exception e) {
                logger.error("Failed to process outbox event with ID: {}. It will be retried later.", event.getId(), e);
                event.setRetryCount(event.getRetryCount() + 1);
                outboxRepositoryPort.save(event);
            }
        }

        return pendingEvents.size() == batchSize;
    }
}
