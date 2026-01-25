package com.ryan_dev.core_banking_service.application.service.integration;

import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.ports.in.transfer.TransferUseCase;
import com.ryan_dev.core_banking_service.application.ports.in.transfer.commands.TransferCommand;
import com.ryan_dev.core_banking_service.application.ports.out.AuthorizerPort;
import com.ryan_dev.core_banking_service.application.ports.out.WalletRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TransferConcurrencyIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @MockitoBean
    private AuthorizerPort authorizerPort;

    @Autowired
    private TransferUseCase transferUseCase;

    @Autowired
    private WalletRepositoryPort walletRepositoryPort;

    private final Logger logger = LoggerFactory.getLogger(TransferConcurrencyIT.class);

    @BeforeEach
    void setup() {
        when(authorizerPort.authorize(any(), any())).thenReturn(true);
    }

    @Test
    @DisplayName("Should prevent double spending when concurrent transfers occur")
    void shouldPreventDoubleSpending() {
        // Arrange
        BigDecimal initialBalance = BigDecimal.valueOf(100.00);
        BigDecimal transferAmount = BigDecimal.valueOf(100.00);

        Wallet payer = new Wallet(null, "Payer", "111", "payer@mail.com", "pass", initialBalance);

        payer = walletRepositoryPort.save(payer);

        Wallet payee1 = new Wallet(null, "Payee1", "222", "payee1@mail.com", "pass", BigDecimal.ZERO);
        Wallet payee2 = new Wallet(null, "Payee2", "333", "payee2@mail.com", "pass", BigDecimal.ZERO);

        payee1 = walletRepositoryPort.save(payee1);
        payee2 = walletRepositoryPort.save(payee2);

        TransferCommand tx1 = new TransferCommand(UUID.randomUUID(), payer.getId(), payee1.getId(), transferAmount);
        TransferCommand tx2 = new TransferCommand(UUID.randomUUID(), payer.getId(), payee2.getId(), transferAmount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger optimismLockExceptionCount = new AtomicInteger(0);

        CountDownLatch latch = new CountDownLatch(1);

        // Act

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            try {
                latch.await();
                transferUseCase.performTransfer(tx1);
                successCount.incrementAndGet();
            } catch (ObjectOptimisticLockingFailureException e) {
                optimismLockExceptionCount.incrementAndGet();
            } catch (Exception e) {
                logger.error("An unexpected error occurred: ", e);
            }
        });

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            try {
                latch.await();
                transferUseCase.performTransfer(tx2);
                successCount.incrementAndGet();
            } catch (ObjectOptimisticLockingFailureException e) {
                optimismLockExceptionCount.incrementAndGet();
            } catch (Exception e) {
                logger.error("An unexpected error occurred: ", e);
            }
        });

        latch.countDown();

        CompletableFuture.allOf(future1, future2).join();

        // Assert

        assertThat(successCount.get()).as("Only one transaction should succeed").isEqualTo(1);

        assertThat(optimismLockExceptionCount.get()).as("One transaction should fail due to concurrency").isEqualTo(1);

        Wallet updatedPayer = walletRepositoryPort.findById(payer.getId()).orElseThrow();

        assertThat(updatedPayer.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);

        assertThat(updatedPayer.getVersion()).isEqualTo(1L);
    }
}