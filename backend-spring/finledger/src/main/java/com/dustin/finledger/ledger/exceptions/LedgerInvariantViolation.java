package com.dustin.finledger.ledger.exceptions;

public class LedgerInvariantViolation extends RuntimeException {
    public LedgerInvariantViolation(String message) {
        super(message);
    }
}