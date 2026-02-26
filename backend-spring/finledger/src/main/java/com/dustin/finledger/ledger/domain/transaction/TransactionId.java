package com.dustin.finledger.ledger.domain;

import java.util.UUID;

public record TransactionId(UUID value) {
    public TransactionId {
        if (value == null) {
            throw new IllegalArgumentException("TransactionId cannot be null");
        }
    }
}
