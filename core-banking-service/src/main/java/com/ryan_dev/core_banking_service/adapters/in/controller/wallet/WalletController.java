package com.ryan_dev.core_banking_service.adapters.in.controller.wallet;

import com.ryan_dev.core_banking_service.adapters.in.controller.wallet.dtos.request.CreateWalletRequestDto;
import com.ryan_dev.core_banking_service.adapters.in.controller.wallet.dtos.response.CreateWalletResponseDto;
import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.ports.in.wallet.WalletUseCase;
import com.ryan_dev.core_banking_service.application.ports.in.wallet.commands.CreateWalletCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/wallets")
@RestController
public class WalletController {

    private final WalletUseCase walletUseCase;

    public WalletController(WalletUseCase walletUseCase) {
        this.walletUseCase = walletUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateWalletResponseDto> createWallet(@RequestBody @Valid CreateWalletRequestDto requestDto) {

        CreateWalletCommand command = new CreateWalletCommand(
                requestDto.fullName(),
                requestDto.cpfCnpj(),
                requestDto.email(),
                requestDto.password()
        );

        Wallet createdWallet = walletUseCase.createWallet(command);

        CreateWalletResponseDto response = new CreateWalletResponseDto(createdWallet);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}