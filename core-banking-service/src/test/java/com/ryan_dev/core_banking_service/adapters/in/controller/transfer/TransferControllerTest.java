package com.ryan_dev.core_banking_service.adapters.in.controller.transfer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryan_dev.core_banking_service.adapters.in.controller.transfer.dtos.request.TransferRequestDto;
import com.ryan_dev.core_banking_service.application.domain.exceptions.BusinessException;
import com.ryan_dev.core_banking_service.application.domain.exceptions.TransferAlreadyExistsException;
import com.ryan_dev.core_banking_service.application.ports.in.transfer.TransferUseCase;
import com.ryan_dev.core_banking_service.application.ports.in.transfer.commands.TransferCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransferUseCase transferUseCase;

    @Nested
    @DisplayName("POST /api/v1/transfers")
    class PerformTransfer {

        @Test
        @DisplayName("Should return 200 OK when transfer is successful")
        void shouldTransferAmountWhenValidRequest() throws Exception {
            // Arrange
            var requestDto = new TransferRequestDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN);
            var commandCaptor = ArgumentCaptor.forClass(TransferCommand.class);

            doNothing().when(transferUseCase).performTransfer(any(TransferCommand.class));

            // Act
            ResultActions response = mockMvc.perform(post("/api/v1/transfers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)));

            // Assert
            response.andDo(print())
                    .andExpect(status().isOk());

            verify(transferUseCase).performTransfer(commandCaptor.capture());
            var capturedCommand = commandCaptor.getValue();
            assertThat(capturedCommand.payerId()).isEqualTo(requestDto.payerId());
            assertThat(capturedCommand.payeeId()).isEqualTo(requestDto.payeeId());
            assertThat(capturedCommand.amount()).isEqualByComparingTo(requestDto.amount());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request is invalid")
        void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
            // Arrange
            var invalidRequest = new TransferRequestDto(UUID.randomUUID(), UUID.randomUUID(), null, BigDecimal.TEN);

            // Act
            ResultActions response = mockMvc.perform(post("/api/v1/transfers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)));

            // Assert
            response.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Validation Exception"))
                    .andExpect(jsonPath("$.invalid_params").isArray());
        }

        @Test
        @DisplayName("Should return 422 Unprocessable Entity when a business rule fails")
        void shouldReturnUnprocessableEntityWhenBusinessRuleFails() throws Exception {
            // Arrange
            var validRequest = new TransferRequestDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN);
            var businessException = new BusinessException("INSUFFICIENT_FUNDS", "Insufficient funds to complete the transaction.");

            doThrow(businessException).when(transferUseCase).performTransfer(any(TransferCommand.class));

            // Act
            ResultActions response = mockMvc.perform(post("/api/v1/transfers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)));

            // Assert
            response.andDo(print())
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.title").value("Business Exception"))
                    .andExpect(jsonPath("$.detail").value(businessException.getMessage()))
                    .andExpect(jsonPath("$.internal_code").value(businessException.getCode()));
        }

        @Test
        @DisplayName("Should return 409 Conflict when transfer already exists")
        void shouldReturnConflictWhenTransferAlreadyExists() throws Exception {
            // Arrange
            var validRequest = new TransferRequestDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN);
            var exception = new TransferAlreadyExistsException("Transfer already processed.", "TRANSFER_ALREADY_EXISTS");

            doThrow(exception).when(transferUseCase).performTransfer(any(TransferCommand.class));

            // Act
            ResultActions response = mockMvc.perform(post("/api/v1/transfers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)));

            // Assert
            response.andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Transfer Already Exists Exception"))
                    .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                    .andExpect(jsonPath("$.internal_code").value(exception.getCode()));
        }
    }
}