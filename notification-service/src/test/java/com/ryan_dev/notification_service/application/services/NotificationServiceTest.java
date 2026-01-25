package com.ryan_dev.notification_service.application.services;

import com.ryan_dev.notification_service.application.domain.Notification;
import com.ryan_dev.notification_service.application.domain.NotificationStatus;
import com.ryan_dev.notification_service.application.ports.out.NotificationRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepositoryPort notificationRepositoryPort;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Given a new transaction, When processNotification is called, Then should save notification")
    void shouldProcessNewNotification() {
        // Given
        UUID transactionId = UUID.randomUUID();
        String payerEmail = "payer@test.com";
        String payeeEmail = "payee@test.com";
        BigDecimal amount = BigDecimal.TEN;

        when(notificationRepositoryPort.existsByTransactionId(transactionId)).thenReturn(false);

        // When
        notificationService.processNotification(transactionId, payerEmail, payeeEmail, amount);

        // Then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepositoryPort).save(notificationCaptor.capture());

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(transactionId, capturedNotification.getTransactionId());
        assertEquals(payeeEmail, capturedNotification.getEmail());
        assertEquals(NotificationStatus.SENT, capturedNotification.getStatus());
        assertEquals("Transfer of " + amount + " received from " + payerEmail, capturedNotification.getMessage());
    }

    @Test
    @DisplayName("Given an existing transaction, When processNotification is called, Then should not save notification")
    void shouldNotProcessExistingNotification() {
        // Given
        UUID transactionId = UUID.randomUUID();
        String payerEmail = "payer@test.com";
        String payeeEmail = "payee@test.com";
        BigDecimal amount = BigDecimal.TEN;

        when(notificationRepositoryPort.existsByTransactionId(transactionId)).thenReturn(true);

        // When
        notificationService.processNotification(transactionId, payerEmail, payeeEmail, amount);

        // Then
        verify(notificationRepositoryPort, never()).save(any());
    }
}
