package com.ryan_dev.core_banking_service.application.service;

import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.domain.exceptions.BusinessException;
import com.ryan_dev.core_banking_service.application.ports.in.transfer.commands.TransferCommand; // ✅ Importa do próprio Core
import com.ryan_dev.core_banking_service.application.ports.in.transfer.TransferUseCase;
import com.ryan_dev.core_banking_service.application.ports.out.AuthorizerPort;
import com.ryan_dev.core_banking_service.application.ports.out.WalletRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TransferUseCaseImpl implements TransferUseCase {

    private final WalletRepositoryPort walletRepositoryPort;
    private final AuthorizerPort authorizerPort;

    public TransferUseCaseImpl(WalletRepositoryPort walletRepositoryPort, AuthorizerPort authorizerPort) {
        this.walletRepositoryPort = walletRepositoryPort;
        this.authorizerPort = authorizerPort;
    }

    @Override
    @Transactional
    public void performTransfer(TransferCommand command) {

        Wallet payer = walletRepositoryPort.findById(command.payerId())
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        Wallet payee = walletRepositoryPort.findById(command.payeeId())
                .orElseThrow(() -> new RuntimeException("Payee not found"));

        payer.debit(command.amount());

        boolean authorized = authorizerPort.authorize(payer, command.amount());

        if (!authorized) {
            throw new BusinessException("TRANSFER_REJECTED", "Transfer rejected by the authorizer.");
        }

        payee.credit(command.amount());

        walletRepositoryPort.save(payer);
        walletRepositoryPort.save(payee);
    }
}