package com.ryan_dev.core_banking_service.adapters.in.scheduler.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryan_dev.core_banking_service.adapters.in.scheduler.OutboxScheduler;
import com.ryan_dev.core_banking_service.adapters.out.message.dto.TransferEvent;
import com.ryan_dev.core_banking_service.application.domain.OutboxEvent;
import com.ryan_dev.core_banking_service.application.ports.out.OutboxRepositoryPort;
import com.ryan_dev.core_banking_service.application.ports.out.SendNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OutboxSchedulerConcurrencyIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @MockitoBean
    private SendNotificationPort sendNotificationPort;

    @Autowired
    private OutboxScheduler outboxScheduler;

    @Autowired
    private OutboxRepositoryPort outboxRepositoryPort;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws Exception {
        for (OutboxEvent event : outboxRepositoryPort.findPendingEventsBatch(1000)) {
            outboxRepositoryPort.delete(event.getId());
        }
    }

    @Test
    @DisplayName("Should process batches concurrently across multiple worker threads without duplication via SKIP LOCKED")
    void shouldProcessBatchesConcurrentlyWithoutDuplication() throws Exception {
        // Arrange
        TransferEvent dummyTransferEvent = new TransferEvent(UUID.randomUUID(), "payer@mail.com", "payee@mail.com", BigDecimal.TEN);
        String payload = objectMapper.writeValueAsString(dummyTransferEvent);

        for (int i = 0; i < 100; i++) {
            UUID id = UUID.randomUUID();
            OutboxEvent event = new OutboxEvent(id, "Transfer", id, "TransferCreated", payload, "PENDING", LocalDateTime.now());
            outboxRepositoryPort.save(event);
        }

        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch processingLatch = new CountDownLatch(2);
        AtomicInteger processedMessagesCount = new AtomicInteger(0);

        doAnswer(invocation -> {
            processingLatch.countDown();
            Thread.sleep(150);
            processedMessagesCount.incrementAndGet();
            return null;
        }).when(sendNotificationPort).send(any(TransferEvent.class));

        // Act
        CompletableFuture<Void> worker1 = CompletableFuture.runAsync(() -> {
            try {
                latch.await();
                outboxScheduler.processPendingEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        CompletableFuture<Void> worker2 = CompletableFuture.runAsync(() -> {
            try {
                latch.await();
                outboxScheduler.processPendingEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        latch.countDown();
        CompletableFuture.allOf(worker1, worker2).join();

        // Assert
        assertThat(processedMessagesCount.get()).isEqualTo(100);

        List<OutboxEvent> remaining = outboxRepositoryPort.findPendingEventsBatch(100);
        assertThat(remaining).isEmpty();
    }
}
