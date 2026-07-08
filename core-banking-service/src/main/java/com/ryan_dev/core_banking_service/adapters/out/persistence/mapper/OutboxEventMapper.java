package com.ryan_dev.core_banking_service.adapters.out.persistence.mapper;

import com.ryan_dev.core_banking_service.adapters.out.persistence.entity.OutboxEventEntity;
import com.ryan_dev.core_banking_service.application.domain.OutboxEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventMapper {

    public OutboxEventEntity toEntity(OutboxEvent domain) {
        if (domain == null) return null;
        OutboxEventEntity entity = new OutboxEventEntity();
        BeanUtils.copyProperties(domain, entity);
        return entity;
    }

    public OutboxEvent toDomain(OutboxEventEntity entity) {
        if (entity == null) return null;
        OutboxEvent domain = new OutboxEvent();
        BeanUtils.copyProperties(entity, domain);
        return domain;
    }
}
