package com.ryan_dev.core_banking_service.adapters.out.client.authorizer.adapter;

import com.ryan_dev.core_banking_service.adapters.out.client.authorizer.client.AuthorizerClient;
import com.ryan_dev.core_banking_service.adapters.out.client.authorizer.request.AuthorizerRequest;
import com.ryan_dev.core_banking_service.adapters.out.client.authorizer.response.AuthorizerResponse;
import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.domain.exceptions.BusinessException;
import com.ryan_dev.core_banking_service.application.ports.out.AuthorizerPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AuthorizerAdapter implements AuthorizerPort {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizerAdapter.class);
    private final AuthorizerClient authorizerClient;

    public AuthorizerAdapter(AuthorizerClient authorizerClient) {
        this.authorizerClient = authorizerClient;
    }

    @Override
    @CircuitBreaker(name = "authorizer", fallbackMethod = "authorizeFallback")
    public boolean authorize(Wallet payer, BigDecimal amount) {
        try {
            logger.info("Requesting authorization for wallet: {}", payer.getId());

            AuthorizerRequest request = new AuthorizerRequest(payer.getCpfCnpj(), amount);
            AuthorizerResponse response = authorizerClient.validate(request);

            return "AUTHORIZED".equalsIgnoreCase(response.status());

        } catch (Exception e) {
            logger.error("Erro ao chamar autorizador externo", e);
            throw e;
        }
    }


    public boolean authorizeFallback(Wallet payer, BigDecimal amount, Throwable t) {
        logger.warn("Circuit Breaker active or communication error. Reason: {}", t.getMessage());

        throw new BusinessException("SERVICE_UNAVAILABLE",
                "The external authorization service is temporarily unavailable. Please try again later.");
    }
}