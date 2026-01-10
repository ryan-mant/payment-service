package com.ryan_dev.notification_service.adapters.out.persistence.repository;

import com.ryan_dev.notification_service.adapters.out.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataNotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    boolean existsByTransactionId(UUID transactionId);
}