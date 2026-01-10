package com.ryan_dev.notification_service.adapters.in.message;

import com.ryan_dev.notification_service.adapters.in.message.dto.TransferEvent;
import com.ryan_dev.notification_service.application.ports.in.ProcessNotificationUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
    private final ProcessNotificationUseCase processNotificationUseCase;

    public NotificationConsumer(ProcessNotificationUseCase processNotificationUseCase) {
        this.processNotificationUseCase = processNotificationUseCase;
    }

    @RabbitListener(queues = "core-banking.notification.queue")
    public void receive(TransferEvent event) {
        log.info("Notification received for transfer ID: {}", event.transactionId());

        processNotificationUseCase.processNotification(
                event.transactionId(),
                event.payerEmail(),
                event.payeeEmail(),
                event.amount()
        );

        log.info("Notification processed successfully");
    }
}
