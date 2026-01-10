package com.ryan_dev.notification_service.application.services;

import com.ryan_dev.notification_service.application.domain.Notification;
import com.ryan_dev.notification_service.application.domain.NotificationStatus;
import com.ryan_dev.notification_service.application.ports.in.ProcessNotificationUseCase;
import com.ryan_dev.notification_service.application.ports.out.NotificationRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class NotificationService implements ProcessNotificationUseCase {

    private final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepositoryPort notificationRepositoryPort;

    public NotificationService(NotificationRepositoryPort notificationRepositoryPort) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }

    @Override
    public void processNotification(UUID transactionId, String payerEmail, String payeeEmail, BigDecimal amount) {
        if (notificationRepositoryPort.existsByTransactionId(transactionId)) {
            log.info("Notification already processed for transaction ID: {}", transactionId);
            return;
        }

        log.info("Sending notification to payer: {}", payerEmail);
        log.info("Sending notification to payee: {}", payeeEmail);

        Notification notification = new Notification();
        notification.setTransactionId(transactionId);
        notification.setEmail(payeeEmail);
        notification.setMessage("Transfer of " + amount + " received from " + payerEmail);
        notification.setStatus(NotificationStatus.SENT);

        notificationRepositoryPort.save(notification);
        log.info("Notification saved for transaction ID: {}", transactionId);
    }
}
