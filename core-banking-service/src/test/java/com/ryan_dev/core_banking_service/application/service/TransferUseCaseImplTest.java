package com.ryan_dev.core_banking_service.application.service;

import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.ports.in.transfer.commands.TransferCommand;
import com.ryan_dev.core_banking_service.application.ports.out.WalletRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferUseCaseImplTest {

    @Mock
    private WalletRepositoryPort walletRepositoryPort;

    @InjectMocks
    private TransferUseCaseImpl transferUseCase;

    @Test
    @DisplayName("Should perform transfer successfully")
    void shouldPerformTransferSuccessfully() {
        // Arrange
        UUID payerId = UUID.randomUUID();
        UUID payeeId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100.00);

        Wallet payer = new Wallet(payerId, "Payer", "111", "payer@mail.com", "pass", BigDecimal.valueOf(200.00));
        Wallet payee = new Wallet(payeeId, "Payee", "222", "payee@mail.com", "pass", BigDecimal.valueOf(50.00));

        when(walletRepositoryPort.findById(payerId)).thenReturn(Optional.of(payer));
        when(walletRepositoryPort.findById(payeeId)).thenReturn(Optional.of(payee));

        TransferCommand command = new TransferCommand(payerId, payeeId, amount);

        // Act
        transferUseCase.performTransfer(command);

        // Assert
        verify(walletRepositoryPort).save(payer);
        verify(walletRepositoryPort).save(payee);
    }

    @Test
    @DisplayName("Should fail when payer not found")
    void shouldFailWhenPayerNotFound() {
        // Arrange
        UUID payerId = UUID.randomUUID();
        TransferCommand command = new TransferCommand(payerId, UUID.randomUUID(), BigDecimal.TEN);

        // Act
        when(walletRepositoryPort.findById(payerId)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> transferUseCase.performTransfer(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Payer not found");
    }
}