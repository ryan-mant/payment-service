package com.ryan_dev.core_banking_service.adapters.in.controller.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryan_dev.core_banking_service.adapters.in.controller.wallet.dtos.request.CreateWalletRequestDto;
import com.ryan_dev.core_banking_service.application.domain.Wallet;
import com.ryan_dev.core_banking_service.application.ports.in.wallet.WalletUseCase;
import com.ryan_dev.core_banking_service.application.ports.in.wallet.commands.CreateWalletCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WalletUseCase walletUseCase;

    @Nested
    @DisplayName("POST /api/v1/wallets")
    class CreateWallet {

        @Test
        @DisplayName("Should return 201 Created when request is valid")
        void shouldCreateWalletWhenValidRequest() throws Exception {
            // Arrange
            var requestDto = new CreateWalletRequestDto(
                    "Ryan", "12345678901", "ryan@email.com", "pass123!");
            var walletId = UUID.randomUUID();
            var createdWallet = new Wallet(
                    walletId,
                    requestDto.fullName(),
                    requestDto.cpfCnpj(),
                    requestDto.email(),
                    requestDto.password(),
                    BigDecimal.ZERO
            );

            when(walletUseCase.createWallet(any(CreateWalletCommand.class))).thenReturn(createdWallet);

            // Act
            ResultActions response = mockMvc.perform(post("/api/v1/wallets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)));

            // Assert
            response.andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.fullName").value(requestDto.fullName()))
                    .andExpect(jsonPath("$.email").value(requestDto.email()));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request is invalid")
        void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
            // Arrange
            var invalidRequest = new CreateWalletRequestDto(
                    null, null, null, null
            );

            // Act
            ResultActions response = mockMvc.perform(post("/api/v1/wallets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)));

            // Assert
            response.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Validation Exception"))
                    .andExpect(jsonPath("$.invalid_params").isArray());
        }
    }
}