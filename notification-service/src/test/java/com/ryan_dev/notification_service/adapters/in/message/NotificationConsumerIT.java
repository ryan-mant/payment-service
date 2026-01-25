package com.ryan_dev.notification_service.adapters.in.message;

import com.ryan_dev.notification_service.adapters.in.message.dto.TransferEvent;
import com.ryan_dev.notification_service.application.ports.in.ProcessNotificationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class NotificationConsumerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.12-management");

    @Autowired
    private NotificationConsumer notificationConsumer;

    @MockitoBean
    private ProcessNotificationUseCase processNotificationUseCase;

    @Test
    @DisplayName("Given a valid event, When receive is called, Then should invoke use case")
    void shouldExecuteReceiveMethod() {
        // Given
        TransferEvent event = new TransferEvent(
                UUID.randomUUID(),
                "it@test.com",
                "it@test.com",
                BigDecimal.valueOf(100)
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
}
