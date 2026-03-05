package com.dustin.finledger.ledger.domain.journal;

public class JournalInvariantViolation extends RuntimeException {
    public JournalInvariantViolation(String message) {
        super(message);
    }
}
