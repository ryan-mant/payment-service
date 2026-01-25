package com.ryan_dev.core_banking_service.adapters.out.persistence.repository;

import com.ryan_dev.core_banking_service.adapters.out.persistence.entity.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataTransferRepository extends JpaRepository<TransferEntity, UUID> {
}
