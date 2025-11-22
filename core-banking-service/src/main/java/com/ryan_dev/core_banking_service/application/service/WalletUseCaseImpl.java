package com.ryan_dev.core_banking_service.application.service;

import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.ports.in.wallet.WalletUseCase;
import com.ryan_dev.core_banking_service.application.ports.in.wallet.commands.CreateWalletCommand;
import com.ryan_dev.core_banking_service.application.ports.out.WalletRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletUseCaseImpl implements WalletUseCase {
    private final WalletRepositoryPort walletRepositoryPort;

    @Override
    @Transactional
    public Wallet createWallet(CreateWalletCommand command) {
        Wallet newWallet = new Wallet(
                null,
                command.fullName(),
                command.cpfCnpj(),
                command.email(),
                command.password(),
                BigDecimal.ZERO
        );
        return walletRepositoryPort.save(newWallet);
    }

}
