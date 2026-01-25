package com.ryan_dev.core_banking_service.application.ports.out;

import com.ryan_dev.core_banking_service.application.domain.Wallet;
import java.math.BigDecimal;

public interface AuthorizerPort {
    boolean authorize(Wallet payer, BigDecimal amount);
}