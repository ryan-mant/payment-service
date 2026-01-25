package com.ryan_dev.core_banking_service.adapters.in.controller.advice;

import com.ryan_dev.core_banking_service.application.domain.exceptions.TransferAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ExceptionHandlerAdviceTest {

    private final ExceptionHandlerAdvice handler = new ExceptionHandlerAdvice();

    @Test
    @DisplayName("Should handle Optimistic Locking Failure nicely")
    void shouldHandleConcurrencyException() {
        // Arrange
        ObjectOptimisticLockingFailureException ex =
                new ObjectOptimisticLockingFailureException("Row was updated or deleted by another transaction", null);

        // Act
        ProblemDetail problem = handler.handleConcurrencyException(ex);

        // Assert
        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getTitle()).isEqualTo("Version Conflict Exception");
    }

    @Test
    @DisplayName("Should handle Transfer Already Exists Exception nicely")
    void shouldHandleTransferAlreadyExistsException() {
        // Arrange
        TransferAlreadyExistsException ex = new TransferAlreadyExistsException("Transfer already exists", "TRANSFER_ALREADY_EXISTS");

        // Act
        ProblemDetail problem = handler.handleTransferAlreadyExistsException(ex);

        // Assert
        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getTitle()).isEqualTo("Transfer Already Exists Exception");
        assertThat(problem.getDetail()).isEqualTo("Transfer already exists");
        assertThat(problem.getProperties()).isNotNull();
        assertThat(problem.getProperties().get("internal_code")).isEqualTo("TRANSFER_ALREADY_EXISTS");
    }
}