package com.ryan_dev.core_banking_service.application.service;

import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.ports.in.transfer.commands.TransferCommand;
import com.ryan_dev.core_banking_service.application.ports.out.AuthorizerPort;
import com.ryan_dev.core_banking_service.application.ports.out.SendNotificationPort;
import com.ryan_dev.core_banking_service.application.ports.out.TransferRepositoryPort;
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

    @Mock
    private AuthorizerPort authorizerPort;

    @Mock
    private TransferRepositoryPort transferRepositoryPort;

    @Mock
    private SendNotificationPort sendNotificationPort;



    @InjectMocks
    private TransferUseCaseImpl transferUseCase;

    @Test
    @DisplayName("Should perform transfer successfully")
    void shouldPerformTransferSuccessfully() {
        // Arrange
        UUID payerId = UUID.randomUUID();
        UUID payeeId = UUID.randomUUID();
        UUID transferId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100.00);

        Wallet payer = new Wallet(payerId, "Payer", "111", "payer@mail.com", "pass", BigDecimal.valueOf(200.00));
        Wallet payee = new Wallet(payeeId, "Payee", "222", "payee@mail.com", "pass", BigDecimal.valueOf(50.00));

        when(walletRepositoryPort.findById(payerId)).thenReturn(Optional.of(payer));
        when(walletRepositoryPort.findById(payeeId)).thenReturn(Optional.of(payee));
        when(authorizerPort.authorize(any(), any())).thenReturn(true);
        when(transferRepositoryPort.exists(any())).thenReturn(false);
        when(transferRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));


        TransferCommand command = new TransferCommand(transferId, payerId, payeeId, amount);

        transferUseCase.performTransfer(command);

        // Assert
        verify(walletRepositoryPort).save(payer);
        verify(walletRepositoryPort).save(payee);
        verify(transferRepositoryPort).save(any());
    }

    @Test
    @DisplayName("Should fail when payer not found")
    void shouldFailWhenPayerNotFound() {
        // Arrange
        UUID payerId = UUID.randomUUID();
        UUID transferId = UUID.randomUUID();
        UUID payeeId = UUID.randomUUID();
        TransferCommand command = new TransferCommand(transferId, payerId, payeeId, BigDecimal.TEN);

        // Act
        when(walletRepositoryPort.findById(payerId)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> transferUseCase.performTransfer(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Payer not found");
    }
}