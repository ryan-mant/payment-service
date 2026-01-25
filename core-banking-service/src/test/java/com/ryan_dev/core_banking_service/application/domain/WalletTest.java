package com.ryan_dev.core_banking_service.application.domain;

import com.ryan_dev.core_banking_service.application.domain.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WalletTest {

    private Wallet wallet;

    @BeforeEach
    void setup(){
        wallet = new Wallet(UUID.randomUUID(), "Ryan", "123", "ryan@email.com",
                "pass", BigDecimal.valueOf(100.00));
    }

    @Test
    @DisplayName("Should debit successfully when balance is sufficient")
    void shouldDebitSuccessfully() {
        // Given // Arrange
        // When // Act
        wallet.debit(BigDecimal.valueOf(40.00));

        // Then // Assert
        assertThat(wallet.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(60.00));
    }

    @Test
    @DisplayName("Should throw exception when balance is insufficient")
    void shouldThrowExceptionWhenInsufficientFunds() {
        // Given // Arrange
        // Act & Assert
        assertThatThrownBy(() -> wallet.debit(BigDecimal.valueOf(101.00)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Insufficient funds to complete the transaction.")
                .extracting("code").isEqualTo("INSUFFICIENT_FUNDS");
    }

    @Test
    @DisplayName("Should credit successfully")
    void shouldCreditSuccessfully() {
        // Given // Arrange
        // When // Act
        wallet.credit(BigDecimal.valueOf(50.00));

        // Then // Assert
        assertThat(wallet.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
    }

    @Test
    @DisplayName("Should throw exception when invalid amount to credit")
    void shouldThrowExceptionWhenInvalidAmount() {
        // Given // Arrange
        // Act & Assert
        assertThatThrownBy(() -> wallet.credit(BigDecimal.ZERO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("The amount to be credited must be greater than zero.")
                .extracting("code").isEqualTo("INVALID_AMOUNT");
    }
}