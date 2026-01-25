package com.ryan_dev.core_banking_service.application.ports.in.transfer;


import com.ryan_dev.core_banking_service.application.ports.in.transfer.commands.TransferCommand;

public interface TransferUseCase {

    void performTransfer(TransferCommand requestDto);
}
