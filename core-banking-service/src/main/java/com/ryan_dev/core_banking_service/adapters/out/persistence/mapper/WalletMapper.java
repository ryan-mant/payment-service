package com.ryan_dev.core_banking_service.adapters.out.persistence.mapper;

import com.ryan_dev.core_banking_service.adapters.out.persistence.entity.WalletEntity;
import com.ryan_dev.core_banking_service.application.domain.Wallet;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public WalletEntity toEntity(Wallet domain) {
        WalletEntity entity = new WalletEntity();
        BeanUtils.copyProperties(domain, entity);
        return entity;
    }

    public Wallet toDomain(WalletEntity entity) {
        Wallet domain = new Wallet();
        BeanUtils.copyProperties(entity, domain);
        return domain;
    }

}
