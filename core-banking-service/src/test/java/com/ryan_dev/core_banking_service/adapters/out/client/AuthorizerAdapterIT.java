package com.ryan_dev.core_banking_service.adapters.out.client;

import com.ryan_dev.core_banking_service.adapters.out.client.authorizer.adapter.AuthorizerAdapter;
import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.domain.exceptions.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(properties = "app.client.authorizer.url=http://localhost:${wiremock.server.port}")
@AutoConfigureWireMock(port = 0)
class AuthorizerAdapterIT {

    @Autowired
    private AuthorizerAdapter authorizerAdapter;

    @Test
    @DisplayName("Should return TRUE when external service authorizes")
    void shouldReturnTrueWhenAuthorized() {
        // --- ARRANGE ---
        stubFor(post(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "status": "AUTHORIZED",
                                    "message": "Transaction authorized successfully"
                                }
                                """)));

        Wallet payer = new Wallet(UUID.randomUUID(), "Ryan", "123", "ryan@email.com", "pass", BigDecimal.TEN);

        // --- ACT ---
        boolean result = authorizerAdapter.authorize(payer, BigDecimal.TEN);

        // --- ASSERT ---
        assertThat(result).isTrue();

        verify(postRequestedFor(urlEqualTo("/")));
    }

    @Test
    @DisplayName("Should activate Circuit Breaker/Fallback when external service fails")
    void shouldActivateFallbackWhenServiceFails() {
        // --- ARRANGE ---
        stubFor(post(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(500)));

        Wallet payer = new Wallet(UUID.randomUUID(), "Ryan", "123", "ryan@email.com", "pass", BigDecimal.TEN);

        // --- ACT & ASSERT ---
        assertThatThrownBy(() -> authorizerAdapter.authorize(payer, BigDecimal.TEN))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo("SERVICE_UNAVAILABLE");
    }
}