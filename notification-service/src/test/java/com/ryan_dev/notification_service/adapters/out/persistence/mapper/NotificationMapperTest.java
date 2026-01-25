package com.ryan_dev.notification_service.adapters.out.persistence.mapper;

import com.ryan_dev.notification_service.adapters.out.persistence.entity.NotificationEntity;
import com.ryan_dev.notification_service.application.domain.Notification;
import com.ryan_dev.notification_service.application.domain.NotificationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotificationMapperTest {

    private final NotificationMapper notificationMapper = new NotificationMapper();

    @Test
    @DisplayName("Given a Notification domain object, When toNotificationEntity is called, Then should return correct NotificationEntity")
    void shouldMapToNotificationEntity() {
        // Given
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setTransactionId(UUID.randomUUID());
        notification.setEmail("test@test.com");
        notification.setMessage("Test message");
        notification.setStatus(NotificationStatus.SENT);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());

        // When
        NotificationEntity entity = notificationMapper.toNotificationEntity(notification);

        // Then
        assertNotNull(entity);
        assertEquals(notification.getId(), entity.getId());
        assertEquals(notification.getTransactionId(), entity.getTransactionId());
        assertEquals(notification.getEmail(), entity.getEmail());
        assertEquals(notification.getMessage(), entity.getMessage());
        assertEquals(notification.getStatus().name(), entity.getStatus().name());
        assertEquals(notification.getCreatedAt(), entity.getCreatedAt());
        assertEquals(notification.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Given a NotificationEntity, When toNotification is called, Then should return correct Notification domain object")
    void shouldMapToNotification() {
        // Given
        NotificationEntity entity = new NotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setTransactionId(UUID.randomUUID());
        entity.setEmail("test@test.com");
        entity.setMessage("Test message");
        entity.setStatus(NotificationStatus.SENT);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // When
        Notification notification = notificationMapper.toNotification(entity);

        // Then
        assertNotNull(notification);
        assertEquals(entity.getId(), notification.getId());
        assertEquals(entity.getTransactionId(), notification.getTransactionId());
        assertEquals(entity.getEmail(), notification.getEmail());
        assertEquals(entity.getMessage(), notification.getMessage());
        assertEquals(entity.getStatus().name(), notification.getStatus().name());
        assertEquals(entity.getCreatedAt(), notification.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), notification.getUpdatedAt());
    }
}
