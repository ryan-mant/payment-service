package com.ryan_dev.core_banking_service.application.ports.out;

import com.ryan_dev.core_banking_service.application.domain.Transfer;

import java.util.UUID;

public interface TransferRepositoryPort {
    Transfer save(Transfer transfer);
    boolean exists(UUID id);
}
