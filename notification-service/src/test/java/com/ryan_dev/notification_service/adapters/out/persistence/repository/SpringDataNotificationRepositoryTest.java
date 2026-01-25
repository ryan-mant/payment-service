package com.ryan_dev.notification_service.adapters.out.persistence.repository;

import com.ryan_dev.notification_service.adapters.out.persistence.entity.NotificationEntity;
import com.ryan_dev.notification_service.application.domain.NotificationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Testcontainers
class SpringDataNotificationRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SpringDataNotificationRepository repository;

    @Test
    @DisplayName("Given an existing transaction ID, When existsByTransactionId is called, Then should return true")
    void shouldReturnTrueWhenTransactionExists() {
        // Given
        UUID transactionId = UUID.randomUUID();
        NotificationEntity entity = new NotificationEntity();
        entity.setTransactionId(transactionId);
        entity.setEmail("test@test.com");
        entity.setMessage("Test message");
        entity.setStatus(NotificationStatus.SENT);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        entityManager.persist(entity);
        entityManager.flush();

        // When
        boolean exists = repository.existsByTransactionId(transactionId);

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Given a non-existing transaction ID, When existsByTransactionId is called, Then should return false")
    void shouldReturnFalseWhenTransactionDoesNotExist() {
        // Given
        UUID transactionId = UUID.randomUUID();

        // When
        boolean exists = repository.existsByTransactionId(transactionId);

        // Then
        assertFalse(exists);
    }
}
