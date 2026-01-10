package com.ryan_dev.core_banking_service.adapters.out.persistence.mapper;

import com.ryan_dev.core_banking_service.adapters.out.persistence.entity.TransferEntity;
import com.ryan_dev.core_banking_service.adapters.out.persistence.entity.WalletEntity;
import com.ryan_dev.core_banking_service.application.domain.Transfer;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferMapper {

    private final WalletMapper walletMapper;
    private final EntityManager entityManager;

    public TransferEntity toEntity(Transfer domain) {
        TransferEntity entity = new TransferEntity();
        BeanUtils.copyProperties(domain, entity);
        entity.setPayer(entityManager.getReference(WalletEntity.class, domain.getPayer().getId()));
        entity.setPayee(entityManager.getReference(WalletEntity.class, domain.getPayee().getId()));
        return entity;
    }

    public Transfer toDomain(TransferEntity entity) {
        Transfer domain = new Transfer();
        BeanUtils.copyProperties(entity, domain);
        domain.setPayer(walletMapper.toDomain(entity.getPayer()));
        domain.setPayee(walletMapper.toDomain(entity.getPayee()));
        return domain;
    }
}
