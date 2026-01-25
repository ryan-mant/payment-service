package com.ryan_dev.core_banking_service.application.service;

import com.ryan_dev.core_banking_service.adapters.out.message.dto.TransferEvent;
import com.ryan_dev.core_banking_service.application.domain.Transfer;
import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.domain.exceptions.BusinessException;
import com.ryan_dev.core_banking_service.application.domain.exceptions.TransferAlreadyExistsException;
import com.ryan_dev.core_banking_service.application.ports.in.transfer.commands.TransferCommand;
import com.ryan_dev.core_banking_service.application.ports.in.transfer.TransferUseCase;
import com.ryan_dev.core_banking_service.application.ports.out.AuthorizerPort;
import com.ryan_dev.core_banking_service.application.ports.out.SendNotificationPort;
import com.ryan_dev.core_banking_service.application.ports.out.TransferRepositoryPort;
import com.ryan_dev.core_banking_service.application.ports.out.WalletRepositoryPort;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransferUseCaseImpl implements TransferUseCase {

    private final WalletRepositoryPort walletRepositoryPort;
    private final TransferRepositoryPort transferRepositoryPort;
    private final AuthorizerPort authorizerPort;
    private final SendNotificationPort sendNotificationPort;
    private static final Logger logger = LoggerFactory.getLogger(TransferUseCaseImpl.class);


    public TransferUseCaseImpl(WalletRepositoryPort walletRepositoryPort, TransferRepositoryPort transferRepositoryPort, AuthorizerPort authorizerPort, SendNotificationPort sendNotificationPort) {
        this.walletRepositoryPort = walletRepositoryPort;
        this.transferRepositoryPort = transferRepositoryPort;
        this.authorizerPort = authorizerPort;
        this.sendNotificationPort = sendNotificationPort;
    }

    @Override
    @Transactional
    public void performTransfer(TransferCommand command) {

        if (transferRepositoryPort.exists(command.id())) {
            logger.warn("Transfer already exists with ID: {}.", command.id());
            throw new TransferAlreadyExistsException("TRANSFER_ALREADY_EXISTS", "Transfer already exists.");
        }

        Wallet payer = walletRepositoryPort.findById(command.payerId())
                .orElseThrow(() -> new RuntimeException("Payer not found"));

        Wallet payee = walletRepositoryPort.findById(command.payeeId())
                .orElseThrow(() -> new RuntimeException("Payee not found"));


        boolean authorized = authorizerPort.authorize(payer, command.amount());

        if (!authorized) {
            throw new BusinessException("TRANSFER_REJECTED", "Transfer rejected by the authorizer.");
        }

        payer.debit(command.amount());
        payee.credit(command.amount());

        walletRepositoryPort.save(payer);
        walletRepositoryPort.save(payee);

        Transfer transfer = new Transfer(command.id(), payer, payee, command.amount());
        UUID savedTransferId = transferRepositoryPort.save(transfer).getId();

        TransferEvent event = new TransferEvent(
                savedTransferId,
                payer.getEmail(),
                payee.getEmail(),
                command.amount()

        );
        sendNotificationPort.send(event);
    }
}