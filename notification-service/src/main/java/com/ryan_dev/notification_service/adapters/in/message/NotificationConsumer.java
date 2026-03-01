package com.ryan_dev.notification_service.adapters.in.message;

import com.rabbitmq.client.Channel;
import com.ryan_dev.notification_service.adapters.in.message.dto.TransferEvent;
import com.ryan_dev.notification_service.application.ports.in.ProcessNotificationUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NotificationConsumer {

    private final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
    private final ProcessNotificationUseCase processNotificationUseCase;

    public NotificationConsumer(ProcessNotificationUseCase processNotificationUseCase) {
        this.processNotificationUseCase = processNotificationUseCase;
    }
    @RabbitListener(queues = "core-banking.notification.queue", ackMode = "MANUAL")
    public void receive(TransferEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Notification received for transfer ID: {}", event.transactionId());

        try {
            processNotificationUseCase.processNotification(
                    event.transactionId(),
                    event.payerEmail(),
                    event.payeeEmail(),
                    event.amount()
            );

            channel.basicAck(tag, false);
            log.info("Notification processed successfully and ACK sent.");

        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate message detected for transaction ID: {}. Discarding...", event.transactionId());
            channel.basicAck(tag, false);

        } catch (Exception e) {
            log.error("Error processing notification for transaction ID: {}. Requeuing...", event.transactionId(), e);
            channel.basicNack(tag, false, true);
        }
    }
}