package com.ryan_dev.core_banking_service.application.ports.out;


import com.ryan_dev.core_banking_service.application.domain.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepositoryPort {

    Optional<Wallet> findById(UUID id);

    Optional<Wallet> findByCpfCnpj(String cpfCnpj);

    Wallet save(Wallet wallet);
}
