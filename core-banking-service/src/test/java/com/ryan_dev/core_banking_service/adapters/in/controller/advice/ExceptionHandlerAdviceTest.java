package com.ryan_dev.core_banking_service.adapters.in.controller.advice;

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
}