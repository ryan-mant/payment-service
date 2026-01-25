package com.ryan_dev.core_banking_service.application.service;

import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.ports.in.wallet.commands.CreateWalletCommand;
import com.ryan_dev.core_banking_service.application.ports.out.WalletRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletUseCaseImplTest {

    @Mock
    private WalletRepositoryPort walletRepositoryPort;

    @InjectMocks
    private WalletUseCaseImpl walletUseCase;

    @Test
    @DisplayName("Should create a wallet successfully")
    void shouldCreateWalletSuccessfully() {
        // Arrange
        CreateWalletCommand command = new CreateWalletCommand("Ryan", "123", "ryan@email.com", "pass");
        Wallet savedWallet = new Wallet(UUID.randomUUID(), command.fullName(), command.cpfCnpj(), command.email(), command.password(), BigDecimal.ZERO);

        when(walletRepositoryPort.save(any(Wallet.class))).thenReturn(savedWallet);

        // Act
        Wallet createdWallet = walletUseCase.createWallet(command);

        // Assert
        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepositoryPort).save(walletCaptor.capture());
        Wallet walletToSave = walletCaptor.getValue();

        assertThat(walletToSave.getId()).isNull();
        assertThat(walletToSave.getFullName()).isEqualTo(command.fullName());
        assertThat(walletToSave.getCpfCnpj()).isEqualTo(command.cpfCnpj());
        assertThat(walletToSave.getEmail()).isEqualTo(command.email());
        assertThat(walletToSave.getPassword()).isEqualTo(command.password());
        assertThat(walletToSave.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);

        assertThat(createdWallet).isNotNull();
        assertThat(createdWallet).isEqualTo(savedWallet);
    }
}