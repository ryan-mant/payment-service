package com.ryan_dev.core_banking_service.adapters.out.persistence.repository;

import com.ryan_dev.core_banking_service.adapters.out.persistence.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataWalletRepository extends JpaRepository<WalletEntity, UUID> {
    Optional<WalletEntity> findByCpfCnpj(String cpfCnpj);
}
