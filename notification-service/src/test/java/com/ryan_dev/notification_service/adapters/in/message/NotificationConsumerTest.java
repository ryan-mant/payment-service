package com.ryan_dev.notification_service.adapters.in.message;

import com.rabbitmq.client.Channel;
import com.ryan_dev.notification_service.adapters.in.message.dto.TransferEvent;
import com.ryan_dev.notification_service.application.ports.in.ProcessNotificationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private ProcessNotificationUseCase processNotificationUseCase;

    @Mock
    private Channel channel;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    @Test
    @DisplayName("Given a valid event, When receive is called, Then should process notification successfully and ACK")
    void shouldReceiveAndProcessEventSuccessfully() throws IOException {
        // Given
        TransferEvent event = new TransferEvent(
                UUID.randomUUID(),
                "payer@test.com",
                "payee@test.com",
                BigDecimal.TEN
        );
        long deliveryTag = 1L;

        // When
        notificationConsumer.receive(event, channel, deliveryTag);

        // Then
        verify(processNotificationUseCase).processNotification(
                event.transactionId(),
                event.payerEmail(),
                event.payeeEmail(),
                event.amount()
        );
        verify(channel).basicAck(deliveryTag, false);
    }

    @Test
    @DisplayName("Given a duplicate event, When receive is called, Then should discard and ACK")
    void shouldDiscardAndAckWhenDuplicateMessage() throws IOException {
        // Given
        TransferEvent event = new TransferEvent(
                UUID.randomUUID(),
                "payer@test.com",
                "payee@test.com",
                BigDecimal.TEN
        );
        long deliveryTag = 1L;

        doThrow(new DataIntegrityViolationException("Duplicate entry"))
                .when(processNotificationUseCase)
                .processNotification(any(), any(), any(), any());

        // When
        notificationConsumer.receive(event, channel, deliveryTag);

        // Then
        verify(processNotificationUseCase).processNotification(
                event.transactionId(),
                event.payerEmail(),
                event.payeeEmail(),
                event.amount()
        );
        verify(channel).basicAck(deliveryTag, false);
    }

    @Test
    @DisplayName("Given an event, When processing fails, Then should NACK and requeue")
    void shouldNackAndRequeueWhenProcessingFails() throws IOException {
        // Given
        TransferEvent event = new TransferEvent(
                UUID.randomUUID(),
                "payer@test.com",
                "payee@test.com",
                BigDecimal.TEN
        );
        long deliveryTag = 1L;

        doThrow(new RuntimeException("Processing failed"))
                .when(processNotificationUseCase)
                .processNotification(any(), any(), any(), any());

        // When
        notificationConsumer.receive(event, channel, deliveryTag);

        // Then
        verify(processNotificationUseCase).processNotification(
                event.transactionId(),
                event.payerEmail(),
                event.payeeEmail(),
                event.amount()
        );
        verify(channel).basicNack(deliveryTag, false, true);
    }
}