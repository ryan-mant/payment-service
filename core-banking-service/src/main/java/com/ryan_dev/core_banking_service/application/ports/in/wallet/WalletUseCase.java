package com.ryan_dev.core_banking_service.application.ports.in.wallet;

import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.ports.in.wallet.commands.CreateWalletCommand;

public interface WalletUseCase {
    Wallet createWallet(CreateWalletCommand createWalletCommand);
}
