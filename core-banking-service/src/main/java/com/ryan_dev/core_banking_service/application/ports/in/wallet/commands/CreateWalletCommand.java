package com.ryan_dev.core_banking_service.application.ports.in.wallet.commands;


public record CreateWalletCommand(
        String fullName,
        String cpfCnpj,
        String email,
        String password
) {
}
