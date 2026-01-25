package com.ryan_dev.core_banking_service.adapters.in.controller.wallet.dtos.response;

import com.ryan_dev.core_banking_service.application.domain.Wallet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWalletResponseDto {
    private String fullName;
    private String email;

    public CreateWalletResponseDto(Wallet wallet) {
        this.fullName = wallet.getFullName();
        this.email = wallet.getEmail();
    }
}
