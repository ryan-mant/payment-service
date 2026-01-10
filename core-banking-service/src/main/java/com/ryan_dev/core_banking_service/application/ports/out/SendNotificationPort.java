package com.ryan_dev.core_banking_service.application.ports.out;

import com.ryan_dev.core_banking_service.adapters.out.message.dto.TransferEvent;

public interface SendNotificationPort {
    void send(TransferEvent event);
}