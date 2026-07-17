package com.ryan_dev.core_banking_service.adapters.out.persistence.repository;

import com.ryan_dev.core_banking_service.adapters.out.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataOutboxRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findByStatusOrderByCreatedAtAsc(String status);

    @Query(value = """
        SELECT * FROM outbox_event 
        WHERE status = :status 
        ORDER BY created_at ASC 
        LIMIT :limit 
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<OutboxEventEntity> findPendingEventsWithLockSkipLocked(@Param("status") String status, @Param("limit") int limit);
}
