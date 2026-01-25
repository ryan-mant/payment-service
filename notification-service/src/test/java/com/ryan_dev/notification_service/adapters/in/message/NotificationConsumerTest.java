package com.ryan_dev.notification_service.adapters.in.message;

import com.ryan_dev.notification_service.adapters.in.message.dto.TransferEvent;
import com.ryan_dev.notification_service.application.ports.in.ProcessNotificationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private ProcessNotificationUseCase processNotificationUseCase;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    @Test
    @DisplayName("Given a valid event, When receive is called, Then should process notification successfully")
    void shouldReceiveAndProcessEventSuccessfully() {
        // Given
        TransferEvent event = new TransferEvent(
                UUID.randomUUID(),
                "payer@test.com",
                "payee@test.com",
                BigDecimal.TEN
        );

        // When
        notificationConsumer.receive(event);

        // Then
        verify(processNotificationUseCase).processNotification(
                event.transactionId(),
                event.payerEmail(),
                event.payeeEmail(),
                event.amount()
        );
    }

    @Test
    @DisplayName("Given an event, When use case throws exception, Then exception should be propagated")
    void shouldPropagateExceptionWhenUseCaseFails() {
        // Given
        TransferEvent event = new TransferEvent(
                UUID.randomUUID(),
                "payer@test.com",
                "payee@test.com",
                BigDecimal.TEN
        );

        doThrow(new RuntimeException("Processing failed"))
                .when(processNotificationUseCase)
                .processNotification(any(), any(), any(), any());

        // When & Then
        assertThrows(RuntimeException.class, () -> notificationConsumer.receive(event));

        verify(processNotificationUseCase).processNotification(
                event.transactionId(),
                event.payerEmail(),
                event.payeeEmail(),
                event.amount()
        );
    }
}
