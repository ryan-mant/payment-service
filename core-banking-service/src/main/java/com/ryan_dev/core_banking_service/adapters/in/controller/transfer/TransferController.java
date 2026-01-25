package com.ryan_dev.core_banking_service.adapters.in.controller.transfer;

import com.ryan_dev.core_banking_service.adapters.in.controller.transfer.dtos.request.TransferRequestDto;
import com.ryan_dev.core_banking_service.application.ports.in.transfer.commands.TransferCommand;
import com.ryan_dev.core_banking_service.application.ports.in.transfer.TransferUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/transfers")
@RestController
public class TransferController {

    private final TransferUseCase transferUseCase;

    public TransferController(TransferUseCase transferUseCase) {
        this.transferUseCase = transferUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> performTransfer(@RequestBody @Valid TransferRequestDto requestDto) {
        TransferCommand command = new TransferCommand(
                requestDto.id(),
                requestDto.payerId(),
                requestDto.payeeId(),
                requestDto.amount()
        );

        transferUseCase.performTransfer(command);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}