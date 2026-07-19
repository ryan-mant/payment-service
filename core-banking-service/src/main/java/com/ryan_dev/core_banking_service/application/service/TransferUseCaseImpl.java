package com.ryan_dev.core_banking_service.application.service;

import com.ryan_dev.core_banking_service.adapters.out.message.dto.TransferEvent;
import com.ryan_dev.core_banking_service.application.domain.Transfer;
import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.domain.exceptions.BusinessException;
import com.ryan_dev.core_banking_service.application.domain.exceptions.TransferAlreadyExistsException;
import com.ryan_dev.core_banking_service.application.ports.in.transfer.commands.TransferCommand;
import com.ryan_dev.core_banking_service.application.ports.in.transfer.TransferUseCase;
import com.ryan_dev.core_banking_service.application.ports.out.AuthorizerPort;
import com.ryan_dev.core_banking_service.application.ports.out.TransferRepositoryPort;
import com.ryan_dev.core_banking_service.application.ports.out.WalletRepositoryPort;
import com.ryan_dev.core_banking_service.application.domain.exceptions.InsufficientBalanceException;
import com.ryan_dev.core_banking_service.application.domain.OutboxEvent;
import com.ryan_dev.core_banking_service.application.ports.out.OutboxRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransferUseCaseImpl implements TransferUseCase {

    private final WalletRepositoryPort walletRepositoryPort;
    private final TransferRepositoryPort transferRepositoryPort;
    private final AuthorizerPort authorizerPort;
    private final TransactionTemplate transactionTemplate;
    private final OutboxRepositoryPort outboxRepositoryPort;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(TransferUseCaseImpl.class);


    public TransferUseCaseImpl(WalletRepositoryPort walletRepositoryPort,
                               TransferRepositoryPort transferRepositoryPort,
                               AuthorizerPort authorizerPort,
                               TransactionTemplate transactionTemplate,
                               OutboxRepositoryPort outboxRepositoryPort,
                               ObjectMapper objectMapper) {
        this.walletRepositoryPort = walletRepositoryPort;
        this.transferRepositoryPort = transferRepositoryPort;
        this.authorizerPort = authorizerPort;
        this.transactionTemplate = transactionTemplate;
        this.outboxRepositoryPort = outboxRepositoryPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public void performTransfer(TransferCommand command) {

        if (transferRepositoryPort.exists(command.id())) {
            logger.warn("Transfer already exists with ID: {}.", command.id());
            throw new TransferAlreadyExistsException("TRANSFER_ALREADY_EXISTS", "Transfer already exists.");
        }

        boolean payerFirst = command.payerId().compareTo(command.payeeId()) < 0;
        UUID firstId = payerFirst ? command.payerId() : command.payeeId();
        UUID secondId = payerFirst ? command.payeeId() : command.payerId();

        transactionTemplate.executeWithoutResult(status -> {
            Wallet dbFirst = walletRepositoryPort.findByIdWithLock(firstId)
                    .orElseThrow(() -> new RuntimeException(firstId.equals(command.payerId()) ? "Payer not found" : "Payee not found"));
            Wallet dbSecond = walletRepositoryPort.findByIdWithLock(secondId)
                    .orElseThrow(() -> new RuntimeException(secondId.equals(command.payerId()) ? "Payer not found" : "Payee not found"));

            Wallet dbPayer = dbFirst.getId().equals(command.payerId()) ? dbFirst : dbSecond;
            Wallet dbPayee = dbFirst.getId().equals(command.payeeId()) ? dbFirst : dbSecond;

            if (dbPayer.getBalance().compareTo(command.amount()) < 0) {
                throw new InsufficientBalanceException("INSUFFICIENT_FUNDS", "Insufficient funds to complete the transaction.");
            }

            boolean authorized = authorizerPort.authorize(dbPayer, command.amount());
            if (!authorized) {
                throw new BusinessException("TRANSFER_REJECTED", "Transfer rejected by the authorizer.");
            }

            dbPayer.debit(command.amount());
            dbPayee.credit(command.amount());

            walletRepositoryPort.save(dbPayer);
            walletRepositoryPort.save(dbPayee);

            Transfer transfer = new Transfer(command.id(), dbPayer, dbPayee, command.amount());
            UUID savedTransferId = transferRepositoryPort.save(transfer).getId();

            TransferEvent event = new TransferEvent(
                    savedTransferId,
                    dbPayer.getEmail(),
                    dbPayee.getEmail(),
                    command.amount()
            );

            try {
                String payload = objectMapper.writeValueAsString(event);
                OutboxEvent outboxEvent = new OutboxEvent(
                        savedTransferId,
                        "Transfer",
                        savedTransferId,
                        "TransferCreated",
                        payload,
                        "PENDING",
                        LocalDateTime.now()
                );
                outboxRepositoryPort.save(outboxEvent);
            } catch (JsonProcessingException e) {
                logger.error("Error serializing transfer event for outbox", e);
                throw new RuntimeException("Failed to serialize outbox event", e);
            }
        });
    }
}