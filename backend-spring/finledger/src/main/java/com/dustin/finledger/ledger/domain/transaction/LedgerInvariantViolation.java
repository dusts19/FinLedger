package com.dustin.finledger.ledger.domain;

public class LedgerInvariantViolation extends RuntimeException {
    public LedgerInvariantViolation(String message) {
        super(message);
    }
}
