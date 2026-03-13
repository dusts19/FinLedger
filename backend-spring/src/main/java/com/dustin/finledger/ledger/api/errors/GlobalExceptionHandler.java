package com.dustin.finledger.ledger.api.errors;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dustin.finledger.common.exceptions.DomainException;
import com.dustin.finledger.ledger.domain.journal.JournalInvariantViolation;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {

        ErrorResponse error = new ErrorResponse(
            "DOMAIN_ERROR",
            ex.getMessage(),
            Instant.now()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(JournalInvariantViolation.class)
    public ResponseEntity<ErrorResponse> handleJournalInvariantViolation(JournalInvariantViolation ex) {

        ErrorResponse error = new ErrorResponse(
            "JOURNAL_INVARIANT_VIOLATION",
            ex.getMessage(),
            Instant.now()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        ErrorResponse error = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "Unexpected error occurred",
            Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}