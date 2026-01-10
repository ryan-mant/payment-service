package com.ryan_dev.core_banking_service.adapters.out.message;

import com.ryan_dev.core_banking_service.adapters.out.message.dto.TransferEvent;
import com.ryan_dev.core_banking_service.application.ports.out.SendNotificationPort;
import com.ryan_dev.core_banking_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQProducerAdapter implements SendNotificationPort {

    private final RabbitTemplate rabbitTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducerAdapter.class);

    @Override
    public void send(TransferEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, event);
            logger.info("Sent message to RabbitMQ queue");
        } catch (Exception e) {
            logger.error("Error sending message to RabbitMQ queue", e);
        }
    }
}