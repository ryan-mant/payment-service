package com.ryan_dev.core_banking_service.adapters.out.persistence.mapper;

import com.ryan_dev.core_banking_service.adapters.out.persistence.entity.WalletEntity;
import com.ryan_dev.core_banking_service.application.domain.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public WalletEntity toEntity(Wallet domain) {
        if (domain == null) {
            return null;
        }
        return new WalletEntity(
                domain.getId(),
                domain.getFullName(),
                domain.getCpfCnpj(),
                domain.getEmail(),
                domain.getPassword(),
                domain.getBalance(),
                domain.getVersion()
        );
    }

    public Wallet toDomain(WalletEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Wallet(
                entity.getId(),
                entity.getFullName(),
                entity.getCpfCnpj(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getBalance(),
                entity.getVersion()
        );
    }

    public void updateEntityFromDomain(Wallet domain, WalletEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setFullName(domain.getFullName());
        entity.setCpfCnpj(domain.getCpfCnpj());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setBalance(domain.getBalance());
    }

}
