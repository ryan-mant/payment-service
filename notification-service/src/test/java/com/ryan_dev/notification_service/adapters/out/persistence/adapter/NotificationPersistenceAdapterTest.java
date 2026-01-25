package com.ryan_dev.notification_service.adapters.out.persistence.adapter;

import com.ryan_dev.notification_service.adapters.out.persistence.entity.NotificationEntity;
import com.ryan_dev.notification_service.adapters.out.persistence.mapper.NotificationMapper;
import com.ryan_dev.notification_service.adapters.out.persistence.repository.SpringDataNotificationRepository;
import com.ryan_dev.notification_service.application.domain.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationPersistenceAdapterTest {

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private SpringDataNotificationRepository springDataNotificationRepository;

    @InjectMocks
    private NotificationPersistenceAdapter notificationPersistenceAdapter;

    @Test
    @DisplayName("Given a notification, When save is called, Then should map and save entity")
    void shouldSaveNotification() {
        // Given
        Notification notification = new Notification();
        NotificationEntity entity = new NotificationEntity();
        NotificationEntity savedEntity = new NotificationEntity();
        Notification savedNotification = new Notification();

        when(notificationMapper.toNotificationEntity(notification)).thenReturn(entity);
        when(springDataNotificationRepository.save(entity)).thenReturn(savedEntity);
        when(notificationMapper.toNotification(savedEntity)).thenReturn(savedNotification);

        // When
        Notification result = notificationPersistenceAdapter.save(notification);

        // Then
        assertNotNull(result);
        assertEquals(savedNotification, result);
        verify(notificationMapper).toNotificationEntity(notification);
        verify(springDataNotificationRepository).save(entity);
        verify(notificationMapper).toNotification(savedEntity);
    }

    @Test
    @DisplayName("Given a transaction ID, When existsByTransactionId is called, Then should return repository result")
    void shouldCheckIfTransactionExists() {
        // Given
        UUID transactionId = UUID.randomUUID();
        when(springDataNotificationRepository.existsByTransactionId(transactionId)).thenReturn(true);

        // When
        boolean exists = notificationPersistenceAdapter.existsByTransactionId(transactionId);

        // Then
        assertTrue(exists);
        verify(springDataNotificationRepository).existsByTransactionId(transactionId);
    }
}
