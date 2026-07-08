package com.ryan_dev.core_banking_service.adapters.out.persistence.adapter;

import com.ryan_dev.core_banking_service.adapters.out.persistence.entity.OutboxEventEntity;
import com.ryan_dev.core_banking_service.adapters.out.persistence.mapper.OutboxEventMapper;
import com.ryan_dev.core_banking_service.adapters.out.persistence.repository.SpringDataOutboxRepository;
import com.ryan_dev.core_banking_service.application.domain.OutboxEvent;
import com.ryan_dev.core_banking_service.application.ports.out.OutboxRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OutboxPersistenceAdapter implements OutboxRepositoryPort {

    private final SpringDataOutboxRepository outboxRepository;
    private final OutboxEventMapper mapper;

    public OutboxPersistenceAdapter(SpringDataOutboxRepository outboxRepository, OutboxEventMapper mapper) {
        this.outboxRepository = outboxRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(OutboxEvent event) {
        OutboxEventEntity entity = mapper.toEntity(event);
        outboxRepository.save(entity);
    }

    @Override
    public List<OutboxEvent> findPendingEvents() {
        return outboxRepository.findByStatusOrderByCreatedAtAsc("PENDING").stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        outboxRepository.deleteById(id);
    }
}
