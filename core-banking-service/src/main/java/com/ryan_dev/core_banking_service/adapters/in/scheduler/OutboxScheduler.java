package com.ryan_dev.core_banking_service.adapters.in.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.outbox.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OutboxScheduler.class);
    private final OutboxBatchProcessor batchProcessor;

    public OutboxScheduler(OutboxBatchProcessor batchProcessor) {
        this.batchProcessor = batchProcessor;
    }

    @Scheduled(fixedDelayString = "${app.outbox.scheduler.delay:5000}")
    public void processPendingEvents() {
        int batchSize = 100;
        int maxBatchesPerRun = 50;
        int batchesProcessed = 0;

        while (batchesProcessed < maxBatchesPerRun) {
            boolean hasMore = batchProcessor.processNextBatch(batchSize);
            batchesProcessed++;
            if (!hasMore) {
                break;
            }
        }

        if (batchesProcessed > 1) {
            logger.info("Drained {} consecutive batches of outbox events without waiting for scheduled delay.", batchesProcessed);
        }
    }
}
