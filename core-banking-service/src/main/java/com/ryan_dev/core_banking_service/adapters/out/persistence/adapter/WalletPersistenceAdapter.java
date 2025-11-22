package com.ryan_dev.core_banking_service.adapters.out.persistence.adapter;


import com.ryan_dev.core_banking_service.adapters.out.persistence.entity.WalletEntity;
import com.ryan_dev.core_banking_service.adapters.out.persistence.mapper.WalletMapper;
import com.ryan_dev.core_banking_service.adapters.out.persistence.repository.SpringDataWalletRepository;
import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.ports.out.WalletRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WalletPersistenceAdapter implements WalletRepositoryPort {

    private final SpringDataWalletRepository dataWalletRepository;
    private final WalletMapper mapper;

    @Override
    public Optional<Wallet> findById(UUID id) {
        return dataWalletRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Wallet> findByCpfCnpj(String cpfCnpj) {
        return dataWalletRepository.findByCpfCnpj(cpfCnpj)
                .map(mapper::toDomain);
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletEntity entity = mapper.toEntity(wallet);
        WalletEntity saved = dataWalletRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
