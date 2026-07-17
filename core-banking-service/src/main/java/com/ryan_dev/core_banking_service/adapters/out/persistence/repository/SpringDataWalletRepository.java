package com.ryan_dev.core_banking_service.adapters.out.persistence.repository;

import com.ryan_dev.core_banking_service.adapters.out.persistence.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataWalletRepository extends JpaRepository<WalletEntity, UUID> {
    Optional<WalletEntity> findByCpfCnpj(String cpfCnpj);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WalletEntity w WHERE w.id = :id")
    Optional<WalletEntity> findByIdWithLock(@Param("id") UUID id);
}
