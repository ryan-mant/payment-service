package com.ryan_dev.notification_service.adapters.out.persistence.mapper;

import com.ryan_dev.notification_service.adapters.out.persistence.entity.NotificationEntity;
import com.ryan_dev.notification_service.application.domain.Notification;
import com.ryan_dev.notification_service.application.domain.NotificationStatus;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationEntity toNotificationEntity(Notification notification) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setId(notification.getId());
        notificationEntity.setTransactionId(notification.getTransactionId());
        notificationEntity.setEmail(notification.getEmail());
        notificationEntity.setMessage(notification.getMessage());

        notificationEntity.setStatus(NotificationStatus.valueOf(notification.getStatus().name()));

        notificationEntity.setCreatedAt(notification.getCreatedAt());
        notificationEntity.setUpdatedAt(notification.getUpdatedAt());
        return notificationEntity;
    }

    public Notification toNotification(NotificationEntity notificationEntity) {
        Notification notification = new Notification();
        notification.setId(notificationEntity.getId());
        notification.setTransactionId(notificationEntity.getTransactionId());
        notification.setEmail(notificationEntity.getEmail());
        notification.setMessage(notificationEntity.getMessage());

        notification.setStatus(NotificationStatus.valueOf(notificationEntity.getStatus().name()));

        notification.setCreatedAt(notificationEntity.getCreatedAt());
        notification.setUpdatedAt(notificationEntity.getUpdatedAt());
        return notification;
    }
}
