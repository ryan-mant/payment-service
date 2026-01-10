package com.ryan_dev.notification_service.adapters.out.persistence.adapter;

import com.ryan_dev.notification_service.adapters.out.persistence.entity.NotificationEntity;
import com.ryan_dev.notification_service.adapters.out.persistence.mapper.NotificationMapper;
import com.ryan_dev.notification_service.adapters.out.persistence.repository.SpringDataNotificationRepository;
import com.ryan_dev.notification_service.application.domain.Notification;
import com.ryan_dev.notification_service.application.ports.out.NotificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NotificationPersistenceAdapter implements NotificationRepositoryPort {

    private final NotificationMapper notificationMapper;
    private final SpringDataNotificationRepository springDataNotificationRepository;


    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = notificationMapper.toNotificationEntity(notification);
        NotificationEntity savedEntity = springDataNotificationRepository.save(entity);
        return notificationMapper.toNotification(savedEntity);
    }

    @Override
    public boolean existsByTransactionId(UUID transactionId) {
        return springDataNotificationRepository.existsByTransactionId(transactionId);
    }
}
