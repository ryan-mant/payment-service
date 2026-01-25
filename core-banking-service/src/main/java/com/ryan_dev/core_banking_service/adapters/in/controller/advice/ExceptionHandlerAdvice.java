package com.ryan_dev.core_banking_service.adapters.in.controller.advice;

import com.ryan_dev.core_banking_service.application.domain.exceptions.BusinessException;
import com.ryan_dev.core_banking_service.application.domain.exceptions.TransferAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    private final Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid request data."
        );

        problemDetail.setTitle("Validation Exception");
//        problemDetail.setType(URI.create("https://github.com/ryan-mant/payment-service")); IMPLEMENTAR UM LINK COM DOC
        problemDetail.setProperty("timestamp", Instant.now());

        List<InvalidParam> invalidParams = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new InvalidParam(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        problemDetail.setProperty("invalid_params", invalidParams);

        return problemDetail;
    }

    record InvalidParam(String name, String reason) {}

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());

        problemDetail.setTitle("Business Exception");
//        problemDetail.setType(URI.create("https://github.com/ryan-mant/payment-service")); IMPLEMENTAR UM LINK COM DOC
        problemDetail.setProperty("timestamp", Instant.now());

        problemDetail.setProperty("internal_code", e.getCode());

        return problemDetail;
    }



    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(Exception ex) {
        logger.error("An unexpected error occurred: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please contact the administrator."
        );

        problemDetail.setTitle("Unexpected Exception");
//        problemDetail.setType(URI.create("https://github.com/ryan-mant/payment-service")); IMPLEMENTAR UM LINK COM DOC
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleConcurrencyException(ObjectOptimisticLockingFailureException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "The transaction failed due to a concurrency conflict. Please try again."
        );

        problemDetail.setTitle("Version Conflict Exception");
//        problemDetail.setType(URI.create("https://github.com/ryan-mant/payment-service")); IMPLEMENTAR UM LINK COM DOC
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
    
    @ExceptionHandler(TransferAlreadyExistsException.class)
    public ProblemDetail handleTransferAlreadyExistsException(TransferAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );

        problemDetail.setTitle("Transfer Already Exists Exception");
//        problemDetail.setType(URI.create("https://github.com/ryan-mant/payment-service")); IMPLEMENTAR UM LINK COM DOC
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("internal_code", ex.getCode());

        return problemDetail;
    }
}
