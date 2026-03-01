package com.ryan_dev.notification_service.application.ports.out;

import com.ryan_dev.notification_service.application.domain.Notification;

public interface NotificationRepositoryPort {

    Notification save(Notification notification);
}
