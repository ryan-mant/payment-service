package com.ryan_dev.core_banking_service.adapters.out.persistence.adapter;

import com.ryan_dev.core_banking_service.adapters.out.persistence.entity.TransferEntity;
import com.ryan_dev.core_banking_service.adapters.out.persistence.mapper.TransferMapper;
import com.ryan_dev.core_banking_service.adapters.out.persistence.repository.SpringDataTransferRepository;
import com.ryan_dev.core_banking_service.application.domain.Transfer;
import com.ryan_dev.core_banking_service.application.ports.out.TransferRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TransferPersistenceAdapter implements TransferRepositoryPort {

    private final SpringDataTransferRepository dataTransferRepository;
    private final TransferMapper mapper;

    @Override
    public Transfer save(Transfer transfer) {
        TransferEntity entity = mapper.toEntity(transfer);
        TransferEntity saved = dataTransferRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean exists(UUID id) {
        return dataTransferRepository.existsById(id);
    }
}
