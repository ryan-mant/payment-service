package com.ryan_dev.core_banking_service.adapters.in.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryan_dev.core_banking_service.adapters.out.message.dto.TransferEvent;
import com.ryan_dev.core_banking_service.application.domain.OutboxEvent;
import com.ryan_dev.core_banking_service.application.ports.out.OutboxRepositoryPort;
import com.ryan_dev.core_banking_service.application.ports.out.SendNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OutboxScheduler.class);
    private final OutboxRepositoryPort outboxRepositoryPort;
    private final SendNotificationPort sendNotificationPort;
    private final ObjectMapper objectMapper;

    public OutboxScheduler(OutboxRepositoryPort outboxRepositoryPort,
                           SendNotificationPort sendNotificationPort,
                           ObjectMapper objectMapper) {
        this.outboxRepositoryPort = outboxRepositoryPort;
        this.sendNotificationPort = sendNotificationPort;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${app.outbox.scheduler.delay:30000}")
    public void processPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxRepositoryPort.findPendingEvents();
        if (pendingEvents.isEmpty()) {
            return;
        }

        logger.info("Found {} pending outbox events to process.", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            try {
                // Deserializa o payload de volta para TransferEvent
                TransferEvent transferEvent = objectMapper.readValue(event.getPayload(), TransferEvent.class);
                
                // Envia para o RabbitMQ
                sendNotificationPort.send(transferEvent);
                
                // Se bem sucedido, deleta do outbox
                outboxRepositoryPort.delete(event.getId());
                logger.info("Outbox event successfully published and cleared for aggregate ID: {}", event.getAggregateId());
            } catch (Exception e) {
                logger.error("Failed to process outbox event with ID: {}. It will be retried in the next execution.", event.getId(), e);
                event.setRetryCount(event.getRetryCount() + 1);
                outboxRepositoryPort.save(event);
            }
        }
    }
}
