package com.ryan_dev.core_banking_service.application.ports.out;

import com.ryan_dev.core_banking_service.application.domain.OutboxEvent;
import java.util.List;
import java.util.UUID;

public interface OutboxRepositoryPort {
    void save(OutboxEvent event);
    List<OutboxEvent> findPendingEvents();
    void delete(UUID id);
}
